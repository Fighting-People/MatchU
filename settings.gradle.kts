pluginManagement {
    repositories {
<<<<<<< HEAD
        google()
=======
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
>>>>>>> develop
        mavenCentral()
        gradlePluginPortal()
    }
}
<<<<<<< HEAD
=======
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
>>>>>>> develop
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "xml"
include(":app")
 
=======
rootProject.name = "MatchUx"
include(":app")
>>>>>>> develop
