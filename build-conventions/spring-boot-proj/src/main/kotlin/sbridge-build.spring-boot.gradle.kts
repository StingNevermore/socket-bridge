@file:Suppress("UnstableApiUsage")

import com.google.protobuf.gradle.id
import com.nevermore.sbridge.build.configureTestingFramework
import com.nevermore.sbridge.build.coordinateFromVersionCatalog

plugins {
    id("java-library")
    id("sbridge-build.java-base")
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
    id("com.google.protobuf")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("io.grpc:grpc-services")
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
    testImplementation("org.springframework.grpc:spring-grpc-test")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

dependencyManagement {
    imports {
        mavenBom(coordinateFromVersionCatalog("springGrpcBom"))
    }
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

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("jakarta_omit")
                    option("@generated=omit")
                }
            }
        }
    }
}

