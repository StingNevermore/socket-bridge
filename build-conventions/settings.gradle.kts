@file:Suppress("UnstableApiUsage")

rootProject.name = "build-conventions"

include(":build-logics")
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
