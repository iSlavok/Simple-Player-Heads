# Simple Player Heads

This is a Fabric mod for customizing player head drops. It supports Minecraft **1.18.2 through 26.2** from a single source (built per version with [Stonecutter](https://stonecutter.kikugie.dev/)).

## Features

- **Customizable:** You can adjust when the heads drop.
- Works with pirate mode and other mods to change skins

## Dependencies

This mod requires the following dependencies:

- **[Fabric API](https://modrinth.com/mod/fabric-api)**
- **[Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)**

## Configuration

The default configuration file is `config\simple-player-heads.toml`:

```toml
selfKill = true
playerKill = true
otherDeaths = true

[playerKillLooting]
enabled = false
noLooting = 0.0
looting1 = 0.33
looting2 = 0.67
looting3 = 1.0
```

**Configuration options:**

- `selfKill`: If true, player will get his head after self kill
- `playerKill`: If true, player will get his head after kill by another player
- `otherDeaths`: If true, player will get his head after all other deaths
- `playerKillLooting.enabled`: If true, a player-kill head drop uses the per-Looting-level
  chances below instead of always dropping. Requires `playerKill = true`.
- `playerKillLooting.noLooting` / `looting1` / `looting2` / `looting3`: Drop chance
  (`0.0`–`1.0`) when the killer's weapon has no Looting / Looting I / II / III. Looting
  above III uses `looting3`. Example: `noLooting = 0.0`, `looting3 = 1.0` drops a head
  only when the killer used a Looting III weapon.

You can also edit these options in-game from the [Mod Menu](https://modrinth.com/mod/modmenu) config screen (Cloth Config is bundled).


## Installation

1. Download the latest release of Simple Player Heads from [here](https://modrinth.com/mod/simple-player-heads).
2. Place the `.jar` file in your `mods` folder.

## Server Plugin (Bukkit/Spigot/Paper/Purpur/Folia)

For servers without a Fabric client mod, a Bukkit plugin reproduces the head-drop
behavior. It lives in `plugin/` as a standalone build.

- **Build:** `./gradlew -p plugin build` → `plugin/build/libs/simple-player-heads-plugin-<version>.jar`
- **Run for testing:** `./gradlew -p plugin runServer` (downloads Paper; override the
  Minecraft version with `-Prun_mc=1.20.6`).
- **Install:** download the plugin jar from the same
  [Modrinth project](https://modrinth.com/mod/simple-player-heads) (filter by the
  Bukkit/Paper/Folia loaders) or build it, then drop it in the server's `plugins/` folder.
- **Config:** `plugins/SimplePlayerHeads/config.yml` with the same `selfKill`,
  `playerKill`, `otherDeaths` flags plus a `playerKillLooting` section (`enabled` +
  `noLooting`/`looting1`/`looting2`/`looting3` drop chances). Edit and restart/reload
  the server to apply.

One jar runs Spigot, Paper, Purpur and Folia (it declares `folia-supported: true`).

## Building

`./gradlew build` builds a jar per supported version into `versions/<version>/build/libs/`.
The yarn range (1.18.2–1.21.8) builds on JDK 17/21; Minecraft 26+ is unobfuscated and
only builds on JDK 25.

## Releasing

CI publishes automatically on a `v*` tag (see `.github/workflows/release.yml`):
it attaches every version's jar to a GitHub Release and publishes each to Modrinth.
The released version is taken **from the tag** (`v1.0.2` → `1.0.2`), so you do not
edit `mod_version` for a release — just tag. (`mod_version` in `gradle.properties`
is only the default for local dev builds.)

One-time setup:

1. Create the project on [Modrinth](https://modrinth.com) and set `modrinth_id` in
   `gradle.properties` to its id or slug (it is public, safe to commit).
2. Add a repository Actions secret `MODRINTH_TOKEN` — a Modrinth PAT with the
   "Create versions" scope.

Then release by pushing a tag: `git tag v1.0.2 && git push origin v1.0.2`.