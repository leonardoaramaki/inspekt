This is a sample Kotlin compiler plugin I designed under my ramblings
on JVM bytecode instrumentation.

This is a very basic plugin. All it does is to automatically generate the
code to return the full qualified class name for custom views to be
properly viewed and identified on tools such as the uiautomatorviewer.
I called it **inspekt**.

# Installation
Since this is just an exploratory project and not _really_ that useful (_yet_) 
, this plugin is not published anywhere.
Instead, if you're willing to give it a try for any reason, just build the project:

```
./gradlew clean build
```

And install it on your local maven repository:

```
./gradlew :gradle-plugin:install :compiler:install
```

Add the following dependency to your project-level `build.gradle` file:

```
buildscript {
    // Does not work on Kotlin versions 1.4.X and above
    ext.kotlin_version = "1.3.61"
    repositories {
        ...
        mavenLocal()
    }
    dependencies {
       ...
       classpath "inspekt:gradle-plugin:0.0.1"
    }
}

allprojects {
    repositories {
        ...
        mavenLocal()
    }
    apply plugin: 'inspekt'
}
```
This would apply it to all subprojects. That's all what's needed.

The plugin is enabled by default. You can disable it for specific modules by adding to each `build.gradle` file:

```
inspekt {
    enabled = false
}
```

# Inspekt

The resulting effect of this plugin can be seen on the comparative images below.

Before (uiautomatorviewer):

![Normal output as seen by uiautomatorviewer](/assets/image1.png)

Instrumented (uiautomatorviewer):

![Instrumented output as seen by uiautomatorviewer](/assets/image2.png)

# Roadmap

There are plans to add features whenever I have some time. The 
basic idea is developing this plugin to make inspection tooling more
extensible. 



