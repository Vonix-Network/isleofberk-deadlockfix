# IsleOfBerk DeadlockFix

A tiny Forge 1.18.2 compatibility mod that fixes a **server-crash chunk-load deadlock** in the [Isle of Berk](https://www.curseforge.com/minecraft/mc-mods/isleofberk) HTTYD dragon mod by Aibreann (GACMD).

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

## Compatibility

| | |
|---|---|
| **Minecraft** | 1.18.2 |
| **Forge** | 40.x+ |
| **Loader** | Forge (FML) |
| **Side** | Both (server-side fix; client copy is harmless) |
| **Required mod** | `isleofberk` ≥ 1.2.0 |

This mod **only patches IsleOfBerk** — it does not interact with any other mod and is safe to add to any 1.18.2 modpack that includes IsleOfBerk.

## Installation

1. Download `isleofberk-deadlockfix-1.0.0.jar` from the [Releases](../../releases) page.
2. Drop it into your server's `mods/` folder alongside `isleofberk-1.2.0.jar`.
3. Restart the server.

You should see this line during startup:
```
[network.vonix.isleofberkdeadlockfix.IsleOfBerkDeadlockFix/]:
  [IoB-DeadlockFix] Loaded. Mixin will short-circuit ADragonBase.isWaterBelow() during off-main-thread worldgen.
```

## Building

```bash
./gradlew build
# Output: build/libs/isleofberk-deadlockfix-1.0.0.jar
```

Requires JDK 17, internet access for ForgeGradle's first build.

## Credits

- **Bug discovery + fix authored by** the [Vonix Network](https://vonix.network) team via Hermes Agent (Claude Opus 4.7).
- Originally deployed on the [Isle of Berk (Claws of Berk)](https://vonix.network) Minecraft server to fix a recurring crash that was triggered every time a player flew into ungenerated terrain on a SpeedStinger spawn cell.

## License

MIT — see [LICENSE](LICENSE).
