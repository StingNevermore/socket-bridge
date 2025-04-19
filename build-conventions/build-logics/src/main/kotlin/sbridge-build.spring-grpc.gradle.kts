import com.nevermore.sbridge.build.fromVersionCatalog

plugins {
    id("sbridge-build.java-base")
    id("sbridge-build.protobuf")
    id("org.springframework.boot")
}

dependencies {
    implementation(platform(fromVersionCatalog("springGrpcBom")))

    // spring-grpc
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
    testImplementation("org.springframework.grpc:spring-grpc-test")
}
