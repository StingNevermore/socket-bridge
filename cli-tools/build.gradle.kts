plugins {
    id("sbridge-build.java-base")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.graalvmBuildTools)
}

dependencies {
    implementation(libs.picocliSpringBootStarter) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation(project(":libs:cli-server-communication"))
    annotationProcessor(libs.picocliCodegen)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

configurations.all {
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "org.slf4j", module = "slf4j-api")
    exclude(group = "org.slf4j", module = "jul-to-slf4j")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    exclude(group = "org.apache.commons", module = "commons-logging")
}


springBoot {
    mainClass = "com.nevermore.sbridge.CliToolLauncher"
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("sbridge-cli")
            fallback.set(false)
            sharedLibrary.set(false)
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+UnlockExperimentalVMOptions")
            buildArgs.add("-H:+ReportExceptionStackTraces")
//            buildArgs.add("-H:+StripDebugInfo")
            buildArgs.add("-H:Optimize=2")
            buildArgs.add("-H:+RemoveSaturatedTypeFlows")
            buildArgs.add("-H:Log=registerResource:3")
        }
    }
}

//configureTestingFramework()

tasks {
    assemble {
        dependsOn("nativeCompile")
    }
}
