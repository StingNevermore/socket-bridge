import com.nevermore.sbridge.build.coordinateFromVersionCatalog

plugins {
    id("sbridge-build.spring-boot-kt")
    id("sbridge-build.spring-grpc")
}

dependencies {
    implementation(project(":libs:cli-server-communication"))
    implementation(libs.guava)
    implementation("io.netty:netty-all")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

dependencyManagement {
    imports {
        mavenBom(coordinateFromVersionCatalog("nettyBom"))
    }
}

tasks {
    bootRun {
        enabled = true
    }
    bootJar {
        enabled = true
        archiveFileName.set("socket-bridge-server.jar")
    }
}
