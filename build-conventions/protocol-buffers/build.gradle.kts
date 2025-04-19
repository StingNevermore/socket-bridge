plugins {
    `kotlin-dsl`
}

group = "sbridge-build"

dependencies {
    implementation(project(":base"))

    implementation(libs.protobufPlugin)
}
