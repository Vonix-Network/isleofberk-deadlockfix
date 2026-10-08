# IsleOfBerk DeadlockFix

A Forge 1.18.2 compatibility companion for the discontinued **Isle of Berk 1.2.0** mod by GACMD. It contains two narrowly scoped protections:

1. the original `Worker-Main` world-generation deadlock guard; and
2. an optional IoB Variant Loader 2.6.4 ownerless follow-goal guard.

The regular published artifact is IoB-only by default. The Variant Loader adapter
is included only when building with `-PincludeVariantLoader=true` and is not part
of the regular release artifact.

This project does not bundle or redistribute Isle of Berk, Variant Loader, or GeckoLib.

## Confirmed failure paths

### World-generation deadlock

`ADragonBase.isWaterBelow()` can read a neighboring chunk during `SpeedStinger.checkSpawnRules` while running on a Forge world-generation worker. The cross-chunk `ServerChunkCache.getChunk(...).join()` cycle can block workers until the server watchdog terminates the process.

The existing fix returns `false` only when the current thread name begins with `Worker-Main`. This is intentionally narrower than a general all-non-server-thread rule; normal server-thread dragon AI remains unchanged.

### Optional Variant Loader ownerless follow goal

IoB Variant Loader 2.6.4 can tick `AltFollowGoal` after its `ADragonBase` owner is unavailable. The resulting null owner reaches Minecraft look control/navigation and was observed as a `NullPointerException` while ticking an `isleofberk:skrill`.

The optional mixin targets the exact foreign class and runtime descriptors:

- `nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal`
- `m_8036_()Z` — rejects ownerless activation;
- `m_8037_()V` — stops navigation and cancels an ownerless tick.

Owner-present behavior is untouched. No fallback target is invented. The Variant Loader configuration is separate and soft-required, so installations without Variant Loader retain the original world-generation fix.

The immediate configuration workaround remains:

```toml
alternative_land_navigation = false
```

## Compatibility and support boundary

| Component | Scope |
|---|---|
| Minecraft | 1.18.2 |
| Loader | Forge 40.x |
| Required target | Isle of Berk 1.2.x; live Claws of Berk still ships 1.2.0 |
| **Optional target** | IoB Variant Loader 2.6.4 only until 2.7.0 is separately validated |
| Java | 17 |
| Side | Both; protections are server-oriented |

Live Claws of Berk (checked 2026-08-16) still uses Isle of Berk **1.2.0** and Variant Loader
**2.7.0**. The 1.0.1 `isWaterBelow()` worker-thread guard still matches the live 1.2.0
bytecode. The 1.1.x `AltFollowGoal` ownerless adapter still has a matching class/method
shape on 2.7.0, but that adapter was only validated against 2.6.4 and is not in the
published regular artifact. Treat 2.7.0 as unproven until a dedicated adapter build and
packaged-runtime gate exist.

Variant Loader's publisher documentation states Forge **40.3.12+**, while the live Vonix
server is Forge 40.3.12 on Claws of Berk. Do not treat this as a general Variant Loader
compatibility guarantee. Keep `alternative_land_navigation=false` unless the operator
accepts residual ownerless-follow risk on 2.7.0.

Future Variant Loader builds are unsupported unless separately validated; the metadata
range remains limited to the exact 2.6.4 release until a 2.7.0 pin exists.

Unrelated `AcquirePoi` and VonixGuardian watchdog paths are intentionally out of scope.

## Installation and rollback

1. Use Forge 1.18.2 with Java 17.
2. Install Isle of Berk 1.2.0 and this companion in `mods/`.
3. The regular release does not require IoB Variant Loader. Build the optional adapter separately with `-PincludeVariantLoader=true` only for a validated Variant Loader installation.
4. Back up the existing companion JAR and configuration, then restart safely.
5. Confirm the startup log and Mixin application lines before testing gameplay.

Rollback: stop the server safely, restore the prior `isleofberk-deadlockfix` JAR (v1.0.1 for the original guard), and restart. If the Variant Loader path is the only concern, set `alternative_land_navigation=false` and restart; this is reversible and does not alter world data.

## Build and validation

The source and artifact must be rebuilt after every final change. Public release is gated on:

- `./gradlew clean build --no-daemon` under Java 17;
- readable user-facing JAR with `mods.toml`, the regular mixin config, refmap, and classes;
- no embedded upstream packages;
- production-parity boot with the exact target jars;
- Mixin apply evidence for `ADragonBaseMixin`; the optional adapter requires its own build and runtime evidence;
- owner-present/ownerless behavior testing where a reproducible fixture is available.

The local validation jars are developer-supplied, gitignored, and never published. The release workflow downloads pinned Modrinth files and records their SHA-512 values before building. Do not place credentials or upstream JARs in Git.

```bash
./gradlew clean build --no-daemon
# Output: build/libs/isleofberk-deadlockfix-1.1.2.jar
```

## Release status

Published / live fleet: **v1.0.1** (`isleofberk-deadlockfix-1.0.1.jar`). That is the
current Claws of Berk companion and still covers the required IoB 1.2.0 worldgen guard.

Local worktree: **1.1.2** source and docs, untagged and unpublished. `v1.1.0` exists on
GitHub only as a prerelease and still bundled the 2.6.4-only Variant Loader adapter.

Do not replace live 1.0.1 with 1.1.x until a 2.7.0-validated adapter decision is made.
A successful compile or startup is not a performance claim.

## Credits and license

Bug discovery and companion implementation: Vonix.Network team.

MIT — see [LICENSE](LICENSE).
