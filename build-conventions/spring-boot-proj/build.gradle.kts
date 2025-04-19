plugins {
    `kotlin-dsl`
}

group = "sbridge-build"

dependencies {
    implementation(project(":base"))
    implementation(project(":protocol-buffers"))

    implementation(libs.springBootPlugin)
    implementation(libs.kotlinSpringPlugin)
    implementation(libs.protobufPlugin)
    implementation(libs.springDependenciesPlugin)
    implementation(libs.kotlinKaptPlugin)
}
