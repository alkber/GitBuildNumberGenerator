GitBuildNumberGenerator
=======================

[Inspitration](http://stackoverflow.com/questions/18800062/eclipse-git-plugin-and-a-method-to-fetch-current-branch-commit-id-to-java-code)
--------------

About
-----

This tool generate version or build information in a source file format from 
a pointing local git repository, during the pre-build phase. Currently only 
supports java code generation. However can be extended to a more generic 
plugin based system to support other programming languages.
 
Tool favors command line arguments instead of configuration files to read the 
parameters, this decision is to speed up the tool, as this tool may be run 
numbers of times in a time slice.
 
You can use this in your build script in pre build phase, use in eclipse 
without a build script is just a use case. Policy of when to run is left to 
your creativity.
 
> gbn git-repo-directory version-file-directory package-name

> git-repo-directory 	: Absolute path to local .git directory

> version-file-directory : Absolute path to the directory, to place version source file

> package-name 	: Java package name 

  
eg: gbn /home/myhome/gbn/.git /home/myhome/gbn/src/com/alkber/gbn/version/ com.alkber.gbn.version

<pre><code>
- VersionInfo.java -
package com.alkber.gbn.version;
/*
 * This is an auto generated file, modifications will not persist.
 * 2013/09/22 08:48:52
 */ 
public class VersionInfo {
															
	public static final String buildNumber = "master.11.71740cc";
	public static final String branch = "master";
	public static final String commit = "11";
	public static final String version = "71740cc639811a5c4249f4344fe5e8586a7a494b";
	public static final String shortVersion = "71740cc";
															
}
- VersionInfo.java -
</code></pre>

NOTE: It is highly advised to place .gitingore in the version-file-directory
with .gitingnore content being 'VersionInfo.java'. As this file is updated 
frequently, it may cause git issues during commit and merge.Further to avoid
git issues ignore the *class files. 

[Download gbn.jar](https://github.com/alkber/GitBuildNumberGenerator/blob/master/GitBuildNumberGenerator/build/gbn.jar)
------------------

Does it make sense to gitignore VersionInfo.java ?
-------------------------------------------------------------

Yes, tool autogenerate your versionInfo.java when ever you build and what ever
branch you are in, and during compilation already version information in
embedded into the binary.

Simplest UseCase
---------------------

In eclipse, Goto Project->Properties->Builders , create a new builder

>Location:

>[ path to java binary ] 

>eg: [ /home/myhome/bin/dk1.7.0_25/bin/java ]


>Arguments: 

>[ arguments to java binary]

>eg: -jar /home/myhome/bin/gbn.jar /home/myhome/git/Click2Limo/.git /home/myhome/git/Click2Limo/Click2Limo/src/com/frooday/click2limo/version/ com.frooday.click2limo.version


In the refresh tab of new builder setting specifiy the resource ie our version source file 
to be refeshed when ever tool is run.

In Build Options tab, 

Enable-

*Allocate Console

*During Manual Build

*During Auto Build

*During Clean


Dependency
----------

* [JGit 3.0.0.201306101825] (http://download.eclipse.org/jgit/maven/org/eclipse/jgit/org.eclipse.jgit/3.0.0.201306101825-r/org.eclipse.jgit-3.0.0.201306101825-r.jar)
* This project uses code from [jgit-buildnumber] https://github.com/alx3apps/jgit-buildnumber

How different is GitBuildNumberGenerator from JGit-Buildnumber ?
----------------------------------------------------------------

Both actually does the same thing, however, we have detached policy from they tool
and given the choice of policy to the developer. So, he can use it in any build
environment like Makefile, Gradle etc, further we generate a source file.

License information
-------------------
This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
