import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import net.fabricmc.loom.task.RemapJarTask

plugins {
    alias(libs.plugins.gradleUtils)
    alias(libs.plugins.fabricLoom)
    `maven-publish`
}

loom {
    runs {
        named("client") {
            ideConfigGenerated(false)
        }
        named("server") {
            ideConfigGenerated(false)
            serverWithGui()
        }
    }

    mixin {
        useLegacyMixinAp.set(true)
    }

    accessWidenerPath.set(file("src/main/resources/ninjaphenix_container_lib.accessWidener"))
}

repositories {
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "TerraformersMC"
                url = uri("https://maven.terraformersmc.com/")
            }
        }
        filter {
            includeGroup("com.terraformersmc")
        }
    }
    maven {
        name = "Siphalor's Maven"
        url = uri("https://maven.siphalor.de/")
    }
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

dependencies {
    minecraft(libs.minecraft.fabric)
    mappings("net.fabricmc:yarn:1.17.1+build.61:v2")

    modImplementation(libs.fabric.loader)

    listOf(
            "fabric-networking-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-key-binding-api-v1"
    ).forEach {
        modImplementation(fabricApi.module(it, libs.fabric.api.get().versionConstraint.displayName))
    }

    modCompileOnly(libs.rei.api, excludeFabric)

    modCompileOnly(libs.modmenu, excludeFabric)

    modCompileOnly(libs.amecs.api, excludeFabric)
}

tasks.withType<ProcessResources> {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

val updateForgeSources = tasks.register<net.fabricmc.loom.task.MigrateMappingsTask>("updateForgeSources") {
    setInputDir(rootDir.toPath().resolve("common/fabricSrc/main/java").toString())
    setOutputDir(rootDir.toPath().resolve("common/forgeSrc/main/java").toString())
    setMappings("net.minecraft:mappings:${properties["minecraft_version"]}")
}
if (hasProperty("yv")) {
    val updateCommonSources = tasks.register<net.fabricmc.loom.task.MigrateMappingsTask>("updateCommonSources") {
        setInputDir(rootDir.toPath().resolve("common/fabricSrc/main/java").toString())
        setOutputDir(rootDir.toPath().resolve("common/fabricSrc/main/java").toString())
        setMappings("net.fabricmc:yarn:" + findProperty("yv") as String)
    }

    val updateFabricSources = tasks.register<net.fabricmc.loom.task.MigrateMappingsTask>("updateFabricSources") {
        dependsOn(updateCommonSources)

        setInputDir(rootDir.toPath().resolve("fabric/src/main/java").toString())
        setOutputDir(rootDir.toPath().resolve("fabric/src/main/java").toString())
        setMappings("net.fabricmc:yarn:" + findProperty("yv") as String)
    }
}

afterEvaluate {

    val jarTask: Jar = tasks.getByName<Jar>("jar") {
        archiveClassifier.set("dev")
    }

    val remapJarTask: RemapJarTask = tasks.getByName<RemapJarTask>("remapJar") {
        archiveClassifier.set("fat")
        dependsOn(jarTask)
    }

    val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
        input.set(remapJarTask.outputs.files.singleFile)
        archiveClassifier.set("")
        from(rootDir.resolve("LICENSE"))
        dependsOn(remapJarTask)
    }

    tasks.getByName("build") {
        dependsOn(minifyJarTask)
    }

    // https://docs.gradle.org/current/userguide/publishing_maven.html
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "ninjaphenix.container_library"
                artifactId = "fabric"
                artifact(minifyJarTask) {
                    builtBy(minifyJarTask)
                }
            }
        }
    }
}
