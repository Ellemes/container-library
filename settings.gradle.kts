enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.toString() == "net.minecraftforge.gradle") {
                useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
            } else if (requested.id.toString() == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "SpongePowered"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://jitpack.io")
                    name = "JitPack"
                }
            }
            filter {
                includeGroup("com.gitlab.ninjaphenix")
                includeGroup("com.gitlab.ninjaphenix.gradle-utils")
            }
        }
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "container-library"

include("fabric")
include("fabric-test")
//include("forge")
//include("forge-test")
