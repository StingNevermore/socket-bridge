plugins {
    `kotlin-dsl`
}

group = "sbridge-build"

dependencies {
    implementation(project(":base"))

    implementation(libs.springBootPlugin)
    implementation(libs.kotlinSpringPlugin)
    implementation(libs.protobufPlugin)
    implementation(libs.springDependenciesPlugin)
}
