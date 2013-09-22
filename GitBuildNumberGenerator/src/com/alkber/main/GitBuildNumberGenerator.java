package com.alkber.main;
/**
 * GitBuildNumberGenerator
 * @author Althaf K Backer <althafkbacker@gmail.com>
 * @blog blog.althafkbacker.com
 * 
 * 15/Sep/2013
 * 
 * This tool generate version or build information in a source file format from 
 * a pointing local git repository, during the pre-build phase. Currently only 
 * supports java code generation. However can be extended to a more generic 
 * plugin based system to support other programming languages.
 * 
 * Tool favors command line arguments instead of configuration files to read the 
 * parameters, this decision is to speed up the tool, as this tool may be run 
 * numbers of times in a time slice.
 * 
 * You can use this in your build script in pre build phase, use in eclipse 
 * without a build script is just a use case. Policy of when to run is left to 
 * your creativity.
 * 
 * gbn git-repo-directory version-file-directory package-name
 * > git-repo-directory 	: Absolute path to local .git directory
 * > version-file-directory : Absolute path to the directory, to place version source file
 * > package-name 			: Java package name 
 * 
 * eg: gbn /home/myhome/gbn/.git /home/myhome/gbn/src/com/alkber/gbn/version/ com.alkber.gbn.version
 * 
 * NOTE: It is highly advised to place .gitingore in the version-file-directory
 * with .gitingnore content being 'VersionInfo.java'. As this file is updated 
 * frequently, it may cause git issues during commit and merge.Further to avoid
 * git issues ignore the *class files. 
 * 
 * Does it make sense to git ignore VersionInfo.java ?
 * 
 * Yes, tool autogenerate your versionInfo.java when ever you build and what ever
 * branch you are in, and during compilation already version information in
 * embedded into the binary.
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ru.concerteza.util.buildnumber.BuildNumber;
import ru.concerteza.util.buildnumber.BuildNumberExtractor;

//TODO to append platform specific 'directory or file separator' at the end of 
//version-file-directory if user hasn't specified
public class GitBuildNumberGenerator {

	private String javaPackage;
	private String versionFileDirectory;
	private String gitRepoLocation;
	private String versionFile;
	private BuildNumber buildNumberHelper;
	
	private static final Logger LOGGER = Logger.getLogger(GitBuildNumberGenerator.class.getName()); 
	private FileHandler logFileHandler;
	
	public static void main(String args[]) {
		
		LOGGER.setLevel(Level.INFO); 
		
		if (args.length < 3) {
			
			LOGGER.info("\ngbn git-repo-directory version-file-directory " +
					"package-name \n" +
					"eg: gbn /home/myhome/gbn/.git " +
					"/home/myhome/gbn/src/com/alkber/gbn/version/ " +
					"com.alkber.gbn.version \n" +
					"A version file called \"VersionInfo.java\" " +
					"is created in the version-file-directory");
			
			LOGGER.severe("\nError: Insufficient parameters");
			System.exit(0);
			
		}
		
		GitBuildNumberGenerator gbn = new GitBuildNumberGenerator(args[0], 
				args[1], args[2], null, null);
		gbn.writeVersionInformation();
		
	}
	
	public GitBuildNumberGenerator(String gitRepoLocation, 
			String sourceFileLocation, 
			String javaPackage,
			String versionFile,
			String logFileLocation) {
	
		if (logFileLocation != null) {
			
			try {
				
				logFileHandler = new FileHandler(logFileLocation);
				logFileHandler.setFormatter(new SimpleFormatter());
				LOGGER.addHandler(logFileHandler);
				
			} catch (SecurityException | IOException e) {
				
				System.out.println(e.toString());
				
			}
			

		}

		try {
			
			this.versionFileDirectory = sourceFileLocation;
			this.gitRepoLocation = gitRepoLocation;
			this.javaPackage = javaPackage;
			this.versionFile = (versionFile == null) ? "VersionInfo.java" : versionFile;
			
			buildNumberHelper  = BuildNumberExtractor.extract(new File(gitRepoLocation));
			buildNumberHelper.getRevision();
			
		} catch (IOException ioe) {
		
			LOGGER.severe(ioe.toString());

		}
		
		
	}
	
	public void writeVersionInformation() {
	
		if (buildNumberHelper != null) {
			
			String fileContent =
			"package " + javaPackage + ";\n"
			+"/*\n" 
			+" * This is an auto generated file, modifications will not persist.\n"
			+" * "+((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()))
			+"\n */ \n"
			+"public class VersionInfo {\n" + "															\n"
					+ "	public static final String buildNumber = \""
					+ buildNumberHelper.defaultBuildnumber() + "\";\n"
					+ "	public static final String branch = \""
					+ buildNumberHelper.getBranch() + "\";\n"
					+ "	public static final String commit = \""
					+ buildNumberHelper.getCommitsCount() + "\";\n"
					+ "	public static final String version = \""
					+ buildNumberHelper.getRevision() + "\";\n"
					+ "	public static final String shortVersion = \""
					+ buildNumberHelper.getShortRevision() + "\";\n"
					+ "															\n" + "}\n";
			try {

				File sourceFile = new File(versionFileDirectory+versionFile);

				if (!sourceFile.exists()) {

					sourceFile.createNewFile();

				}

				FileWriter fw = new FileWriter(sourceFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(fileContent);
				bw.close();
				
				LOGGER.info(versionFile + " generated successfully at " + versionFileDirectory);

			} catch (IOException ioe) {

				 LOGGER.severe(ioe.toString() + " " + versionFileDirectory);
				
			}
		}
				
	}
}