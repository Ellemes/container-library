import net.fabricmc.loom.api.LoomGradleExtensionAPI
import kotlin.reflect.KClass

plugins {
    id("architectury-plugin").version("3.4-SNAPSHOT")
    id("dev.architectury.loom").version("0.11.0-SNAPSHOT").apply(false)
    id("ninjaphenix.gradle.mod").version("6.2.0.6").apply(false)
    id("com.github.johnrengelman.shadow").version("7.1.2").apply(false)
    //id("com.modrinth.minotaur").version("1.2.1")
    //id("com.matthewprenger.cursegradle").version("1.4.0")
}

fun <T : Any> getPlugin(project : Project, type : KClass<T>): T {
    return project.extensions.findByType(type)!!
}

architectury {
    minecraft = rootProject.properties["minecraft_version"] as String
}

tasks.register("buildMod") {
    dependsOn(project(":fabric").tasks.getByName("build"))
    dependsOn(project(":forge").tasks.getByName("build"))
}

subprojects {
    apply(plugin="dev.architectury.loom")

    buildDir = file(rootDir.toPath().resolve("build/${project.name}/"))

    val loomPlugin = getPlugin(this, LoomGradleExtensionAPI::class)
    loomPlugin.silentMojangMappingsLicense()

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.properties["minecraft_version"]}")
        "mappings"(loomPlugin.officialMojangMappings())
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    getPlugin(this, BasePluginExtension::class).archivesName.set("${rootProject.properties["archives_base_name"]}")
    version = "${rootProject.properties["mod_version"]}"
    group = "${rootProject.properties["maven_group"]}"

    tasks.withType(JavaCompile::class) {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}
