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
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Beatbeat"
include(":app")
include(":feature:auth")
include(":feature:home")
include(":feature:search")
include(":feature:favorites")
include(":core:network")
include(":core:local_data")
include(":core:ui")
include(":core:utils")
include(":core")
include(":feature")
include(":feature:listen")
include(":feature:details")
include(":exo_player")
