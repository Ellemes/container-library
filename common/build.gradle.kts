dependencies {
    modCompileOnly("maven.modrinth:inventory-profiles-next:fabric-${rootProject.properties["ipn_minecraft_version"]}-${rootProject.properties["ipn_version"]}") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }
}
