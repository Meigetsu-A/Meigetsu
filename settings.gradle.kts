pluginManagement {
    repositories {
        google()
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

rootProject.name = "Meigetsu"

include(":app")
include(":core:common")
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:network")
include(":core:database")
include(":core:ui")
include(":core:extensions")
include(":feature:home")
include(":feature:library")
include(":feature:updates")
include(":feature:browse")
include(":feature:details")
include(":feature:player")
include(":feature:reader")
include(":feature:settings")
include(":feature:news")
