plugins {
    java
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

    // todo: produce an api jar somehow
    sourceSets {
        //create("api") {
        //    java {
        //        setSrcDirs(listOf(rootDir.resolve("common/${project.name}Src/api/java")))
        //    }
        //    resources {
        //        setSrcDirs(listOf<File>())
        //    }
        //}

        main {
            java {
                setSrcDirs(listOf(
                        "src/main/java",
                        rootDir.resolve("common/${project.name}Src/main/java"),
                        rootDir.resolve("common/${project.name}Src/api/java")
                ))
            }
            resources {
                setSrcDirs(listOf(
                        "src/main/resources",
                        rootDir.resolve("common/${project.name}Src/main/resources")
                ))
            }
            //compileClasspath += sourceSets["api"].output
            //runtimeClasspath += sourceSets["api"].output
        }

        test {
            java {
                setSrcDirs(listOf(
                        "src/test/java",
                        rootDir.resolve("common/${project.name}Src/test/java")
                ))
            }
            resources {
                setSrcDirs(listOf(
                        "src/test/resources",
                        rootDir.resolve("common/${project.name}Src/test/resources")
                ))
            }
            compileClasspath += this@sourceSets["main"].compileClasspath
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

tasks.register("buildMod") {
    subprojects.forEach {
        dependsOn(it.tasks["build"])
    }
}

tasks.register("publishToMavenLocal") {
    subprojects.forEach {
        dependsOn(it.tasks["publishToMavenLocal"])
    }
}
