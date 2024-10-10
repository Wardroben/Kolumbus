pluginManagement {
    includeBuild("build-logic")
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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kolumbus"
include(":app")
include(":feature:places")
include(":feature:edit")
include(":feature:settings")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:photo-store")
include(":core:ui")
include(":feature:place")
include(":core:permissions")
