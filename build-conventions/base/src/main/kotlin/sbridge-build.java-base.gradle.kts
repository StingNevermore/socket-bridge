import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTIUM

plugins {
    id("sbridge-build.base")
    `java-library`
}

repositories {
    mavenCentral()
    google()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = ADOPTIUM
    }
}

tasks.withType<JavaCompile> {
    options.apply {
        release = 21
        compilerArgs.add("-parameters")
    }
}
