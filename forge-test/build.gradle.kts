plugins {
    id("ellemes.gradle.mod").apply(false)
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

dependencies {
    compileOnly(project(":forge", configuration = "dev"))
    compileOnly(group = "org.anti-ad.mc", name = "inventory-profiles-next", version = "forge-${properties["ipn_minecraft_version"]}-${properties["ipn_version"]}")
}
