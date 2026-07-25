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
```

**Configuration options:**

- `selfKill`: If true, player will get his head after self kill
- `playerKill`: If true, player will get his head after kill by another player
- `otherDeaths`: If true, player will get his head after all other deaths

You can also edit these options in-game from the [Mod Menu](https://modrinth.com/mod/modmenu) config screen (Cloth Config is bundled).


## Installation

1. Download the latest release of Simple Player Heads from [here](https://modrinth.com/mod/simple-player-heads).
2. Place the `.jar` file in your `mods` folder.

## Building

`./gradlew build` builds a jar per supported version into `versions/<version>/build/libs/`.
The yarn range (1.18.2–1.21.8) builds on JDK 17/21; Minecraft 26+ is unobfuscated and
only builds on JDK 25.

## Releasing

CI publishes automatically on a `v*` tag (see `.github/workflows/release.yml`):
it attaches every version's jar to a GitHub Release and publishes each to Modrinth.

One-time setup:

1. Create the project on [Modrinth](https://modrinth.com) and set `modrinth_id` in
   `gradle.properties` to its id or slug (it is public, safe to commit).
2. Add a repository Actions secret `MODRINTH_TOKEN` — a Modrinth PAT with the
   "Create versions" scope.
3. Cut a release by pushing a tag, e.g. `git tag v1.0.2 && git push origin v1.0.2`.