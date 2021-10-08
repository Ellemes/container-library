plugins {
    alias(libs.plugins.gradle.loom)
}

loom {
    runs {
        named("client") {
            ideConfigGenerated(false)
        }
        named("server") {
            ideConfigGenerated(false)
            serverWithGui()
        }
    }
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
    // For ARRP
    maven {
        name = "Devan Maven"
        url = uri("https://storage.googleapis.com/devan-maven/")
    }
    maven {
        name = "Flemmli97"
        url = uri("https://gitlab.com/api/v4/projects/21830712/packages/maven")
    }
    mavenLocal()
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = properties["minecraft_version"] as String)
    mappings(group = "net.fabricmc", name = "yarn", version = "${properties["minecraft_version"]}+build.${properties["yarn_version"]}", classifier = "v2")

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = properties["fabric_loader_version"] as String)
    implementation(group = "org.jetbrains", name = "annotations", version = properties["jetbrains_annotations_version"] as String)

    implementation(project(":fabric", "dev"), action = excludeFabric)

    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = properties["fabric_api_version"] as String)

    modRuntimeOnly(group = "me.shedaniel", name = "RoughlyEnoughItems-fabric", version = properties["rei_version"] as String, dependencyConfiguration = excludeFabric)

    modRuntimeOnly(group = "com.terraformersmc", name = "modmenu", version = properties["modmenu_version"] as String, dependencyConfiguration = excludeFabric)

    modImplementation(group = "net.devtech", name = "arrp", version = properties["arrp_version"] as String, dependencyConfiguration = excludeFabric)

    modRuntimeOnly(group = "io.github.flemmli97", name = "flan", version = "1.17.1-${properties["flan_version"]}") {
        also(excludeFabric)
        isTransitive = false
    }
}
