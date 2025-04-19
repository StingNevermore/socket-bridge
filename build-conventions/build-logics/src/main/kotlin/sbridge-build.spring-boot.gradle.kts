@file:Suppress("UnstableApiUsage")

import com.nevermore.sbridge.build.configureTestingFramework
import com.nevermore.sbridge.build.fromVersionCatalog

plugins {
    id("sbridge-build.java-base")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(platform(fromVersionCatalog("springGrpcBom")))

    implementation("org.springframework.boot:spring-boot-starter")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

configureTestingFramework()

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}", "-Xshare:off")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val profile: String? by project

tasks {
    processResources {
        filesMatching("application.yml") {
            expand("activeProfile" to (profile ?: "dev"))
        }
    }

    bootRun {
        enabled = false
    }
    bootBuildImage {
        enabled = false
    }
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
}
