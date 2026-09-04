# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.4] - 2026-09-04

### Fixed
- Replaced the retained `Worker-Main` thread-name heuristic with an exact
  server-thread identity check before allowing `ADragonBase` block probes to
  run. This preserves normal server-tick and client behavior while covering
  off-main server executors without adding scans, blocking calls, or state.
- Added the same off-main hardening boundary to `ADragonBase.isOnJumpHeight()`.
  This is preventative sibling hardening; no separate production deadlock is
  claimed for that method.

### Changed
- Kept the Variant Loader 2.7.0 ownerless-follow guards unchanged.
- Removed the superseded Simply Swords/WildfireEffect work from this IoB-only
  release line. That fix remains the separate
  `simplyswords-wildfire-fix-0.1.0` companion.

### Evidence boundary
- No additional Isle of Berk deadlock has been confirmed beyond the retained
  `isWaterBelow()` worldgen path.
- This release does not claim a quantitative performance improvement. The
  companion remains a narrow early-return guard with no loops, allocations,
  chunk loads, synchronization, timers, or logging.

## [1.0.3] - 2026-08-29

Patch on the 1.0.x Variant Loader 2.7.0 maintenance line. This is not the
separate GitHub prerelease `v1.1.0`.

### Fixed
- Corrected Mixin remapping for the optional Variant Loader 2.7.0
  `AltFollowGoal` `m_8036_()` / `m_8037_()` selectors so production Goal
  members resolve through the refmap. Ownerless activation still returns
  false; an ownerless tick still stops navigation and cancels before look
  control or navigation receive a null owner. Owner-present behavior is
  unchanged.

### Changed
- Clarified the evidence boundary: the confirmed 2026-08-29 12:40:03
  Variant Loader 2.7.0 `AltFollowGoal` owner-null NPE is separate from the
  12:41:06 ServerHangWatchdog event.
- Documented `ADragonBaseMixin` Worker-Main worldgen guard as retained
  historical / compatibility behavior, not as a proven fix for the supplied
  12:41:06 watchdog report.
- Stated that the 12:41:06 watchdog cause remains unresolved because the
  supplied report has no captured Server thread / Worker-Main / IoB / AI /
  chunk / navigation causal stack.
- No in-world owner-loss reproduction or quantitative performance
  improvement is claimed.

## [1.0.2] - 2026-08-22

### Added
- Added an optional ownerless-follow guard for IoB Variant Loader 2.7.0.
- Rejects ownerless follow-goal activation and stops its navigation before the
  invalid tick can reach Minecraft look control or navigation.

### Changed
- Preserved the existing `Worker-Main` world-generation deadlock guard and
  owner-present follow-goal behavior.
- Updated the validated build lane to Forge 1.18.2-40.3.12 and Isle of Berk
  Variant Loader 2.7.0.
- The Variant Loader dependency remains optional; Isle of Berk 1.2.x is still
  required.

## [1.1.0] - 2026-08-04

Historical GitHub **prerelease** on `main` for IoB Variant Loader **2.6.4**.
This is a separate line from 1.0.3 and is **not** the 1.0.3 artifact.

### Added
- Optional `AltFollowGoal` ownerless-state guard targeted at Variant Loader 2.6.4.

### Changed
- Split the optional Variant Loader mixin into its own soft-required configuration.

This prerelease does not supersede the 1.0.x Variant Loader 2.7.0 maintenance line.

## [1.0.1] - 2026-06-25

### Fixed
- Added `pack.mcmeta` (pack_format 8, MC 1.18.2) so Forge can register the jar
  as a valid resource pack. Eliminates the client-side warning screen:
  *"Mod File: …isleofberk-deadlockfix-1.0.0.jar failed to load a valid
  ResourcePackInfo"*. Cosmetic only — the mixin fix itself was always loading
  correctly — but the warning was scaring users on startup.

### Fixed (docs)
- README: corrected upstream Isle of Berk authorship to the **GACMD** team
  (Ghanou Craft, Apprentice Necromancer, TooTHleZZ & Arsian) — previously
  misattributed to "Aibreann".
- README: noted that upstream Isle of Berk is officially discontinued, so users
  understand the scope of this compatibility patch (final `isleofberk-1.2.0`
  on 1.18.2), and linked the canonical *Isle of Berk (Claws of Berk)* modpack
  by kesselot as the representative deployment target.
- README: replaced stale `vonix.network` link in the Credits section with the
  actual *Claws of Berk* CurseForge modpack page.

## [1.0.0] - 2026-06-24

### Added
- Initial release.
- `ADragonBaseMixin` short-circuits `ADragonBase.isWaterBelow()` to `false`
  when called from a `Worker-Main*` worldgen thread, eliminating the
  `ServerChunkCache.getChunk(...).join()` deadlock triggered by
  `SpeedStinger.checkSpawnRules` during chunk generation.
- `@Pseudo` mixin target so the mod loads cleanly regardless of refmap
  visibility into `isleofberk-1.2.0.jar`.
- Forge 1.18.2 (40.x+) compatibility, server-side fix (client copy is harmless).

[Unreleased]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.4...HEAD
[1.0.4]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.1...v1.0.2
[1.1.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/releases/tag/v1.0.0
