plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.141" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.141" apply false
}

stonecutter active "1.21.1-neoforge" /* [SC] DO NOT EDIT */

stonecutter parameters {
    val loader = node.metadata.project.substringAfterLast('-')
    constants.match(loader, "forge", "neoforge")
    constants["standalone_ebs"] = node.metadata.version.let {
        stonecutter.eval(it, ">=1.21")
    }
    constants["neo_mods_toml"] = node.metadata.version.let {
        stonecutter.eval(it, ">=1.21")
    } && loader == "neoforge"

    dependencies["mc"] = node.metadata.version
}

// Aggregate task to build all versions
tasks.register("chiseledBuild") {
    group = "project"
    dependsOn(stonecutter.versions.map { ":${it.project}:build" })
}
