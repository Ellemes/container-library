import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import com.gitlab.ninjaphenix.gradle.api.task.ParamLocalObfuscatorTask
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
        create("testClient") {
            inherit(this@runs["client"])
            source("test")
        }
        create("testServer") {
            inherit(this@runs["server"])
            source("test")
        }
    }

    accessWidenerPath.set(file("src/main/resources/ninjaphenix_container_lib.accessWidener"))
}

//configurations.getByName(sourceSets["api"].compileClasspathConfigurationName) {
//    this.extendsFrom(configurations["minecraftNamed"])
//}
//
//configurations.getByName(sourceSets["api"].runtimeClasspathConfigurationName) {
//    this.extendsFrom(configurations["minecraftNamed"])
//}

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
    maven {
        name = "Devan Maven"
        url = uri("https://storage.googleapis.com/devan-maven/")
    }
}

val excludeFabric: (ExternalModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
}

dependencies {
    minecraft(libs.minecraft.fabric)
    mappings(loom.layered(nputils::applySilentMojangMappings))

    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)

    modCompileOnly(libs.rei.api, excludeFabric)
    modRuntime(libs.rei)

    modCompileOnly(libs.modmenu, excludeFabric)
    modRuntime(libs.modmenu)


    modCompileOnly(libs.amecs.api)

    // Used for tests only, added to main class path as loom doesn't have a test source set equivalent
    modCompileOnly(libs.arrp, excludeFabric)
    modRuntime(libs.arrp)
}

tasks.withType<ProcessResources> {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

afterEvaluate {

    val jarTask : Jar = tasks.getByName<Jar>("jar") {
    archiveClassifier.set("dev")
    }

    val remapJarTask : RemapJarTask = tasks.getByName<RemapJarTask>("remapJar") {
        archiveClassifier.set("fat")
        //archiveFileName.set("${properties["archives_base_name"]}-${properties["version"]}-fat.jar")
        dependsOn(jarTask)
    }

    val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
        input.set(remapJarTask.outputs.files.singleFile)
        archiveClassifier.set("min")
        dependsOn(remapJarTask)
    }

    // will likely remove this obfuscation when we switch back to yarn
    val releaseJarTask = tasks.register<ParamLocalObfuscatorTask>("releaseJar") {
        input.set(minifyJarTask.get().outputs.files.singleFile)
        from(rootDir.resolve("LICENSE"))
        dependsOn(minifyJarTask)
    }

    tasks.getByName("build") {
        dependsOn(releaseJarTask)
    }

    // https://docs.gradle.org/current/userguide/publishing_maven.html
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "ninjaphenix.container_library"
                artifactId = "fabric"
                artifact(releaseJarTask) {
                    builtBy(releaseJarTask)
                }
                //artifact(sourcesJar) {
                //    builtBy remapSourcesJar
                //}
            }
        }

        repositories {

        }
    }
}
