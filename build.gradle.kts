import com.matthewprenger.cursegradle.CurseArtifact
import com.modrinth.minotaur.request.VersionType

buildscript {
    dependencies {
        classpath(group = "com.google.guava", name = "guava", version = "30.1.1-jre") {
            because("Required by loom, conflicts with curse gradle.")
        }
        classpath(group = "com.google.code.gson", name = "gson", version = "2.8.8") {
            because("Required by loom, conflicts with curse gradle")
        }
    }
}

plugins {
    java
    alias(libs.plugins.gradle.modrinth)
    alias(libs.plugins.gradle.curseforge)
}

fun isMainSubProject(name: String): Boolean {
    return name == "forge" || name == "fabric"
}

subprojects {
    apply(plugin = "java")

    group = properties["maven_group"] as String
    version = "${properties["mod_version"]}+${properties["minecraft_version"]}"
    base.archivesName.set(properties["archives_base_name"] as String)
    buildDir = rootDir.resolve("build/${project.name}")

    java {
        sourceCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
        targetCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
    }

    if (isMainSubProject(project.name)) {
        sourceSets {
            main {
                java {
                    setSrcDirs(listOf(
                            "src/main/java",
                            rootDir.resolve("common/${project.name}Src/main/java")
                    ))
                }
                resources {
                    setSrcDirs(listOf(
                            "src/main/resources",
                            rootDir.resolve("common/src/main/resources")
                    ))
                }
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

tasks.register("buildMod") {
    subprojects.forEach {
        if (isMainSubProject(it.name)) {
            dependsOn(it.tasks["build"])
        }
    }
}

tasks.register("publishToMavenLocal") {
    subprojects.forEach {
        if (isMainSubProject(it.name)) {
            dependsOn(it.tasks["publishToMavenLocal"])
        }
    }
}

var modrinthForgeTask : TaskProvider<com.modrinth.minotaur.TaskModrinthUpload>? = null
var modrinthFabricTask : TaskProvider<com.modrinth.minotaur.TaskModrinthUpload>? = null

var curseforgeForgeTask : TaskProvider<com.matthewprenger.cursegradle.CurseUploadTask>? = null
var curseforgeFabricTask : TaskProvider<com.matthewprenger.cursegradle.CurseUploadTask>? = null

val realChangelog = rootDir.resolve("changelog.md").readText(Charsets.UTF_8)
val modrinthToken: String? = System.getenv("MODRINTH_TOKEN")
val curseforgeToken: String? = System.getenv("CURSEFORGE_TOKEN")

if (modrinthToken != null) {
    modrinthForgeTask = tasks.register<com.modrinth.minotaur.TaskModrinthUpload>("publishModrinthForge") {
        dependsOn(project(":forge").tasks.getByName("minJar"))

        detectLoaders = false
        changelog = realChangelog
        token = modrinthToken
        projectId = properties["modrinth_project_id"] as String
        versionName = "Forge ${properties["mod_version"]}"
        versionNumber = "${properties["mod_version"]}-forge"
        versionType = VersionType.RELEASE
        uploadFile = project(":forge").tasks.getByName("minJar")
        addGameVersion(properties["minecraft_version"] as String)
        addLoader("forge")
    }

    modrinthFabricTask = tasks.register<com.modrinth.minotaur.TaskModrinthUpload>("publishModrinthFabric") {
        dependsOn(project(":fabric").tasks.getByName("minJar"))
        mustRunAfter(modrinthForgeTask)

        detectLoaders = false
        changelog = realChangelog
        token = modrinthToken
        projectId = properties["modrinth_project_id"] as String
        versionName = "Fabric ${properties["mod_version"]}"
        versionNumber = "${properties["mod_version"]}-fabric"
        versionType = VersionType.RELEASE
        uploadFile = project(":fabric").tasks.getByName("minJar")
        addGameVersion(properties["minecraft_version"] as String)
        addLoader("fabric")
    }
}

if (curseforgeToken != null) {
    curseforgeForgeTask = tasks.register<com.matthewprenger.cursegradle.CurseUploadTask>("publishCurseforgeForge") {
        dependsOn(project(":forge").tasks.getByName("minJar"))

        apiKey = curseforgeToken
        projectId = properties["curseforge_project_id"] as String
        mainArtifact = CurseArtifact().apply {
            artifact = project(":forge").tasks.getByName("minJar")
            changelogType = "markdown"
            changelog = realChangelog
            displayName = "[Forge - ${properties["minecraft_version"]}] ${properties["mod_version"]}"
            releaseType = "release"
            gameVersionStrings = listOf(properties["minecraft_version"], "Forge", "Java ${properties["mod_java_version"]}")
        }
        additionalArtifacts = listOf()
    }

    curseforgeFabricTask = tasks.register<com.matthewprenger.cursegradle.CurseUploadTask>("publishCurseforgeFabric") {
        dependsOn(project(":fabric").tasks.getByName("minJar"))
        mustRunAfter(curseforgeForgeTask)

        apiKey = curseforgeToken
        projectId = properties["curseforge_project_id"] as String
        mainArtifact = CurseArtifact().apply {
            artifact = project(":fabric").tasks.getByName("minJar")
            changelogType = "markdown"
            changelog = realChangelog
            displayName = "[Fabric - ${properties["minecraft_version"]}] ${properties["mod_version"]}"
            releaseType = "release"
            gameVersionStrings = listOf(properties["minecraft_version"], "Fabric", "Java ${properties["mod_java_version"]}")
        }
        additionalArtifacts = listOf()
    }
}

val publishTask = tasks.create("publish") {
    listOf(modrinthForgeTask, modrinthFabricTask, curseforgeForgeTask, curseforgeFabricTask).forEach {
        if (it != null) {
            this.dependsOn(it)
            this.mustRunAfter(it)
        }
    }
}
