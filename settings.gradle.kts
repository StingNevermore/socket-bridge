@file:Suppress("UnstableApiUsage")

rootProject.name = "socket-bridge"

include(":server")
include(":distributions")
include(":distributions:cli-tools")

includeBuild("build-conventions")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
