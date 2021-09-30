plugins {
    alias(libs.plugins.fabricLoom)
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
    mavenLocal()
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

dependencies {
    minecraft(libs.minecraft.fabric)
    mappings("net.fabricmc:yarn:1.17.1+build.61:v2")

    modImplementation(libs.fabric.loader)
    implementation(libs.jetbrainAnnotations)

    implementation(project(":fabric", "dev"), excludeFabric)

    modImplementation(libs.fabric.api)

    modRuntimeOnly(libs.rei.asProvider(), excludeFabric)

    modRuntimeOnly(libs.modmenu, excludeFabric)

    modImplementation(libs.arrp, excludeFabric)
}
