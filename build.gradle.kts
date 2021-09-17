plugins {
    java
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
