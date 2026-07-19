# Changelog

All notable changes to **hChat** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> **Repos:** [github.com/hauchdev/hChat](https://github.com/hauchdev/hChat)
> **Targets:** Paper **1.20+**, Java **17**, Adventure API.

---

## [Unreleased]

> **This section is forward-looking.** The first two bullets reflect the
> design proposal documented in [`tutorial.md`](tutorial.md) and are **not
> yet implemented** in the shipped jar; the third bullet mirrors Phase 5
> of [`ROADMAP.md`](ROADMAP.md).

### Design proposal (see `tutorial.md`)
- Permission-based chat format (`{prefix}{player}{suffix}` style) —
  tutorial § 1.
- Custom welcome / farewell / first-join messages — tutorial § 2.

### Planned
- DiscordSRV ↔ `/msg` and `/broadcast` bridge (Phase 5 of `ROADMAP.md`).

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

### Deprecated
- Nothing.

### Removed
- Nothing.

### Security
- Nothing.

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

> **Known issue carried over from v1.0.0:** the `hchat.sc` permission
> node is declared as a child of `hchat.*` in `plugin.yml` but there is
> no `sc` command yet — it is reserved for a future release.
- **Tab completion** — `/ignore` and `/spy` now implement
  `TabCompleter` (online player list for `/ignore`; empty list for
  the `/spy` toggle).
- **New permission nodes** — `hchat.lang`, `hchat.monitor.filter`,
  `hchat.bypass-log`, `hchat.broadcast.color` and the `hchat.*`
  umbrella parent that grants them all. Tab completion on `/ignore`
  and `/spy` is wired directly in code via
  `getCommand(...).setTabCompleter(...)` and does **not** require a new
  permission node.
- **New `lang_en.yml` keys** — `me-command`, `ignored-by-target`,
  `offline-message-saved`, `offline-message-delivered`, placeholder
  group `placeholders.{spy,ignored,dnd,last_seen}`, etc.
- **CI pipeline** — `.github/workflows/build.yml` runs `mvn clean verify`
  on every push to `dev` and every pull request; tagged `v*` builds
  additionally publish a GitHub Release.

### Changed
- **Plugin entry point** — `HChat#onEnable` is now organized in six
  labelled blocks: CONFIG · STORAGE · MANAGERS · COMMANDS · LISTENERS ·
  HOOKS. Same logical behavior, clearer bootstrap order.
- **MessageFormatter** — `LegacyComponentSerializer` now uses
  `character('&')` plus `.hexColors()`. All existing `&`-codes and
  `&#RRGGBB` hex strings render as Adventure components.
- **Direct-message format** — `direct-messages.{sender,receiver}.format`
  default strings replaced with friendlier placeholders so the
  receiver copy no longer reads `[You <-> Steve]` but
  `[Steve -> You] message`.
- **Broadcast defaults** — `broadcast.format` now ships a 3-line
  gradient header and `broadcast.example1` is enabled out of the box
  with a 10-minute cadence to demonstrate the scheduler.
- **Lang fallback** — `LanguageManager#setLanguage` falls back to
  `lang_en.yml` when the requested code is not present on disk and
  logs a warning instead of crashing.

### Deprecated
- The legacy in-memory `Map<UUID, Set<UUID>>` backing of
  `IgnoreManager` is now wrapped: it will be removed once
  `YamlIgnoreStorage` proves stable for two consecutive releases.

### Removed
- Internal `IgnoreManager#clearInMemory()` (no-op): replaced by
  `IgnoreManager#reload()` which now delegates to `YamlIgnoreStorage`.

### Security
- `WordFilter#mask` replaces every character of a black-listed word
  with `*` before the message reaches `MessageFormatter`, so the
  redacted text is what clients actually receive. (External plugins
  that have already cached the pre-filter string remain responsible for
  re-sanitising their own copy.)

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
- LuckPerms integration (declared as a hard dependency on
  `plugin.yml`).
- Vault soft-dependency (declared, not yet wired beyond detection).
- DiscordSRV soft-dependency (declared, not yet wired beyond detection).
- `MessageHistory`, `IgnoreManager`, `SpyManager`, `DndManager`,
  `LanguageManager` core managers exposed through `HChat#getX()`.
- Compact `MessageFormatter` utility supporting `&`-codes, hex,
  hover, click, join, and plain-text serialization.
- Hover-tooltip on every chat-line sender name (configurable via
  `hover-text.format` in `config.yml`).

### Changed
- N/A (initial release).

### Deprecated
- N/A (initial release).

### Removed
- N/A (initial release).

### Security
- All chat listeners operate on `AsyncChatEvent` with the standard
  `HIGHEST` priority, so server-side message ordering is preserved
  across audit hooks.

---

[Unreleased]: https://github.com/hauchdev/hChat/compare/v1.1.1...HEAD
[1.1.2]: https://github.com/hauchdev/hChat/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/hauchdev/hChat/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/hauchdev/hChat/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/hauchdev/hChat/releases/tag/v1.0.0
