import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import com.gitlab.ninjaphenix.gradle.api.task.ParamLocalObfuscatorTask
import org.gradle.jvm.tasks.Jar
import java.text.DateFormat
import java.util.*

plugins {
    alias(libs.plugins.gradleUtils)
    alias(libs.plugins.forgeGradle)
    `maven-publish`
}

//configurations {
//    this["apiImplementation"].extendsFrom(this["implementation"])
//    this["apiRuntimeOnly"].extendsFrom(this["runtimeOnly"])
//}

minecraft {
    mappings("official", "1.17.1")

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg")) // Currently, this location cannot be changed from the default.

    runs {
        create("client") {
            workingDirectory(rootProject.file("run"))
            mods {
                create("ninjaphenix-container-library") {
                    source(sourceSets.main.get())
                    //source(sourceSets["api"])
                }
            }
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            //property("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            //property("forge.logging.console.level", "debug")
        }

        create("server") {
            workingDirectory(rootProject.file("run"))
            mods {
                create("ninjaphenix-container-library") {
                    source(sourceSets.main.get())
                    //source(sourceSets["api"])
                }
            }
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            //property("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            //property("forge.logging.console.level", "debug")
        }
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
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft.forge)
    val jei = (libs.jei.api as Provider<MinimalExternalModuleDependency>).get()
    compileOnly(fg.deobf("${jei.module.group}:${jei.module.name}:${jei.versionConstraint.displayName}"))
    implementation(libs.jetbrainAnnotations)
}

tasks.withType<ProcessResources> {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") {
        expand(props)
    }
}

val jarTask = tasks.getByName<Jar>("jar") {
    archiveClassifier.set("fat")

    manifest.attributes(mapOf(
            "Specification-Title" to "NinjaPhenix's Container Library",
            "Specification-Vendor" to "ninjaphenix",
            "Specification-Version" to "0.0",
            "Implementation-Title" to "ninjaphenix_container_library_forge",
            "Implementation-Version" to "${properties["mod_version"]}",
            "Implementation-Vendor" to "ninjaphenix",
            "Implementation-Timestamp" to DateFormat.getDateTimeInstance().format(Date()),
            "Automatic-Module-Name" to "ninjaphenix.container_library",
    ))

    this.finalizedBy("reobfJar")
}

val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
    input.set(jarTask.outputs.files.singleFile)
    archiveClassifier.set("min")
    dependsOn(jarTask)
}

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
            artifactId = "forge"
            artifact(jarTask) {
                builtBy(jarTask)
            }
            //artifact(sourcesJar) {
            //    builtBy remapSourcesJar
            //}
        }
    }

    repositories {

    }
}
