@file:Suppress("UnstableApiUsage")

rootProject.name = "build-conventions"

include(":spring-boot-proj")
include(":base")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
