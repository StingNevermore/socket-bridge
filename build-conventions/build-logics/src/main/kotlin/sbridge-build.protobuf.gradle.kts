import com.google.protobuf.gradle.id
import com.nevermore.sbridge.build.fromVersionCatalog

plugins {
    id("sbridge-build.java-base")
    id("com.google.protobuf")
}

dependencies {
    implementation(platform(fromVersionCatalog("grpcBom")))

    compileOnly("com.google.protobuf:protobuf-java:4.30.2")
    compileOnly("com.google.protobuf:protobuf-java-util:4.30.2")
    compileOnly("io.grpc:grpc-protobuf")
    compileOnly("io.grpc:grpc-stub")
}

idea {
    module {
        sourceDirs.add(file("src/main/proto"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/java"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/grpc"))
    }
}

protobuf {
    // Configure the protoc executable
    protoc {
        // Download from repositories
        artifact = "com.google.protobuf:protoc:4.30.2"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.72.0"
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
