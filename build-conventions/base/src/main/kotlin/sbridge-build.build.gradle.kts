plugins {
    id("sbridge-build.base")
}

val outputDir = project.layout.buildDirectory.dir("socket-bridge-${version}")

val buildExploded = tasks.register<DefaultTask>("buildExploded") {
    dependsOn(
        subprojects
            .filter { it.name in arrayOf("server", "cli-tools") }
            .map { it.tasks.assemble }
    )

    val outputDirFile = outputDir.get().asFile
    doLast {
        if (outputDirFile.exists()) {
            outputDirFile.deleteRecursively()
        }
        outputDirFile.mkdirs()

        val libsDir = File(outputDirFile.absolutePath + "/libs").also { it.mkdir() }
        val binDir = File(outputDirFile.absolutePath + "/bin").also { it.mkdir() }
        val configDir = File(outputDirFile.absolutePath + "/config").also { it.mkdir() }

        copy {
            from(project(":server").layout.buildDirectory.dir("libs"))
            include("socket-bridge-server.jar")
            into(libsDir)
        }

        copy {
            from(project(":cli-tools").layout.buildDirectory.dir("native/nativeCompile"))
            include("*")
            into(binDir)
        }

        copy {
            from(project(":server").layout.buildDirectory.dir("resources/main"))
            include("application*.yml")
            into(configDir)
        }

        copy {
            from(project(":cli-tools").layout.buildDirectory.dir("resources/main"))
            include("jvm.options")
            into(configDir)
        }

        copy {
            from(project(":cli-tools").layout.buildDirectory.dir("resources/main/bin"))
            include("*")
            into(binDir)
        }

        logger.lifecycle("Artifacts copied to ${outputDirFile.absolutePath}")
    }
    outputs.dir(outputDirFile)
}

val createDistribution = tasks.register<Tar>("createDistribution") {
    dependsOn(buildExploded)

    archiveFileName.set("socket-bridge-${version}.tar.gz")
    destinationDirectory.set(project.layout.buildDirectory)
    compression = Compression.GZIP

    from(project.layout.buildDirectory) {
        include("socket-bridge-${version}/**")
    }
}

tasks.assemble {
    dependsOn(createDistribution)
}

tasks.clean {
    dependsOn(
        subprojects
            .filter { it.name in arrayOf(":server", ":cli-tools") }
            .map { it.tasks.clean }
    )

    // Clean output directory
    doLast {
        outputDir.get().asFile.deleteRecursively()
    }
}
