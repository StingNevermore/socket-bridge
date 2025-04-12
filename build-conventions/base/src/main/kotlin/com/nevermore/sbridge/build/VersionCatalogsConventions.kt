package com.nevermore.sbridge.build

/**
 * @author Snake
 */
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.get

/**
 * @author nevermore
 * @since
 */
fun Project.fromVersionCatalog(name: String): Provider<MinimalExternalModuleDependency> {
    val versionCatalogs = extensions["versionCatalogs"] as VersionCatalogsExtension
    val dependency = versionCatalogs.named("libs")
        .findLibrary(name)
    return dependency.orElseThrow { GradleException("Could not find library $name") }
}

@Suppress("unused")
fun Project.coordinateFromVersionCatalog(name: String): String {
    val versionCatalogs = extensions["versionCatalogs"] as VersionCatalogsExtension
    val dependency = versionCatalogs.named("libs")
        .findLibrary(name)
        .orElseThrow { GradleException("Could not find library $name") }
    return dependency.map { "${it.group}:${it.name}:${it.version}" }.get()
}
