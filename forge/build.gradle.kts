import org.gradle.jvm.tasks.Jar
import java.text.DateFormat
import java.util.*

plugins {
    id("ninjaphenix.gradle.mod").apply(false)
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
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
    compileOnly(group = "mezz.jei", name = "jei-${properties["jei_minecraft_version"]}", version = "${properties["jei_version"]}", classifier = "api")
    compileOnly(group = "org.anti-ad.mc", name = "inventory-profiles-next", version = "forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}")
}

val jarTask = tasks.getByName<Jar>("jar") {
    archiveClassifier.set("fat")

    this.finalizedBy("reobfJar")
}

val namedJarTask = tasks.register<Jar>("namedJar") {
    archiveClassifier.set("named")
    from(sourceSets["main"].output)
}

val minifyJarTask = tasks.register<ninjaphenix.gradle.mod.api.task.MinifyJsonTask>("minJar") {
    input.set(jarTask.outputs.files.singleFile)
    archiveClassifier.set("forge")

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
            }
        }
    }
}
