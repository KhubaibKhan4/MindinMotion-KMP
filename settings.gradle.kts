rootProject.name = "Mind-in-Motion"
include(":composeApp")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jogamp.org/deployment/maven")
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
