plugins {
    id("fabric-loom")
}

val excludeFabric: (ModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
    it.exclude("net.fabricmc.fabric-api")
}

repositories {
    mavenLocal()
}

dependencies {
    modCompileOnly(group = "org.anti-ad.mc", name = "inventory-profiles-next", version = "fabric-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}") {
        excludeFabric(this)
        exclude(group = "com.terraformersmc")
    }
}
