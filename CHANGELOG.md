# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
- Mixin remapping stays enabled for the `AltFollowGoal` `m_8036_`/`m_8037_`
  selectors so production Goal members are resolved through the refmap.

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

[Unreleased]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.2...HEAD
[1.0.2]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/releases/tag/v1.0.0
