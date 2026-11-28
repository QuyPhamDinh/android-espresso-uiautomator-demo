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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

buildCache {
    local {
        val cacheDir = System.getenv("GRADLE_LOCAL_BUILD_CACHE")
        if (!cacheDir.isNullOrBlank()) {
            directory = file(cacheDir)
        }
    }
}

rootProject.name = "WdioNativeDemoApp"
include(":app")
include(":test-shared")
include(":lifecycle-androidTest")
