import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("ellemes.gradle.mod").apply(false)
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

fun excludeFabric(it: ModuleDependency) {
    it.exclude(group = "net.fabricmc")
    it.exclude(group = "net.fabricmc.fabric-api")
}

mod {
    fabricApi(
        "fabric-resource-loader-v0", // Required for resources like keybind text
        "fabric-registry-sync-v0", // Required to delay registry freezing
        "fabric-networking-api-v1",
        "fabric-screen-handler-api-v1",
        "fabric-key-binding-api-v1",
        "fabric-transitive-access-wideners-v1",
        //"fabric-screen-api-v1" // Mod menu
    )
}

dependencies {
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${properties["rei_version"]}") {
        excludeFabric(this)
    }

    modCompileOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}") {
        excludeFabric(this)
    }

    modCompileOnly("de.siphalor:amecsapi-1.19:${properties["amecs_version"]}") {
        excludeFabric(this)
        exclude(group = "com.github.astei")
    }

    modCompileOnly("io.github.flemmli97:flan:1.18.2-${properties["flan_version"]}:fabric-api") {
        excludeFabric(this)
        exclude(group = "curse.maven")
    }

    modCompileOnly("maven.modrinth:inventory-profiles-next:fabric-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}") {
        excludeFabric(this)
    }
}

val releaseModTask = tasks.getByName("releaseMod")
val modVersion = properties["mod_version"] as String
val modReleaseType = if ("alpha" in modVersion) "alpha" else if ("beta" in modVersion) "beta" else "release"
var modChangelog = rootDir.resolve("changelog.md").readText(Charsets.UTF_8)
val modTargetVersions = mutableListOf(properties["minecraft_version"] as String)
val modUploadDebug = System.getProperty("MOD_UPLOAD_DEBUG", "false") == "true" // -DMOD_UPLOAD_DEBUG=true

fun String.execute() = org.codehaus.groovy.runtime.ProcessGroovyMethods.execute(this)
val Process.text: String? get() = org.codehaus.groovy.runtime.ProcessGroovyMethods.getText(this)
val commit = "git rev-parse HEAD".execute().text
modChangelog += "\nCommit: https://github.com/Ellemes/container-library/commit/$commit"

(properties["extra_game_versions"] as String).split(",").forEach {
    if (it != "") {
        modTargetVersions.add(it)
    }
}

curseforge {
    options(closureOf<me.hypherionmc.cursegradle.Options> {
        debug = modUploadDebug
        javaVersionAutoDetect = false
        javaIntegration = false
        forgeGradleIntegration = false
        fabricIntegration = false
        detectFabricApi = false
    })

    project(closureOf<me.hypherionmc.cursegradle.CurseProject> {
        apiKey = System.getenv("CURSEFORGE_TOKEN")
        id = properties["curseforge_project_id"]
        releaseType = modReleaseType
        mainArtifact(tasks.getByName("minJar"), closureOf<me.hypherionmc.cursegradle.CurseArtifact> {
            displayName = project.name.capitalized() + " " + modVersion
            artifact = tasks.getByName("minJar")
        })
        relations(closureOf<me.hypherionmc.cursegradle.CurseRelation> {
            requiredDependency("fabric-api")
            optionalDependency("roughly-enough-items")
            optionalDependency("inventory-profiles-next")
        })
        changelogType = "markdown"
        changelog = modChangelog
        gameVersionStrings = listOf(project.name.capitalized(), "Java " + java.targetCompatibility.majorVersion) + modTargetVersions
    })
}

modrinth {
    debugMode.set(modUploadDebug)
    detectLoaders.set(false)

    projectId.set(properties["modrinth_project_id"] as String)
    versionType.set(modReleaseType)
    versionNumber.set(modVersion  + "+" + project.name)
    versionName.set(project.name.capitalized() + " " + modVersion)
    uploadFile.set(tasks.getByName("minJar"))
    dependencies {
        required.project("P7dR8mSH") // fabric-api
        // optional.project("roughly-enough-items") // roughly-enough-items ( not on modrinth )
        optional.project("O7RBXm3n") // inventory-profiles-next
    }
    changelog.set(modChangelog)
    gameVersions.set(modTargetVersions)
    loaders.set(listOf(project.name))
}

afterEvaluate {
    releaseModTask.finalizedBy(listOf("modrinth", "curseforge" + properties["curseforge_project_id"]))
}
