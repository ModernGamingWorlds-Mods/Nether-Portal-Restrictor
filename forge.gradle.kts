plugins {
    java
    id("net.neoforged.moddev.legacyforge")
}

// Version-specific properties
val modId: String = property("mod_id") as String
val modVersion: String = providers.environmentVariable("VERSION").getOrElse(property("mod_version") as String)
val modName: String = property("mod_name") as String
val modAuthor: String = property("mod_author") as String
val license: String = property("license") as String
val modDescription: String = property("mod_description") as String
val mcVersion: String = property("mc_version") as String
val modLoader: String = property("mod_loader") as String
val forgeVersion: String = property("forge_version") as String
val javaVersion: String = property("java_version") as String

// Properties expanded into resource templates (mods.toml, pack.mcmeta, etc.)
val expandProps = mapOf(
    "mod_id" to modId,
    "mod_version" to modVersion,
    "mod_name" to modName,
    "mod_author" to modAuthor,
    "license" to license,
    "mod_description" to modDescription,
    "mc_version_range" to (property("mc_version_range") as String),
    "loader_version_range" to (property("loader_version_range") as String),
    "loader_dep_id" to (property("loader_dep_id") as String),
    "loader_dep_version_range" to (property("loader_dep_version_range") as String),
    "pack_format" to (property("pack_format") as String),
    "min_format" to ((findProperty("min_format") ?: property("pack_format")) as String),
    "max_format" to ((findProperty("max_format") ?: property("pack_format")) as String)
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

legacyForge {
    version = forgeVersion
    runs {
        register("client") { client() }
        register("server") { server() }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
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

    filesMatching("META-INF/mods.toml") { expand(expandProps) }
    exclude("META-INF/neoforge.mods.toml")

    filesMatching("pack.mcmeta") { expand(expandProps) }
}

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to modAuthor,
            "Specification-Version" to "1",
            "Implementation-Title" to modId,
            "Implementation-Version" to modVersion,
            "Implementation-Vendor" to modAuthor
        )
    }
}
