import ellemes.gradle.mod.api.task.MinifyJsonTask
import java.text.DateFormat
import java.util.Date

plugins {
    id("ellemes.gradle.mod").apply(false)
}

loom {
    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        mixinConfig("ellemes_container_lib.mixins.json")
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
    compileOnly("mezz.jei:jei-${properties["jei_minecraft_version"]}:${properties["jei_version"]}:api")
    compileOnly("maven.modrinth:inventory-profiles-next:forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}")
}

tasks.getByName<MinifyJsonTask>("minJar") {
    manifest.attributes(mapOf(
            "Specification-Title" to "Ellemes' Container Library",
            "Specification-Vendor" to "ellemes",
            "Specification-Version" to "1.4",
            "Implementation-Title" to "ellemes_container_library_forge",
            "Implementation-Version" to "${project.version}",
            "Implementation-Vendor" to "ellemes",
            "Implementation-Timestamp" to DateFormat.getDateTimeInstance().format(Date()),
            "Automatic-Module-Name" to "ellemes.container_library",
            "MixinConfigs" to "ellemes_container_lib.mixins.json"
    ))
}

val u = ellemes.gradle.mod.api.publishing.UploadProperties(project, "https://github.com/Ellemes/container-library")

u.configureCurseForge {
    relations(closureOf<me.hypherionmc.cursegradle.CurseRelation> {
        optionalDependency("jei")
        optionalDependency("inventory-profiles-next")
    })
}
