import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import net.fabricmc.loom.task.RemapJarTask

plugins {
    alias(libs.plugins.gradle.utils)
    alias(libs.plugins.gradle.loom)
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

    accessWidenerPath.set(file("src/main/resources/ninjaphenix_container_lib.accessWidener"))
}

repositories {
    // For REI
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    // For Mod Menu
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
    // For Amecs
    maven {
        name = "Siphalor's Maven"
        url = uri("https://maven.siphalor.de/")
    }
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

//region Required for test project.
configurations {
    create("dev")
}

tasks.jar {
    archiveClassifier.set("dev")
}

artifacts {
    this.add("dev", tasks.jar.get().archiveFile) {
        this.builtBy(tasks.jar)
    }
}
//endregion

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = properties["minecraft_version"] as String)
    mappings(group = "net.fabricmc", name = "yarn", version = "${properties["minecraft_version"]}+build.${properties["yarn_version"]}", classifier = "v2")

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = properties["fabric_loader_version"] as String)
    implementation(group = "org.jetbrains", name = "annotations", version = properties["jetbrains_annotations_version"] as String)

    listOf(
            "fabric-networking-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-key-binding-api-v1"
    ).forEach {
        modImplementation(fabricApi.module(it, properties["fabric_api_version"] as String))
    }

    modCompileOnly(group = "me.shedaniel", name = "RoughlyEnoughItems-api", version = properties["rei_version"] as String, dependencyConfiguration = excludeFabric)

    modCompileOnly(group = "com.terraformersmc", name = "modmenu", version = properties["modmenu_version"] as String, dependencyConfiguration = excludeFabric)

    modCompileOnly(group = "de.siphalor", name = "amecsapi-1.17", version = properties["amecs_version"] as String, dependencyConfiguration = excludeFabric)
}

tasks.withType<ProcessResources> {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.register<net.fabricmc.loom.task.MigrateMappingsTask>("updateForgeSources") {
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

    tasks.register<net.fabricmc.loom.task.MigrateMappingsTask>("updateFabricSources") {
        dependsOn(updateCommonSources)

        setInputDir(rootDir.toPath().resolve("fabric/src/main/java").toString())
        setOutputDir(rootDir.toPath().resolve("fabric/src/main/java").toString())
        setMappings("net.fabricmc:yarn:" + findProperty("yv") as String)
    }
}

val remapJarTask: RemapJarTask = tasks.getByName<RemapJarTask>("remapJar") {
    archiveClassifier.set("fat")
    dependsOn(tasks.jar)
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
