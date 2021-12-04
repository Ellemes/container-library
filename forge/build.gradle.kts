import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import org.gradle.jvm.tasks.Jar
import java.text.DateFormat
import java.util.*

plugins {
    alias(libs.plugins.gradle.utils)
    alias(libs.plugins.gradle.forge)
    alias(libs.plugins.gradle.mixin)
    `maven-publish`
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

mixin {
    add(sourceSets.main.get(), "ninjaphenix-container-lib.refmap.json")
    disableAnnotationProcessorCheck()
}

minecraft {
    mappings("official", properties["minecraft_version"] as String)

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg")) // Currently, this location cannot be changed from the default.

    runs {
        create("client") {
            workingDirectory(rootProject.file("run"))
            mods {
                create("ninjaphenix_container_lib") {
                    source(sourceSets.main.get())
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
                create("ninjaphenix_container_lib") {
                    source(sourceSets.main.get())
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
    minecraft(group = "net.minecraftforge", name = "forge", version = "${properties["minecraft_version"]}-${properties["forge_version"]}")
    implementation(group = "org.spongepowered", name = "mixin", version = properties["mixin_version"] as String)
    annotationProcessor(group = "org.spongepowered", name = "mixin", version = properties["mixin_version"] as String, classifier = "processor")

    implementation(group = "org.jetbrains", name = "annotations", version = properties["jetbrains_annotations_version"] as String)

    compileOnly(group = "mezz.jei", name = "jei-${properties["jei_minecraft_version"]}", version = "${properties["jei_version"]}", classifier = "api")
    compileOnly(group = "org.anti-ad.mc", name = "inventory-profiles-next", version = "forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}")
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
}

val namedJarTask = tasks.register<Jar>("namedJar") {
    archiveClassifier.set("named")
    from(sourceSets["main"].output)
}

val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
    input.set(jarTask.outputs.files.singleFile)
    archiveClassifier.set("")

    manifest.attributes(mapOf(
            "Specification-Title" to "NinjaPhenix's Container Library",
            "Specification-Vendor" to "ninjaphenix",
            "Specification-Version" to "1.0",
            "Implementation-Title" to "ninjaphenix_container_library_forge",
            "Implementation-Version" to "${properties["mod_version"]}",
            "Implementation-Vendor" to "ninjaphenix",
            "Implementation-Timestamp" to DateFormat.getDateTimeInstance().format(Date()),
            "Automatic-Module-Name" to "ninjaphenix.container_library",
            "MixinConfigs" to "ninjaphenix_container_lib.mixins.json"
    ))

    from(rootDir.resolve("LICENSE"))
    dependsOn(tasks["reobfJar"])
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
                classifier = "forge"
            }
        }
    }
}
