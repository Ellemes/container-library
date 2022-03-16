import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("fabric-loom")
    id("ninjaphenix.gradle.mod").apply(false)
    `maven-publish`
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
    maven {
        name = "Flemmli97"
        url = uri("https://gitlab.com/api/v4/projects/21830712/packages/maven")
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
    listOf(
            "fabric-networking-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-key-binding-api-v1"
    ).forEach {
        modImplementation(fabricApi.module(it, properties["fabric_api_version"] as String))
    }

    modCompileOnly(group = "me.shedaniel", name = "RoughlyEnoughItems-api-fabric", version = properties["rei_version"] as String, dependencyConfiguration = excludeFabric)

    modCompileOnly(group = "com.terraformersmc", name = "modmenu", version = properties["modmenu_version"] as String, dependencyConfiguration = excludeFabric)

    modCompileOnly(group = "de.siphalor", name = "amecsapi-1.17", version = properties["amecs_version"] as String, dependencyConfiguration = excludeFabric)

    modCompileOnly(group = "io.github.flemmli97", name = "flan", version = "1.17.1-${properties["flan_version"]}", classifier = "api")  {
        also(excludeFabric)
        isTransitive = false
    }
    modCompileOnly(group = "org.anti-ad.mc", name = "inventory-profiles-next", version = "fabric-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}", dependencyConfiguration = excludeFabric)
}

val remapJarTask: RemapJarTask = tasks.getByName<RemapJarTask>("remapJar") {
    archiveClassifier.set("fat")
    dependsOn(tasks.jar)
}

val minifyJarTask = tasks.register<ninjaphenix.gradle.mod.api.task.MinifyJsonTask>("minJar") {
    input.set(remapJarTask.outputs.files.singleFile)
    archiveClassifier.set("fabric")
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
            artifactId = "container_library"
            artifact(minifyJarTask) {
                builtBy(minifyJarTask)
            }
        }
    }
}
