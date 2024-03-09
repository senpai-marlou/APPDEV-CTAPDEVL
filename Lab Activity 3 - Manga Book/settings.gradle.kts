pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        val url = "https://www.jitpack.io"
        maven (url)
    }
}

rootProject.name = "Manga Book"
include(":app")
