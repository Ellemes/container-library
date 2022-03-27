//import org.gradle.jvm.tasks.Jar
//import java.text.DateFormat
//import java.util.*
//
//plugins {
//    id("ninjaphenix.gradle.mod").apply(false)
//    id("net.minecraftforge.gradle")
//    id("org.spongepowered.mixin")
//    `maven-publish`
//}
//
////region Required for test project.
//configurations {
//    create("dev")
//}
//
//tasks.jar {
//    archiveClassifier.set("dev")
//}
//
//artifacts {
//    this.add("dev", tasks.jar.get().archiveFile) {
//        this.builtBy(tasks.jar)
//    }
//}
////endregion
//
//
//dependencies {
//    compileOnly(group = "mezz.jei", name = "jei-${properties["jei_minecraft_version"]}", version = "${properties["jei_version"]}", classifier = "api")
//    compileOnly(("org.anti-ad.mc:inventory-profiles-next:forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}"))
//    testCompileOnly(("org.anti-ad.mc:inventory-profiles-next:forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}"))
//}
//
//val jarTask = tasks.getByName<Jar>("jar") {
//    archiveClassifier.set("fat")
//
//    this.finalizedBy("reobfJar")
//}
//
//val namedJarTask = tasks.register<Jar>("namedJar") {
//    archiveClassifier.set("named")
//    from(sourceSets["main"].output)
//}
//
//val minifyJarTask = tasks.register<ninjaphenix.gradle.mod.api.task.MinifyJsonTask>("minJar") {
//    input.set(jarTask.outputs.files.singleFile)
//    archiveClassifier.set("forge")
//
//    manifest.attributes(mapOf(
//            "Specification-Title" to "NinjaPhenix's Container Library",
//            "Specification-Vendor" to "ninjaphenix",
//            "Specification-Version" to "1.0",
//            "Implementation-Title" to "ninjaphenix_container_library_forge",
//            "Implementation-Version" to "${properties["mod_version"]}",
//            "Implementation-Vendor" to "ninjaphenix",
//            "Implementation-Timestamp" to DateFormat.getDateTimeInstance().format(Date()),
//            "Automatic-Module-Name" to "ninjaphenix.container_library",
//            "MixinConfigs" to "ninjaphenix_container_lib.mixins.json"
//    ))
//
//    from(rootDir.resolve("LICENSE"))
//    dependsOn(tasks["reobfJar"])
//}
//
//tasks.getByName("build") {
//    dependsOn(minifyJarTask)
//}
//
//// https://docs.gradle.org/current/userguide/publishing_maven.html
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = "container_library"
//            artifact(minifyJarTask) {
//                builtBy(minifyJarTask)
//            }
//        }
//    }
//}
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import ninjaphenix.gradle.mod.api.task.MinifyJsonTask
import java.text.DateFormat
import java.util.Date

plugins {
    id("com.github.johnrengelman.shadow")
    id("ninjaphenix.gradle.mod").apply(false)
}

architectury {
    platformSetupLoomIde()
    forge()
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
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    //mavenCentral()
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.properties["minecraft_version"]}-${project.properties["forge_version"]}")

    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }

    compileOnly("mezz.jei:jei-${project.properties["jei_minecraft_version"]}:${project.properties["jei_version"]}:api")
    compileOnly("maven.modrinth:inventory-profiles-next:forge-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}")
}

tasks.withType<ProcessResources> {
    val properties = mutableMapOf("version" to project.version)
    inputs.properties(properties)
    filesMatching("META-INF/mods.toml") {
        expand(properties)
    }
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
        create<MavenPublication>("maven") {
            artifactId = "container_library"
            version = "${project.version}+${properties["minecraft_version"]}"
            //from(components.getByName("java"))
            artifact(minifyJarTask) {
                builtBy(minifyJarTask)
            }
        }
    }
}
