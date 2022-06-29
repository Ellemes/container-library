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


dependencies {
    listOf (
            "fabric-resource-loader-v0", // Required for resources like keybind text
            "fabric-registry-sync-v0", // Required to delay registry freezing
            "fabric-networking-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-key-binding-api-v1",
            "fabric-transitive-access-wideners-v1",
            //"fabric-screen-api-v1" // Mod menu
    ).forEach {
        modImplementation(mod.fabricApi().module(it))
    }

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

val u = ellemes.gradle.mod.api.publishing.UploadProperties(project, "https://github.com/Ellemes/container-library")

u.configureCurseForge {
    relations(closureOf<me.hypherionmc.cursegradle.CurseRelation> {
        requiredDependency("fabric-api")
        optionalDependency("roughly-enough-items")
        optionalDependency("inventory-profiles-next")
    })
}

u.configureModrinth {
    dependencies {
        required.project("fabric-api") // P7dR8mSH
        // optional.project("roughly-enough-items") // roughly-enough-items ( not on Modrinth )
        optional.project("inventory-profiles-next") // O7RBXm3n
    }
}
