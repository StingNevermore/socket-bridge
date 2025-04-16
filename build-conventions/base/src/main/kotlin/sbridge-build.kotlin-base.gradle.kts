plugins {
    id("sbridge-build.java-base")
    kotlin("jvm")
}

kotlin {
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}
