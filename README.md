# Gradle Coverity Plugin

A plugin for integrating with [Coverity](http://www.coverity.com), a static
analysis platform.  Adds tasks to the project that will emit, analyze, and
commit data using Coverity analysis tools that are installed on the user's
machine.

Basically this will automatically determine the arguments for
```cov-emit-java``` and run that after building instead of using ```cov-build```
to wrap your build.  Emits are broken up by source set (Java) or variant
(Android) in case your configuration might have multiple versions of a full
package.name.ClassName.  Emits are followed up with ```cov-analyze-java```, then
finally ```cov-commit-defects```.

## Disclaimer

Use at your own risk.  I cannot guarantee support, so be ready to fork and make
changes.

MIT License.  That way neither of us have to worry about that kind of stuff too
much.

## Compatibility

* Gradle 2.0+
* Java projects
* Android projects with Gradle build tools 1.0.0+
  * Note: bumps Gradle requirement to 2.2.1+
* Can analyze projects with child projects
  * Root and child projects can be mixed Java and Android
* Tested with the following versions of Coverity Analysis Tools and Coverity Connect:
  * 7.0.3
  * 7.7.0
  * Anything in between probably works
  * No planned support for other versions

## Instructions

### Usage

It's Gradle.  You know the drill.  Maybe you don't.  So before we get to
configuring the plugin, you need to get the plugin applied to your Gradle build
script.

This plugin is published to the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.github.mjdetullio.gradle.coverity),
so you can use the Gradle 2.1+ plugin mechanism.

Here's one way you can do it by specifying it in your root Gradle file:

```
// Generally this is at the top
buildscript {
    repositories {
        maven {
            url 'https://plugins.gradle.org/m2/'
        }
    }
    dependencies {
        classpath 'gradle.plugin.com.github.mjdetullio.gradle:coverity-plugin:VERSION'
    }
}

apply plugin: 'com.github.mjdetullio.gradle.coverity'
```

Now you're ready to configure.  Emit and analyze are pretty much plug-n-play
once you set your ```COVERITY_HOME``` environment variable.  Commit can be
configured with environment variables.  Reference the below defaults for those
variables, or set them in your Gradle file.

Here's all the optional settings for your root Gradle file:

```
// Most settings here are only valid on the root project 
coverity {
    // Default values shown here
    // You only have to add them if you wish to override

    // Directory Coverity uses during emit/analyze/commit
    intermediateDir = "${project.buildDir}/coverity/intermediate"

    // Path to strip from the beginning of each file's absolute path during analyze
    stripPath = project.projectDir

    // Path to your Coverity Analysis Tools root directory (leave "bin" off the end)
    // If env var is not set and you don't set this, it assumes "${COVERITY_HOME}/bin" is on your PATH
    coverityHome = System.getenv('COVERITY_HOME')

    // Coverity Connect settings used during the commit phase
    stream = System.getenv('COVERITY_STREAM')
    host = System.getenv('COVERITY_HOST')
    // Port or dataport is supported -- dataport is favored (port is used if dataport is unspecified)
    port = System.getenv('COVERITY_PORT')
    dataport = System.getenv('COVERITY_DATAPORT')
    user = System.getenv('COVERITY_USER')
    pass = System.getenv('COVERITY_PASS')

    // Whether child projects (recursive) should be part of the analysis
    includeChildProjects = true

    // Setting this to true will not skip child projects
    skip = false
}

// You can add more args to the external executions for each of the added tasks
// Again, each block is optional
covEmitJava {
    additionalArgs = ['--arg-name', 'value', 'argA', 'argB', 'etc']
}

covAnalyzeJava {
    additionalArgs = ['--arg-name', 'value', 'argA', 'argB', 'etc']
}

covCommitDefects {
    additionalArgs = ['--arg-name', 'value', 'argA', 'argB', 'etc']
}
```

Optional settings for child projects:

```
// Do not apply the plugin in child projects!
// Just add the block
coverity {
    // Default values shown here
    // You only have to add them if you wish to override

    // Whether child projects (recursive) should be part of the analysis
    includeChildProjects = true

    // Setting this to true will not skip child projects
    skip = false
}
```

Now you're ready to do an analysis.  The task dependency tree for this plugin
looks like this:

```
covEmitJava <- covAnalyzeJava <- covCommitDefects
```

So... just run this to build your app and do all those tasks for you in order:

```
./gradlew assemble covCommitDefects
```

### Development

JDK 1.6 required, since that's what Gradle builds against.

IntelliJ recommended.  Import project from the ```build.gradle``` file.

### Build

To create a JAR that can be included on your Gradle build script's classpath,
run the following command from the project root directory:

```
./gradlew assemble
```

### Publishing

Set ```gradle.publish.key``` and ```gradle.publish.secret``` in
```~/.gradle/gradle.properties``` and run:

```
./gradlew clean publishPlugins
```
