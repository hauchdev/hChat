# hChat

**hChat** is a modern, all-in-one chat plugin for Paper 1.18.2 – 1.21.8.
One jar, zero required configuration — drop it in and your chat is
instantly better.

## ✨ Features

- **Chat channels** — `global`, `local`, `staff`… with alias prefixes
  (`#staff hey`), distance ranges, per-world channels, speak/see
  permissions, per-channel cooldowns and action-bar hints.
- **MiniMessage support** — a global `chat.format-mode`
  (`legacy` / `minimessage` / `auto`) plus a per-channel override, so a
  legacy `global` and a MiniMessage `staff` channel can coexist. Player
  text is always escaped: players keep their `&` colors but can never
  inject tags, hover or click events.
- **Direct messages** — `/msg`, `/reply`, offline mail with a pending
  inbox, per-player ignore and DND.
- **Mentions** — `@player`, `@everyone` and `@here` with colors, sounds,
  permission gates and cooldowns.
- **Moderation** — `/slowmode`, `/chatlock`, word filter, anti-caps,
  anti-unicode, anti-advertisement and anti-spam (flood + similarity).
- **Extras** — AFK detection, staff chat `/sc`, chat replay
  (`/hchat replay`), auto-broadcasts, welcome / quit / first-join
  messages, death messages and dynamic tokens
  (`[ping]`, `[item]`, `[coords]`, `[world]`, `[afk]`).
- **PlaceholderAPI built in** — `%luckperms_prefix%`, `%vault_prefix%`
  and any other PAPI placeholder work in every format.

## 📦 Requirements

| | |
|---|---|
| Server | Paper (or forks like Purpur) **1.18.2 → 1.21.8** |
| Java | 21+ |
| Required | [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) |
| Recommended | [LuckPerms](https://modrinth.com/plugin/luckperms) — no longer required; every feature degrades gracefully without it |

## 🚀 Installation

1. Download the latest `hChat.jar`.
2. Drop it into your server's `plugins/` folder.
3. Make sure PlaceholderAPI is installed.
4. Restart — done.

Full configuration guide, commands and permissions:
[github.com/hauchdev/hChat](https://github.com/hauchdev/hChat#readme)

## 🎨 Chat formats

How formats are parsed is controlled by `chat.format-mode`:

| Mode | Behavior |
|---|---|
| `legacy` | Classic `&` codes + `&#RRGGBB` hex (default, backwards compatible) |
| `minimessage` | [MiniMessage](https://docs.advntr.dev/minimessage) tags (`<red>`, `<gradient:red:blue>`, `<#RRGGBB>`, hover/click); classic `&` codes and `&#RRGGBB` hex are still translated |
| `auto` | Legacy when the format contains `&` codes, MiniMessage otherwise |

Every channel can override the global mode with its own
`channels.<id>.format-mode` key.

## 🔌 Discord bridge

When DiscordSRV is installed, hChat detects it at startup and confirms
the hook. Full two-way channel bridging is planned for a future release —
see the [roadmap](https://github.com/hauchdev/hChat/blob/main/ROADMAP.md).

## 📊 Metrics & updates

- **Metrics:** anonymous usage statistics via
  [faststats.dev](https://faststats.dev), always on, nothing to
  configure. No player or chat data is ever collected.
- **Update checker:** on startup hChat checks Modrinth for newer stable
  releases and notifies admins on join. Disable it under
  `update-checker.enabled` in `config.yml`.

## 🔗 Links

- [Source code](https://github.com/hauchdev/hChat)
- [Issue tracker](https://github.com/hauchdev/hChat/issues)
- [Full changelog](https://github.com/hauchdev/hChat/blob/main/CHANGELOG.md)

## 📄 License

hChat is released under the
[MIT License](https://github.com/hauchdev/hChat/blob/main/LICENSE).
