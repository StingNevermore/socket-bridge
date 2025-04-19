plugins {
    id("sbridge-build.java-base")
    id("sbridge-build.protobuf")
}

//protobuf {
//    // Configure the protoc executable
//    protoc {
//        // Download from repositories
//        artifact = "com.google.protobuf:protoc:4.30.2"
//    }
//
//    plugins {
//        id("grpc") {
//            artifact = "io.grpc:protoc-gen-grpc-java:1.72.0"
//        }
//    }
//    generateProtoTasks {
//        all().forEach {
//            it.plugins {
//                id("grpc") {
//                    option("jakarta_omit")
//                    option("@generated=omit")
//                }
//            }
//        }
//    }
//}
