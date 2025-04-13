import com.nevermore.sbridge.build.coordinateFromVersionCatalog

plugins {
    id("sbridge-build.spring-boot")
}

dependencies {
    implementation(libs.guava)

    implementation("io.netty:netty-all")
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
    }
}
