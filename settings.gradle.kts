@file:Suppress("UnstableApiUsage")

rootProject.name = "socket-bridge"

include(":server")
include(":cli-tools")
include(":libs")
include(":libs:cli-server-communication")
include(":libs:bridge-protocol")

includeBuild("build-conventions")

pluginManagement {
    dependencyResolutionManagement {
        repositories {
            mavenCentral()
            google()
        }
    }
    includeBuild("build-conventions")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
