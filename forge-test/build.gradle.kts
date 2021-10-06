plugins {
    alias(libs.plugins.gradle.forge)
    alias(libs.plugins.gradle.mixin)
}

minecraft {
    mappings("official", properties["minecraft_version"] as String)

    runs {
        create("client") {
            workingDirectory(rootProject.file("run"))
            mods {
                create("ninjaphenix_container_lib_test") {
                    source(sourceSets.main.get())
                }
            }
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            //property("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            //property("forge.logging.console.level", "debug")
        }

        create("server") {
            workingDirectory(rootProject.file("run"))
            mods {
                create("ninjaphenix_container_lib_test") {
                    source(sourceSets.main.get())
                }
            }
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            //property("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            //property("forge.logging.console.level", "debug")
        }
    }
}

repositories {
    flatDir {
        name = "Deps with no redistribute perms"
        dir(rootDir.resolve("no_perm_deps"))
    }
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "${properties["minecraft_version"]}-${properties["forge_version"]}")
    implementation(group = "org.spongepowered", name = "mixin", version = properties["mixin_version"] as String)
    annotationProcessor(group = "org.spongepowered", name = "mixin", version = properties["mixin_version"] as String, classifier = "processor")

    implementation(group = "org.jetbrains", name = "annotations", version = properties["jetbrains_annotations_version"] as String)
    implementation(project(":forge", configuration = "dev"))
    runtimeOnly(fg.deobf("local:flan-1.16.5:${properties["flan_version"]}-forge"))
}
