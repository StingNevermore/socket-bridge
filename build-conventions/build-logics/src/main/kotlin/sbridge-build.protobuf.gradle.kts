import com.google.protobuf.gradle.id
import com.nevermore.sbridge.build.fromVersionCatalog
import com.nevermore.sbridge.build.versionFromVersionCatalog

plugins {
    id("sbridge-build.java-base")
    id("com.google.protobuf")
}

dependencies {
    implementation(platform(fromVersionCatalog("grpcBom")))

    compileOnly("com.google.protobuf:protobuf-java:${versionFromVersionCatalog("protobuf")}")
    compileOnly("com.google.protobuf:protobuf-java-util:${versionFromVersionCatalog("protobuf")}")
    compileOnly("io.grpc:grpc-protobuf")
    compileOnly("io.grpc:grpc-stub")
    compileOnly("io.grpc:grpc-api")
}

idea {
    module {
        sourceDirs.add(file("src/main/proto"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/java"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/grpc"))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${versionFromVersionCatalog("protobuf")}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${versionFromVersionCatalog("grpc")}"
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
