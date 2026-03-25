# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Minecraft mod that prevents Nether portal creation. Originally by Gigabit101 for the "Monumental Experience" modpack. Blocks portals via two mechanisms: cancelling `BlockEvent.PortalSpawnEvent` and intercepting player right-clicks on obsidian with portal-lighting items.

Supports multiple Minecraft versions and loaders via Stonecutter:

| Variant | Loader | MC | Java |
|---|---|---|---|
| `1.18.2-forge` | MinecraftForge 40.x | 1.18.2 | 17 |
| `1.19.2-forge` | MinecraftForge 43.x | 1.19.2 | 17 |
| `1.20.1-forge` | MinecraftForge 47.x | 1.20.1 | 17 |
| `1.20.1-neoforge` | NeoForge 47.1.x | 1.20.1 | 17 |
| `1.21.1-neoforge` | NeoForge 21.1.x | 1.21.1 | 21 |
| `26.1-neoforge` | NeoForge 26.x (alpha) | 26.1 | 25 |

## Build Commands

```bash
./gradlew chiseledBuild             # Build JARs for ALL variants
./gradlew :1.21.1-neoforge:build    # Build a single variant
./gradlew :1.21.1-neoforge:runClient  # Launch Minecraft client (active version)
./gradlew :1.21.1-neoforge:runServer  # Launch Minecraft server (active version)
```

No test suite exists. Manual testing is done via `runClient`/`runServer`.

## Architecture

Two-class mod in `com.cyberday1.netherportalnomore`:

- **NetherPortalNoMore.java** — Entry point annotated with `@Mod`. No-op beyond registration.
- **PortalBlocker.java** — Static event handlers via `@EventBusSubscriber`. Two blocking strategies:
  1. `onPortalSpawn()` — Cancels `PortalSpawnEvent` to block world-generated portals.
  2. `onRightClickBlock()` — Cancels player interactions when using flint-and-steel, fire charges, or tagged igniters on obsidian.

Both files use Stonecutter preprocessor directives (`//? if`) for cross-version/cross-loader compatibility.

## Project Structure (Stonecutter)

- **`settings.gradle.kts`** — Stonecutter plugin config, defines all version variants and routes to loader-specific build scripts via `mapBuilds`.
- **`stonecutter.gradle.kts`** — Controller script. Declares preprocessor constants (`forge`, `neoforge`, `standalone_ebs`, `neo_mods_toml`) and the `mc` dependency for version predicates.
- **`neoforge.gradle.kts`** — Build script for NeoForge variants. Uses `net.neoforged.moddev` (>=1.21) or `net.neoforged.moddev.legacyforge` (<1.21).
- **`forge.gradle.kts`** — Build script for Forge variants. Uses `net.neoforged.moddev.legacyforge`.
- **`gradle.properties`** — Shared properties (mod_id, mod_version, group).
- **`versions/<variant>/gradle.properties`** — Per-variant properties (mc_version, loader version, Java version, version ranges, pack_format).

## Key Configuration

- **`src/main/resources/META-INF/neoforge.mods.toml`** — Template for NeoForge >=1.21.
- **`src/main/resources/META-INF/mods.toml`** — Template for Forge and NeoForge <1.21.
- Both use `${placeholder}` syntax expanded by `processResources`.
- **`src/main/resources/pack.mcmeta`** — Uses `${pack_format}` placeholder.

## Stonecutter Preprocessor

Source files use Stonecutter comment directives:
- `//? if mc: >=1.21 { ... //?}` — Version-gated code blocks
- `//? if mc: <1.19` — Handles `event.world` vs `event.level` package rename and `getWorld()` vs `getLevel()`
- `//? if mc: <1.21 { ... //?} else { ... //?}` — Forge vs NeoForge import blocks
- `//? if standalone_ebs` — NeoForge >=1.21 uses standalone `@EventBusSubscriber`
- Disabled code is wrapped in `/* */` comments

## Development Notes

- Gradle 9.4.1 with Stonecutter 0.9 plugin.
- NeoForge uses annotation-driven event subscription — handlers must be `static` in `@EventBusSubscriber` classes.
- For MC >=1.21, the mod checks portal-lighting items using `ItemTags.CREEPER_IGNITERS` and `Tags.Items.TOOLS_IGNITER`. For <1.21, only `instanceof` checks are used.
- The VCS version (checked into git) is `1.21.1-neoforge`.
