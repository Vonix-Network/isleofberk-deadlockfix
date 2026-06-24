# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
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

[Unreleased]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/releases/tag/v1.0.0
