Contents
	1) Introduction
	2) License
	3) Build
	4) Code
	5) Tools

1) Introduction
Mac OSX/Windows/Linux cross platform Java template. Able to produce cross platform jars as well as Windows .exe and Mac OSX .app files. The aim is to support a single jar that can be run on versions of java 1.4 and above on any system, as well as an exe and app wrapper around that jar.

2) License
This template is provided as is with no warranty what so ever. Additional tools supplied under their respective licenses.

3) Build
There are three build modes supported:
	1. One-Jar - Single Jar, with library jars inside
	2. Include - Extract and include library jars within main jar
	3. Seperate - Your code in one jar, library jars seperate
	
1. and 2. have the advantage that your application is contained withn a single jar. 1. keeps things neater within the jar and enables a single jar where it may not be possible using 2. as sometimes library jars cannot be extracted due to license requirements or being sealed. 3. leaves library jars separate meaning you cannot distribute a single jar for your application, but require to distribute the whole directory structure (eg. in the form of a zip file), however the app and exe produced still allow you to distribute a single bundle.

Provided your code is cross platform, the resulting jars should be runable on almost any java system.

All ant tasks should be runnable on almost any operating system. Although There is also an OSX .dmg creation task within the build file. This is currently only available on Mac OSX until a cross platform alternative can be found.

4) Code

5) Tools
The following tools are included to make a Java application integrate better on the various supported operating systems:
	One-Jar http://one-jar.sourceforge.net
		- Create jars within a jar
	JarBundler	http://informagen.com/JarBundler
		- Create OSX .app from jar
	JSmooth http://jsmooth.sourceforge.net
		- Create Windows .exe from a jar
	AppleJavaExtensions http://developer.apple.com/samplecode/AppleJavaExtensions/index.html
		- Allow complication of apple specific code on non apple platforms

These are all supplied with this template, because the idea of this template was that it would 'just work', with no configuration necessary. These are defined explicitly as ant tasks within the and build file. They can be made generic for Ant as a whole by putting the various jars in the Ant lib directory:
	Eclipse - Eclipse->Preferences->Ant->Runtime->Add External Jar
	OSX - /Developer/Java/Ant/Lib
	Ant - {Ant}/Lib

The bundled tools are all 100% original, apart from two small code changes in One-Jar. One of these is to get One-Jar to compile under Java 1.5, the other is to get it to work when the are additional elements in the classpath other than the main jar that you want to run. I have also added a custom ant task to insert some of the properties from build.xml into the JSmooth config file so they don't have to be maintained in two places.