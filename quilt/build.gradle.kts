import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import ninjaphenix.gradle.mod.api.task.MinifyJsonTask

plugins {
    id("com.github.johnrengelman.shadow")
    id("ninjaphenix.gradle.mod").apply(false)
    id("maven-publish")
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

configurations {
    create("common")
    create("shadowCommon") // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    named("developmentQuilt").get().extendsFrom(configurations["common"])
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

fun excludeFabric(it : ModuleDependency) {
    it.exclude(group = "net.fabricmc")
    it.exclude(group = "net.fabricmc.fabric-api")
}

mod {
    fabricApi(
            "fabric-registry-sync-v0", // Required to delay registry freezing
            "fabric-networking-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-key-binding-api-v1"
    )
    qsl (
            "core/qsl_base",
            "core/networking"
    )
}

dependencies {
    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionQuilt")) {
        isTransitive = false
    }

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${project.properties["rei_version"]}") {
        excludeFabric(this)
    }

    modCompileOnly("com.terraformersmc:modmenu:${project.properties["modmenu_version"]}") {
        excludeFabric(this)
    }
    //modRuntimeOnly("com.terraformersmc:modmenu:${project.modmenu_version}")

    modCompileOnly("de.siphalor:amecsapi-1.18:${project.properties["amecs_version"]}") {
        excludeFabric(this)
        exclude(group = "com.github.astei")
    }

    modCompileOnly("io.github.flemmli97:flan:1.18.2-${project.properties["flan_version"]}:fabric-api")  {
        excludeFabric(this)
        exclude(group = "curse.maven")
    }

    modCompileOnly("maven.modrinth:inventory-profiles-next:fabric-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}") {
        excludeFabric(this)
    }
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar")

shadowJar.apply {
    exclude("architectury.common.json")
    configurations = listOf(project.configurations["shadowCommon"])
    archiveClassifier.set("dev-shadow")
}

tasks.getByName<RemapJarTask>("remapJar") {
    injectAccessWidener.set(true)
    inputFile.set(shadowJar.archiveFile)
    dependsOn(shadowJar)
    archiveClassifier.set("fat")
}

tasks.jar {
    archiveClassifier.set("dev")
}

val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
    input.set(tasks.getByName("remapJar").outputs.files.singleFile)
    archiveClassifier.set(project.name)
    from(rootDir.resolve("LICENSE"))
    dependsOn(tasks.getByName("remapJar"))
}

tasks.build {
    dependsOn(minifyJarTask)
}

(components.findByName("java") as AdhocComponentWithVariants).withVariantsFromConfiguration(project.configurations.getByName("shadowRuntimeElements")) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("mavenQuilt") {
            artifactId = "container_library"
            version = "${project.version}+${properties["minecraft_version"]}-${project.name}"
            //from(components.getByName("java"))
            artifact(minifyJarTask) {
                builtBy(minifyJarTask)
                classifier = ""
            }
        }
    }
}
