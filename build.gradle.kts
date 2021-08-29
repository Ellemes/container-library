plugins {
    java
}

subprojects {
    apply(plugin = "java")

    group = properties["maven_group"] as String
    version = properties["mod_version"] as String
    base.archivesName.set(properties["archives_base_name"] as String)
    buildDir = rootDir.resolve("build/${project.name}")

    java {
        sourceCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
        targetCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
    }

    repositories {
        flatDir { // Cannot use exclusive content as forge does not change the artifact group like fabric does.
            name = "Local Dependencies"
            dir(rootDir.resolve("local_dependencies"))
        }

    }

    // todo: make api source set
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
