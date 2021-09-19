plugins {
    alias(libs.plugins.fabricLoom)
    `maven-publish`
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
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
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
    maven {
        name = "Siphalor's Maven"
        url = uri("https://maven.siphalor.de/")
    }
    maven {
        name = "Devan Maven"
        url = uri("https://storage.googleapis.com/devan-maven/")
    }
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

val excludeLoader: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc", "fabric-loader")
}

dependencies {
    minecraft(libs.minecraft.fabric)
    mappings("net.fabricmc:yarn:1.17.1+build.61:v2")

    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.api)

    if (rootDir.resolve("build/fabric/libs/ninjaphenix-container-library-${properties["mod_version"]}+${properties["minecraft_version"]}-fat.jar").exists()) {
        modImplementation(this.project(":fabric"), excludeFabric)
    }

    modCompileOnly(libs.rei.api, excludeFabric)
    modRuntimeOnly(libs.rei.asProvider(), excludeFabric)

    modCompileOnly(libs.modmenu, excludeFabric)
    modRuntimeOnly(libs.modmenu, excludeFabric)

    modCompileOnly(libs.amecs.api, excludeFabric)

    // Used for tests only, added to main class path as loom doesn't have a test source set equivalent
    modCompileOnly(libs.arrp, excludeFabric)
    modRuntimeOnly(libs.arrp, excludeFabric)
}
