# IsleOfBerk DeadlockFix

A tiny Forge 1.18.2 compatibility mod that fixes a **server-crash chunk-load deadlock** in the [Isle of Berk](https://www.curseforge.com/minecraft/mc-mods/isleofberk) HTTYD dragon mod by the **GACMD** team (Ghanou Craft, Apprentice Necromancer, TooTHleZZ & Arsian).

> ℹ️ Isle of Berk itself is officially **discontinued** — this patch is for servers still running the final `isleofberk-1.2.0` jar on 1.18.2 modpacks such as [Isle of Berk (Claws of Berk)](https://www.curseforge.com/minecraft/modpacks/new-berks-dawn) by kesselot.

## The Bug

`ADragonBase.isWaterBelow()` is invoked from `SpeedStinger.checkSpawnRules` (`m_5610_`) during chunk generation. The method performs a `Level.getBlockState(BlockPos)` read against the entity's full `ServerLevel`.

When this happens on a worldgen worker thread (`Worker-Main-N`) and the target block is in a **neighbouring chunk that has not finished generating**, `ServerChunkCache.getChunk(...).join()` blocks indefinitely:

```
Worker-Main-3  →  needs chunk B  →  chunk B is generating on Worker-Main-7
Worker-Main-7  →  needs chunk A  →  chunk A is generating on Worker-Main-3
                                    ↓
                          DEADLOCK
                                    ↓
        net.minecraft.server.MinecraftServer$ServerHangWatchdog fires
        after 60 s and crashes the server.
```

Symptom in logs:
```
[Server Watchdog/FATAL] [net.minecraft.server.MinecraftServer/]:
  A single server tick took 60.00 seconds (should be max 0.05)
```
…with a stack trace landing in `ServerChunkCache.getChunk` while a `Worker-Main` thread is parked inside `SpeedStinger.checkSpawnRules → ADragonBase.isWaterBelow`.

## The Fix

`ADragonBaseMixin` `@Inject`s the `HEAD` of `isWaterBelow()`. If the current thread name begins with `Worker-Main`, it short-circuits to `false`. Worldgen spawn checks are the only off-main caller of this method; live AI uses (`DragonSleepGoal`, `AIDragonLand`, etc.) all run on `Server thread` and are unaffected.

`@Pseudo` is used because the target class lives in a separate jar (`isleofberk-1.2.0.jar`) and may not be present at refmap-generation time.

When IoB Variant Loader 2.7.0 is installed, the optional `AltFollowGoalMixin`
also rejects ownerless follow-goal activation and cancels an ownerless tick
before it reaches Minecraft navigation. Owner-present behavior is unchanged.

## Compatibility

| | |
|---|---|
| **Minecraft** | 1.18.2 |
| **Forge** | 40.x+ |
| **Loader** | Forge (FML) |
| **Side** | Both (server-side fix; client copy is harmless) |
| **Required mod** | Isle of Berk 1.2.x (validated with `isleofberk-1.2.0.jar`) |
| **Optional mod** | IoB Variant Loader 2.7.0 |

This companion only targets Isle of Berk and its optional Variant Loader follow-goal integration; it does not bundle or replace either upstream mod and is safe to add to a 1.18.2 modpack that includes Isle of Berk.

## Installation

1. Download `isleofberk-deadlockfix-1.0.2.jar` from the [Releases](../../releases) page.
2. Drop it into your server's `mods/` folder alongside `isleofberk-1.2.0.jar`.
3. If using IoB Variant Loader 2.7.0, keep it installed alongside the companion.
4. Restart the server.

You should see this line during startup:
```
[network.vonix.isleofberkdeadlockfix.IsleOfBerkDeadlockFix/]:
  [IoB-DeadlockFix] Core worldgen guard loaded; Variant Loader 2.7.0 ownerless-follow guard is optional.
```

## Building

```bash
./gradlew build
# Output: build/libs/isleofberk-deadlockfix-1.0.2.jar
```

Requires JDK 17, internet access for ForgeGradle's first build.

## Credits

- **Bug discovery + fix authored by** the [Vonix Network](https://vonix.network) team via Hermes Agent (Claude Opus 4.7).
- Originally deployed on our [Isle of Berk (Claws of Berk)](https://www.curseforge.com/minecraft/modpacks/new-berks-dawn) Minecraft server to fix a recurring crash that was triggered every time a player flew into ungenerated terrain on a SpeedStinger spawn cell.

## License

MIT — see [LICENSE](LICENSE).
