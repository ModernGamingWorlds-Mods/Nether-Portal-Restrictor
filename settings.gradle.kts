pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.kikugie.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    kotlinController = true

    create(rootProject) {
        version("1.18.2-forge", "1.18.2")
        version("1.19.2-forge", "1.19.2")
        version("1.20.1-forge", "1.20.1")
        version("1.20.1-neoforge", "1.20.1")
        version("1.21.1-neoforge", "1.21.1")
        version("26.1-forge", "26.1")
        version("26.1-neoforge", "26.1")

        mapBuilds { _, data ->
            val loader = data.project.substringAfterLast('-')
            "$loader.gradle.kts"
        }

        vcsVersion = "1.21.1-neoforge"
    }
}
