# Changelog

All notable changes to **hChat** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> **Repos:** [github.com/hauchdev/hChat](https://github.com/hauchdev/hChat)
> **Targets:** Paper **1.18.2 → 1.21.8**, Java **21**, Adventure API.

---

## [1.2.6] 2026-08-08

### Added
- **Command tab completion** — `/hchat` now completes its subcommands
  (`reload`, `help`, `lang`, `update`, filtered by permission) and
  `/hchat lang` completes the available language codes; `/message` also
  suggests offline players (offline delivery is supported), joining the
  existing player-name completion of `/ignore` and channel completion of
  `/channel`.

## [1.2.5] 2026-08-05

### Added
- **Dynamic chat placeholders** — new `DynamicPlaceholderResolver` service
  (registered in `ServiceRegistry`, used by `ChatListener`) renders
  NoNChat-style tokens inside every chat format, after PAPI and before the
  message is inserted:
  - `[ping]` — colored latency indicator with configurable symbol, ms
    value, thresholds and colors (`placeholders.ping.*`; green ≤ `good-max`,
    yellow ≤ `medium-max`, red above).
  - `[item]` — main-hand item with a custom tooltip on hover (name,
    enchantments, durability bar, lore), built from version-safe APIs that
    work on every supported Minecraft version.
  - `[coords]` / `[world]` — player position and world name
    (`placeholders.coords.format`, `placeholders.world.format`).
  - `[afk]` — `[AFK]` prefix when the player is away, detected through a
    configurable PAPI placeholder (`placeholders.afk.*`, default
    `%essentials_afk%`).
- **Config v3** — `config.yml` bumped to `config-version: 3` with a new
  `placeholders:` section; the existing migration merges the new keys into
  already-deployed configs automatically.

### Changed
- **ChatListener** resolves dynamic tokens at the `Component` level
  (`replaceText`) and inserts `{message}` as its own component, so tokens
  typed by players are never interpreted as placeholders while message
  color codes and PAPI placeholders keep working exactly as before.
- **Single version source** — the project now uses Maven CI-friendly
  versions: the `<revision>` property in the root `pom.xml` is the only
  place the plugin version is defined. Every child module's `<parent>`
  reference, the shaded jar name and the filtered `plugin.yml` pick it up
  automatically (or override per build with `-Drevision=x.y.z`). Releasing
  no longer means editing 14 POM files.

---

## [1.2.4] 2026-08-05

### Added
- **Chat channels** — configurable chat channels with per-channel format,
  distance range, speak/see permissions, cooldown and chat alias (`channels:`
  in `config.yml`). Includes distance-based **local chat** with action-bar
  hints for nearby players, named channels (`staff`, `vip`, `trade`), alias
  prefixes (`#staff hola`), a `/channel` command (alias `/ch`) with tab
  completion, clickable channel switching in chat, persistent active channel
  per player, and a configurable `/broadcast` cooldown shared with channel
  cooldowns (`broadcast.cooldown-ms`).
- **Update checker** — checks `hauchdev/hChat` GitHub Releases on startup
  (`update-checker.enabled`), notifies players with the `hchat.update`
  permission on join (`update-checker.notify-admins`), and exposes
  `/hchat update` to force a check. The update message is clickable and
  opens the release page.
- **Automatic config migration** — `config.yml` now carries a
  `config-version` marker. When a newer plugin version adds config keys,
  they are merged from the bundled default file on next startup/reload,
  preserving every existing user setting. No more deleting `config.yml`.

---

## [1.2.0] – 2026-07-20

### Added

#### 🏗️ Multi-Module Maven Architecture
- **Maven Reactor** with parent POM and 13 sub-modules: `core`, 11 version adapters (`v1_18_R2` → `v1_21_R3`), and `plugin-dist`.
- **`core` module** — version-independent business logic (commands, listeners, managers, config, utils, API interfaces). No CraftBukkit/NMS imports.
- **`plugin-dist` module** — single shaded jar entry point (`HChat.java` + `plugin.yml`). Contains the final distributable.
- **Version adapter modules** (`v1_18_R2` … `v1_21_R3`) — each implements the platform abstraction for its Minecraft revision.
- **`maven-shade-plugin`** bundles all adapter modules into one jar. The resulting `hChat-1.2.0.jar` supports every version from 1.18.2 to 1.21.8 out of the box.

#### 🧩 Platform Abstraction Layer
- **`PlatformAdapter` interface** — version-agnostic API for action bar, titles, boss bar, sounds, motd, online player count, and message sending.
- **`Platform` singleton** — stores the adapter detected at startup. Accessed via `Platform.get()` anywhere in core.
- **`VersionLoader`** — auto-detects the running Minecraft version by checking the Bukkit package name and mapping it to the correct adapter via `REVISION_TABLE`. Falls back to `MODERN_ALIAS` for untested future versions.
- **`HChatBossBar` interface** — version-independent boss bar abstraction (setTitle, setColor, setStyle, setProgress, showTo, hideFrom, viewers, dispose).
- **`SoundLookup` utility** — resolves `Sound` enum values across different Minecraft versions.
- **`TitleData` / `SoundData` records** — value objects for title and sound parameters.

#### 🚀 Bootstrap & Registry System
- **`Bootstrap` class** — orchestrates plugin initialization: config → storage → managers → services → commands → listeners → scheduled tasks → PlaceholderAPI hook.
- **`PluginContext`** — dependency container with all plugin services, managers, and registries.
- **`CommandRegistry`** — register and iterate commands with `CommandHolder` (label, executor, optional tab-completer).
- **`ListenerRegistry`** — register Bukkit event listeners by name.
- **`ServiceRegistry`** — central access to `BroadcastService`, `ConfigurationService`, `ChatLogger`, `AutoBroadcastManager`.
- **`ManagerRegistry`** — central access to all managers (`IgnoreManager`, `SpyManager`, `MessageHistory`, etc.).
- **`HChatProvider`** — static `install/uninstall` API so external code can obtain plugin services without coupling to `JavaPlugin`.

#### 🗺️ Version Coverage (1.18.2 → 1.21.8)

| Minecraft | Module    | Action Bar                    | Titles                               | Boss Bar                     |
|-----------|-----------|-------------------------------|--------------------------------------|------------------------------|
| 1.18.2    | v1_18_R2  | `spigot().sendMessage()`      | Bukkit `sendTitle` (legacy)          | BungeeCord serialization     |
| 1.19.1-2  | v1_19_R1  | Experimental Paper            | Hybrid (Bukkit + Adventure)          | BungeeCord                   |
| 1.19.3    | v1_19_R2  | Experimental Paper            | Hybrid                               | BungeeCord                   |
| 1.19.4    | v1_19_R3  | Experimental Paper            | Hybrid                               | BungeeCord                   |
| 1.20.1    | v1_20_R1  | Experimental Paper            | Adventure `showTitle`                | BungeeCord                   |
| 1.20.2    | v1_20_R2  | Experimental Paper            | Adventure `showTitle`                | BungeeCord                   |
| 1.20.3-4  | v1_20_R3  | Experimental Paper            | Adventure `showTitle`                | BungeeCord                   |
| 1.20.5-6  | v1_20_R4  | `sendActionBar(Component)`    | Adventure `showTitle`                | Paper native `Component`     |
| 1.21.1-4  | v1_21_R1  | Adventure native              | Adventure + `Title` API              | Paper native                 |
| 1.21.5    | v1_21_R2  | Adventure native              | Adventure + `Title` API + Times      | Paper native                 |
| 1.21.6-8  | v1_21_R3  | Adventure native              | Adventure + `Title` API + Times      | Paper native                 |

#### 🔧 Services & Utilities
- **`BroadcastService`** — replaces the old `BroadcastRenderer`. Handles broadcast formatting, permission checks, and console output.
- **`ConfigurationService`** — unified access to `PluginConfig` + `PluginMessages`.
- **Exception hierarchy** — `HChatException`, `PlatformLoadException`, `CommandInvocationException`.
- **`ChatLogger`** — improved log rotation; retains `purgeOldLogs()` cleanup on startup, reload, and shutdown.

#### ⚙️ Build & CI
- **GitHub Actions** — updated `build.yml`: triggers on every branch push/PR, runs `mvn clean package -DskipTests`, uploads shaded jar as artifact, creates GitHub Release on `v*` tags.

### Changed
- **Entry point refactored** — `HChat.java` (in `plugin-dist`) is now minimal: `onEnable()` → `Bootstrap.initialize(this)`; `onDisable()` → `Bootstrap.shutdown()`.
- **Package restructuring** — all source moved from `dev.hauch.hChat` (mixed case) to `dev.hauch.hchat` (lowercase). Commands → `command`, listeners → `listener`, managers → `manager`, placeholders → `placeholder`.
- **Java version** bumped from 17 to 21. Compiler target set to `--release 21`.
- **Paper API** updated to `1.21.8-R0.1-SNAPSHOT`.
- **Comments** reduced across the entire codebase to minimal briefs.

### Removed
- `BroadcastRenderer.java` — replaced by `BroadcastService`.
- Old single-module `src/main/java/dev/hauch/hChat/` tree — fully migrated to `core/` and `plugin-dist/`.

### Fixed
- **Pattern variable scope** in `IgnoreCommand` and `SpyCommand`: `player.sendMessage()` inside negated `!(sender instanceof Player player)` blocks changed to `sender.sendMessage()`.
- **`getCommand()` type error** in `Bootstrap` and `ReplyCommand`: cast `plugin` to `JavaPlugin` since `getCommand()` is not on the `Plugin` interface.
- **`nimport` corruption** in PlatformAdapter files from earlier bulk sed pass — restored to valid `import` statements.
- **`util.ChatLogger` import typo** in `PluginContext.java` — corrected to `utils.ChatLogger`.

---

## [1.1.2] – 2026-07-19

> Items in this section map to commits `fix: some error in pom.xml`,
> `fix: HChat.java` (×2) and `chore: implement new functions` on the
> `dev` branch. Cross-check against `git log v1.1.0..v1.1.1` if you need
> the exact diffs; this changelog only records entries that can be
> confirmed against the source.

### Fixed
- **Build:** corrected malformed dependency declaration in `pom.xml`
  that caused `mvn clean verify` to fail on JDK 17 + Maven 3.9
  (`fix: some error in pom.xml`).
- **LangManager:** `languageManager.reload()` now preserves the
  previously-selected language code after `/hchat reload`, instead of
  silently resetting to `en`. Confirmed by reading
  `LanguageManager#reload`, which now scans `loadedLanguages` for the
  current code before re-applying it.

### Changed
- **Build:** bumped Maven Shade Plugin to `3.5.3` and Compiler Plugin
  to `3.13.0`; reproducible jar ordering is now deterministic across
  builds (`chore: implement new functions`).
- **Docs:** CI status badge in `README.md` now matches the actual
  workflow name (`build.yml`).

---

## [1.1.0] – 2026-07-18

### Added
- **Broadcast command** — `/broadcast <message>` (alias `/bc`) with
  the `hchat.broadcast` permission. Body color codes require
  `hchat.broadcast.color`. Format is configurable under
  `broadcast.format` in `config.yml`.
- **Auto-broadcast scheduler** — `AutoBroadcastManager` reads every
  `broadcast.<id>` section in `config.yml` (`enabled`, `message`,
  `interval` in seconds, `display-in-console`) and replays them on a
  `BukkitScheduler` timer.
- **Persistent ignore list** — `YamlIgnoreStorage` serializes the
  `IgnoreManager` map to `plugins/hChat/storage/ignored.yml`. Reloads
  and full restarts now keep ignored players intact.
- **Offline messages** — `OfflineMessageStore` queues `/msg` payloads
  for offline recipients up to `offline-messages.max-pending` per
  player. `PlayerJoinListener` drains them on login and optionally
  plays `offline-messages.sound`.
- **Chat logger** — `ChatLogger` writes ISO-8601 daily logs to
  `plugins/hChat/logs/YYYY-MM-DD.log` with `retention-days` cleanup at
  startup. Players with `hchat.bypass-log` are excluded.
- **Player language** — `PlayerLangManager` + `PlayerLangStorage` allow
  each player to pick their own locale via `/hchat lang <lang>`,
  stored in `storage/player-lang.yml`.
- **PlaceholderAPI expansion** — `HChatPlaceholderExpansion` exposes
  the following placeholders:
  - `%hchat_spy_enabled%`
  - `%hchat_ignored%`
  - `%hchat_ignored_<name>%` (case-insensitive per-player check)
  - `%hchat_ignored_count%`
  - `%hchat_dnd_enabled%`
  - `%hchat_last_sender%`
  - `%hchat_last_message_part%`
  - `%hchat_last_seen%`
- **Word filter** — `WordFilter` matches every chat-and-private-message
  payload against a configurable black list (`word-filter.words`) with
  three actions: `block`, `mask`, `warn`. Staff with
  `hchat.monitor.filter` receive a warning copy.
- **Mention sound + color** — `mentions.sound.{enabled,sound,volume,pitch}`
  and `mentions.colors.{enabled,color}` in `config.yml` introduce the
  sound + hex highlight shown when a player writes `@name` in chat.
- **Hover & clickable actions** — `hover-text.format` now renders a
  multi-line tooltip on every player name in chat and on private
  messages, supporting a `/msg {sender}` `ClickEvent.suggestCommand`
  shortcut.
- **DND infrastructure** — `DndManager` tracks per-player "do not
  disturb" state; surfaced through the `hchat_dnd_enabled%` placeholder.
  A dedicated `/dnd` command is planned for a future release.
- **DiscordSRV detection** — `HChat#onEnable` records a
  `discordSRVEnabled` boolean so future phases can branch on whether
  the hook is present. No traffic is sent to Discord today.
  - **Tab completion** — `/ignore` and `/spy` now implement
  `TabCompleter` (online player list for `/ignore`; empty list for
  the `/spy` toggle).
- **New permission nodes** — `hchat.lang`, `hchat.monitor.filter`,
  `hchat.bypass-log`, `hchat.broadcast.color` and the `hchat.*`
  umbrella parent that grants them all.
- **New `lang_en.yml` keys** — `me-command`, `ignored-by-target`,
  `offline-message-saved`, `offline-message-delivered`, and
  placeholder group `placeholders.{spy,ignored,dnd,last_seen}`.
- **CI pipeline** — `.github/workflows/build.yml` runs `mvn clean verify`
  on every push to `dev` and every pull request; tagged `v*` builds
  additionally publish a GitHub Release.

### Changed
- **Plugin entry point** — `HChat#onEnable` organized in six labelled
  blocks: CONFIG · STORAGE · MANAGERS · COMMANDS · LISTENERS · HOOKS.
- **MessageFormatter** — `LegacyComponentSerializer` now uses
  `character('&')` plus `.hexColors()`. All existing `&`-codes and
  `&#RRGGBB` hex strings render as Adventure components.
- **Direct-message format** — default strings replaced with friendlier
  placeholders (`[Steve -> You] message` instead of `[You <-> Steve]`).
- **Broadcast defaults** — `broadcast.format` ships a 3-line gradient
  header; `broadcast.example1` enabled out of the box at 10-minute cadence.
- **Lang fallback** — `LanguageManager#setLanguage` falls back to
  `lang_en.yml` when the requested code is not on disk instead of crashing.

### Deprecated
- Legacy in-memory `Map<UUID, Set<UUID>>` backing of `IgnoreManager`
  — will be removed once `YamlIgnoreStorage` proves stable.

### Removed
- Internal `IgnoreManager#clearInMemory()` (no-op) — replaced by
  `IgnoreManager#reload()` which delegates to `YamlIgnoreStorage`.

### Security
- `WordFilter#mask` replaces every character of a black-listed word
  with `*` before the message reaches `MessageFormatter`.

---

## [1.0.0] – 2026-07-18

### Added
- Initial public release of **hChat**.
- Direct messages: `/message`, `/msg`, `/tell`, `/w`, `/m`,
  `/whisper`, with `hchat.message` permission.
- Reply: `/reply`, `/r`, with `hchat.reply` permission.
- Spy mode: `/spy`, with `hchat.spy` permission, using
  `spy-format` from `config.yml`.
- Player ignore: `/ignore <player>`, in-memory only for now,
  permission `hchat.ignore`.
- Chat clear: `/clear`, permission `hchat.clear`.
- @mention detection (`@<playername>`) with configurable highlight
  color and personal notification message.
- Multi-language system based on `lang_<code>.yml` files under
  `plugins/hChat/lang/`, switchable with `config.yml > lang`.
- Plugin metadata (`plugin.yml`) with Paper `api-version: 1.20`,
  `depend: PlaceholderAPI + LuckPerms`, `softdepend: Vault, DiscordSRV`.
- Adventure-native rendering — every player-facing message goes through
  `MessageFormatter`, never raw `String` to chat.
- Hex color support: `&#RRGGBB` and standard `&`-codes share the same
  code path.
- LuckPerms integration (declared as a hard dependency on `plugin.yml`).
- Vault soft-dependency (declared, not yet wired beyond detection).
- DiscordSRV soft-dependency (declared, not yet wired beyond detection).
- `MessageHistory`, `IgnoreManager`, `SpyManager`, `DndManager`,
  `LanguageManager` core managers.
- Compact `MessageFormatter` utility supporting `&`-codes, hex, hover,
  click, join, and plain-text serialization.
- Hover-tooltip on every chat-line sender name (configurable via
  `hover-text.format` in `config.yml`).

### Security
- All chat listeners operate on `AsyncChatEvent` with standard
  `HIGHEST` priority, so server-side message ordering is preserved
  across audit hooks.

---

[Unreleased]: https://github.com/hauchdev/hChat/compare/v1.2.0...HEAD
[1.2.0]: https://github.com/hauchdev/hChat/compare/v1.1.2...v1.2.0
[1.1.2]: https://github.com/hauchdev/hChat/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/hauchdev/hChat/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/hauchdev/hChat/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/hauchdev/hChat/releases/tag/v1.0.0
