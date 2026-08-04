# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2026-08-04

> Candidate release for Forge 1.18.2. The Variant Loader protection is bytecode
> and userdev-boot validated against IoB Variant Loader 2.6.4, with the publisher's
> Forge 40.3.12 minimum retained as an explicit support boundary.

### Added

- Added an optional `AltFollowGoal` ownerless-state guard for IoB Variant Loader 2.6.4.
- Rejects ownerless goal activation through `m_8036_()Z`.
- Cancels the ownerless `m_8037_()V` tick after stopping dragon navigation.
- Preserved the existing `Worker-Main` `ADragonBase.isWaterBelow()` deadlock guard.

### Changed

- Split the optional Variant Loader mixin into its own soft-required configuration.
- Pinned ForgeGradle and MixinGradle versions for reproducible builds.
- Added artifact structure, version, and upstream-class exclusion checks to the release workflow.
- Added pinned third-party validation inputs with SHA-512 verification in CI.

### Compatibility boundary

- Minecraft 1.18.2, Forge 40.x, Java 17.
- Required: Isle of Berk 1.2.x; validated against 1.2.0.
- Optional: IoB Variant Loader 2.6.4 only.
- Variant Loader's publisher documents Forge 40.3.12+; the live Forge 40.3.0
  combination remains a residual support risk. Use
  `alternative_land_navigation=false` as the reversible workaround if needed.

### Scope boundary

This release does not address the separate AcquirePoi watchdog or VonixGuardian
hot-path issues.

## [1.0.1] - 2026-06-25

### Fixed

- Added `pack.mcmeta` (pack_format 8, MC 1.18.2) so Forge can register the jar
  as a valid resource pack.

## [1.0.0] - 2026-06-24

### Added

- Initial `ADragonBase.isWaterBelow()` `Worker-Main` world-generation deadlock guard.
- Forge 1.18.2 (40.x+) compatibility.

[Unreleased]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/releases/tag/v1.0.0
