import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
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
        "fabric-key-binding-api-v1"
    )
}

dependencies {
    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${project.properties["rei_version"]}") {
        excludeFabric(this)
    }

    modCompileOnly("com.terraformersmc:modmenu:${project.properties["modmenu_version"]}") {
        excludeFabric(this)
    }

    //modRuntimeOnly("com.terraformersmc:modmenu:${project.properties["modmenu_version"]}")

    modCompileOnly("de.siphalor:amecsapi-1.18:${project.properties["amecs_version"]}") {
        excludeFabric(this)
        exclude(group = "com.github.astei")
    }

    modCompileOnly("io.github.flemmli97:flan:1.18.2-${project.properties["flan_version"]}:fabric-api") {
        excludeFabric(this)
        exclude(group = "curse.maven")
    }

    modCompileOnly("maven.modrinth:inventory-profiles-next:fabric-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}") {
        excludeFabric(this)
    }
}

val releaseModTask = tasks.getByName("releaseMod")
val modVersion = properties["mod_version"] as String
val modReleaseType = if ("alpha" in modVersion) "alpha" else if ("beta" in modVersion) "beta" else "release"
val modChangelog = rootDir.resolve("changelog.md").readText(Charsets.UTF_8)
val modTargetVersions = mutableListOf(properties["minecraft_version"] as String)
val modUploadDebug = System.getProperty("MOD_UPLOAD_DEBUG", "false") == "true" // -DMOD_UPLOAD_DEBUG=true

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
    dependencies.set(listOf(
        ModDependency("P7dR8mSH", DependencyType.REQUIRED), // fabric-api
        // ModDependency("roughly-enough-items", DependencyType.OPTIONAL), // roughly-enough-items ( not on modrinth )
        ModDependency("O7RBXm3n", DependencyType.OPTIONAL) // inventory-profiles-next
    ))
    changelog.set(modChangelog)
    gameVersions.set(modTargetVersions)
    loaders.set(listOf(project.name))
}

afterEvaluate {
    releaseModTask.dependsOn(tasks.getByName("curseforge" + properties["curseforge_project_id"]))
    releaseModTask.dependsOn(tasks.getByName("modrinth"))
}
