import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import ninjaphenix.gradle.mod.api.task.MinifyJsonTask
import java.text.DateFormat
import java.util.Date

plugins {
    id("ninjaphenix.gradle.mod").apply(false)
    id("maven-publish")
}

loom {
    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        mixinConfig("ninjaphenix_container_lib.mixins.json")
    }
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
    compileOnly("mezz.jei:jei-${project.properties["jei_minecraft_version"]}:${project.properties["jei_version"]}:api")
    compileOnly("maven.modrinth:inventory-profiles-next:forge-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}")
}

tasks.getByName<MinifyJsonTask>("minJar") {
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

val minifyJarTask = tasks.getByName("minJar")

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
            artifactId = "container_library"
            version = "${project.version}-${project.name}"
            //from(components.getByName("java"))
            artifact(minifyJarTask) {
                builtBy(minifyJarTask)
                classifier = ""
            }
        }
    }
}
