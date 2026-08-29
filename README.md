# IsleOfBerk DeadlockFix

A tiny Forge 1.18.2 compatibility companion for the [Isle of Berk](https://www.curseforge.com/minecraft/mc-mods/isleofberk) HTTYD dragon mod by the **GACMD** team (Ghanou Craft, Apprentice Necromancer, TooTHleZZ & Arsian).

> ℹ️ Isle of Berk itself is officially **discontinued** — this patch is for servers still running the final `isleofberk-1.2.0` jar on 1.18.2 modpacks such as [Isle of Berk (Claws of Berk)](https://www.curseforge.com/minecraft/modpacks/new-berks-dawn) by kesselot.

This companion does not bundle or replace Isle of Berk or IoB Variant Loader. Keep the original `isleofberk-1.2.0.jar` installed, and keep IoB Variant Loader 2.7.0 installed if you use that optional integration.

`1.0.3` is a patch-level correction on the already released `1.0.2` Variant Loader 2.7.0 lane. It is not the separate GitHub prerelease `v1.1.0`, which targets Variant Loader 2.6.4 on `main`.

## Evidence boundary (2026-08-29)

Two production events from 2026-08-29 are **separate**. Do not collapse them into one cause or one fix.

### Confirmed: 12:40:03 Variant Loader 2.7.0 `AltFollowGoal` owner-null NPE

When IoB Variant Loader 2.7.0 is installed, `AltFollowGoal` can remain active after the dragon owner is unavailable. The ownerless tick reaches Minecraft look control / navigation and was observed as a `NullPointerException`.

`1.0.3` keeps the optional `AltFollowGoalMixin` guard from the accepted `1.0.2` candidate and corrects Mixin remapping for the production `m_8036_()` / `m_8037_()` selectors so those Goal members resolve through the refmap:

- ownerless `canUse` (`m_8036_()Z`) returns `false`;
- an ownerless tick (`m_8037_()V`) stops navigation and cancels before look control / navigation receive a null owner;
- owner-present behavior is unchanged.

No in-world owner-loss reproduction was performed for this companion. Runtime evidence for the accepted candidate covers mixin application and boot readiness only. This release does not claim a quantitative performance improvement.

### Separate and unresolved: 12:41:06 ServerHangWatchdog

A later `ServerHangWatchdog` event at **12:41:06** is a different report. The supplied watchdog dump has **no captured Server thread, Worker-Main, IoB, AI, chunk, or navigation causal stack**, so the hang cause remains **unresolved**.

`1.0.3` does **not** claim a fix for that 12:41:06 watchdog event.

## Retained historical worldgen guard

`ADragonBaseMixin` `@Inject`s the `HEAD` of `ADragonBase.isWaterBelow()`. If the current thread name begins with `Worker-Main`, it short-circuits to `false`.

That mixin is **retained historical / compatibility behavior** from the original 1.0.0 worldgen-spawn path (`SpeedStinger.checkSpawnRules` calling `isWaterBelow()` during chunk generation). It is **not** a proven fix for the supplied 12:41:06 watchdog report.

`@Pseudo` is used because the target class lives in a separate jar (`isleofberk-1.2.0.jar`) and may not be present at refmap-generation time.

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

1. Download `isleofberk-deadlockfix-1.0.3.jar` from the [Releases](../../releases) page.
2. Drop it into your server's `mods/` folder alongside the original `isleofberk-1.2.0.jar`.
3. If using IoB Variant Loader 2.7.0, keep the original Variant Loader jar installed alongside this companion.
4. Restart the server.

You should see this line during startup:
```
[network.vonix.isleofberkdeadlockfix.IsleOfBerkDeadlockFix/]:
  [IoB-DeadlockFix] Core worldgen guard loaded; Variant Loader 2.7.0 ownerless-follow guard is optional.
```

## Building

```bash
./gradlew build
# Output: build/libs/isleofberk-deadlockfix-1.0.3.jar
```

Requires JDK 17, internet access for ForgeGradle's first build.

## Credits

- **Bug discovery + fix authored by** the [Vonix Network](https://vonix.network) team via Hermes Agent (Claude Opus 4.7).
- Originally deployed on our [Isle of Berk (Claws of Berk)](https://www.curseforge.com/minecraft/modpacks/new-berks-dawn) Minecraft server to address a recurring worldgen-path crash on SpeedStinger spawn cells. That historical deployment is not evidence that the separate 12:41:06 watchdog is resolved.

## License

MIT — see [LICENSE](LICENSE).
