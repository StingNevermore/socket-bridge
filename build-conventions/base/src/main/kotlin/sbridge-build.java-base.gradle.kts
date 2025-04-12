import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTIUM

plugins {
    `java-base`
    idea
}

group = "com.nevermore"
version = "0.0.1-SNAPSHOT"

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

//idea {
//    module {
//        isDownloadSources = true
//    }
//}

tasks.withType<JavaCompile> {
    options.apply {
        release = 21
        compilerArgs.add("-parameters")
    }
}
