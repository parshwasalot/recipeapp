pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins{
        id("com.google.gms.google-services") version "4.4.3"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Correctly includes the Google Maven repository
        mavenCentral()
        // The line below is redundant and should be removed if 'google()' is used.
        // maven { url = uri("https://maven.google.com") }
    }
}

rootProject.name = "recipeapp"
include(":app")