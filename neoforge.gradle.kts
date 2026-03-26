val useLegacy = findProperty("use_legacy_moddev") == "true"

plugins {
    java
    id("net.neoforged.moddev") apply false
    id("net.neoforged.moddev.legacyforge") apply false
}

if (useLegacy) {
    apply(plugin = "net.neoforged.moddev.legacyforge")
} else {
    apply(plugin = "net.neoforged.moddev")
}

val modId: String = property("mod_id") as String
val modVersion: String = providers.environmentVariable("VERSION").getOrElse(property("mod_version") as String)
val mcVersion: String = property("mc_version") as String
val modLoader: String = property("mod_loader") as String
val neoVersion: String = property("neo_version") as String
val javaVersion: String = property("java_version") as String
val isModern = stonecutter.eval(stonecutter.current.version, ">=1.21")

val expandProps = mapOf(
    "mod_id" to modId,
    "mod_version" to modVersion,
    "mc_version_range" to (property("mc_version_range") as String),
    "loader_version_range" to (property("loader_version_range") as String),
    "loader_dep_id" to (property("loader_dep_id") as String),
    "loader_dep_version_range" to (property("loader_dep_version_range") as String),
    "pack_format" to (property("pack_format") as String)
)

group = property("group") as String
version = "$modVersion+$modLoader"
base.archivesName.set("$modId-$mcVersion")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    }
    withSourcesJar()
}

repositories {
    mavenLocal()
    maven("https://maven.neoforged.net/releases")
    mavenCentral()
}

if (isModern) {
    extensions.configure<net.neoforged.moddevgradle.dsl.NeoForgeExtension>("neoForge") {
        version = neoVersion
        runs {
            register("client") {
                client()
            }
            register("server") {
                server()
            }
        }
        mods {
            register(modId) {
                sourceSet(sourceSets.main.get())
            }
        }
    }
} else {
    extensions.configure<net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension>("legacyForge") {
        enable {
            neoForgeVersion = neoVersion
        }
        runs {
            register("client") {
                client()
            }
            register("server") {
                server()
            }
        }
        mods {
            register(modId) {
                sourceSet(sourceSets.main.get())
            }
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion.toInt())
}

tasks.processResources {
    inputs.properties(expandProps)

    if (isModern) {
        filesMatching("META-INF/neoforge.mods.toml") { expand(expandProps) }
        exclude("META-INF/mods.toml")
    } else {
        filesMatching("META-INF/mods.toml") { expand(expandProps) }
        exclude("META-INF/neoforge.mods.toml")
    }

    filesMatching("pack.mcmeta") { expand(expandProps) }
}

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to "CyberDay1",
            "Specification-Version" to "1",
            "Implementation-Title" to modId,
            "Implementation-Version" to modVersion,
            "Implementation-Vendor" to "CyberDay1"
        )
    }
}
