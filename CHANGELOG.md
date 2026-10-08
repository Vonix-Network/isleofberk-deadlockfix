# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Evidence against live Claws of Berk (2026-08-16)

Live Isle of Berk remains `isleofberk-1.2.0.jar`. The original `ADragonBase.isWaterBelow()`
cross-chunk `Level.getBlockState` read is still present, and `SpeedStinger.m_5610_` still
calls it. The published `1.0.1` core guard therefore remains required.

Live Variant Loader is `iobvariantloader-2.7.0-all.jar`, not the previously validated
`2.6.4`. Class `nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal` still exists
with `m_8036_()Z` / `m_8037_()V` and still uses `tamable` / `navigation`. The tick path
still calls `ADragonBase.m_142480_()` (inherited `TamableAnimal.getOwner()`) into look
control and navigation with no local null check. The 1.1.x ownerless adapter is therefore
still applicable in principle, but it has not been bytecode-validated or packaged-runtime
proven against 2.7.0. Do not ship the 2.6.4-only adapter as a 2.7.0 fix.

Related live jars (`leafsvariants-1.0.0`, `hybridsplus-2.1.4`, `IoBAddons-1.1d`) do not
contain replacement `ADragonBase` / `AltFollowGoal` classes.

Local `1.1.2` source/docs remain untagged and unpublished. GitHub latest stable is still
`v1.0.1`; `v1.1.0` exists only as a prerelease.

## [1.1.2] - 2026-08-12

### Changed

- Regular release artifacts now exclude the optional Variant Loader adapter and dependency.
- Release validation asserts the regular artifact is Isle of Berk-only.
- Corrected release documentation to describe the implemented `canUse()`/`tick()` adapter behavior.
- This version is local/unpushed. It is not a live-fleet artifact.

## [1.1.1] - 2026-08-04

`1.1.1` is a corrective prerelease candidate; independent verification and packaged-runtime gates remain required.

### Fixed

- Added ownerless activation protection through `canUse()Z` and ownerless tick cancellation through `tick()V`.
- Keeps the target goal's captured navigation field as the ownerless-stop target, matching the validated 2.6.4 bytecode.
- Release CI now verifies fixed SHA-512 values for all compile/runtime inputs, including manual runs.
- Restricts the optional metadata dependency to the exact validated Variant Loader 2.6.4 release.

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
- `AltFollowGoalMixin` is an optional Variant Loader 2.6.4-only protection; the release workflow enforces pinned upstream SHA-512 inputs.

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

[Unreleased]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.1.2...HEAD
[1.1.2]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.1.0...v1.1.1
[1.0.1]: https://github.com/Vonix-Network/isleofberk-deadlockfix/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-deadlockfix/releases/tag/v1.0.0
