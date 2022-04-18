import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import ninjaphenix.gradle.mod.api.task.MinifyJsonTask
import java.text.DateFormat
import java.util.Date

plugins {
    id("com.github.johnrengelman.shadow")
    id("ninjaphenix.gradle.mod").apply(false)
    id("maven-publish")
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        mixinConfig("ninjaphenix_container_lib.mixins.json")
    }
}

configurations {
    create("common")
    create("shadowCommon") // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    named("developmentForge").get().extendsFrom(configurations["common"])
}

repositories {
    maven {
        // JEI maven
        name = "Progwml6 maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    maven {
        // JEI maven - fallback
        name = "ModMaven"
        url = uri("https://modmaven.k-4u.nl")
    }
}

dependencies {
    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }

    compileOnly("mezz.jei:jei-${project.properties["jei_minecraft_version"]}:${project.properties["jei_version"]}:api")
    compileOnly("maven.modrinth:inventory-profiles-next:forge-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}")
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar")

shadowJar.apply {
    //exclude "fabric.mod.json" // dead code?
    exclude("architectury.common.json")
    configurations = listOf(project.configurations["shadowCommon"])
    archiveClassifier.set("dev-shadow")
}

tasks.getByName<RemapJarTask>("remapJar") {
    //injectAccessWidener.set(true)
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

    manifest.attributes(mapOf(
            "Specification-Title" to "NinjaPhenix's Container Library",
            "Specification-Vendor" to "ninjaphenix",
            "Specification-Version" to "1.3",
            "Implementation-Title" to "ninjaphenix_container_library_forge",
            "Implementation-Version" to "${project.version}",
            "Implementation-Vendor" to "ninjaphenix",
            "Implementation-Timestamp" to DateFormat.getDateTimeInstance().format(Date()),
            "Automatic-Module-Name" to "ninjaphenix.container_library",
            "MixinConfigs" to "ninjaphenix_container_lib.mixins.json"
    ))
}
tasks.build {
    dependsOn(minifyJarTask)
}

(components.findByName("java") as AdhocComponentWithVariants).apply {
    withVariantsFromConfiguration(project.configurations.getByName("shadowRuntimeElements")) {
        skip()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
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
