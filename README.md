<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,4,6&height=200&section=header&text=hChat&fontSize=70&fontAlignY=35&desc=Minecraft%20Chat%20Redefined&descAlignY=55" width="100%"/>
</p>

<p align="center">
  <b>hChat</b> — A modern and complete chat system for Paper 1.20+ servers.
  <br>
  Chat channels, direct messages, mentions, moderation (slowmode, chatlock, filters), AFK, staff chat, mail, replay, and more.
</p>

<p align="center">
  <a href="https://github.com/hauchdev/hChat/releases">
    <img src="https://img.shields.io/github/v/release/hauchdev/hChat?style=for-the-badge&logo=github&label=Version&color=6C5CE7" alt="Version">
  </a>
  <a href="https://github.com/hauchdev/hChat/stargazers">
    <img src="https://img.shields.io/github/stars/hauchdev/hChat?style=for-the-badge&logo=github&label=Stars&color=FFD43B" alt="Stars">
  </a>
  <a href="https://github.com/hauchdev/hChat/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/hauchdev/hChat?style=for-the-badge&label=License&color=00CEC9" alt="License">
  </a>
  <a href="https://github.com/hauchdev/hChat/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/hauchdev/hChat/build.yml?style=for-the-badge&logo=githubactions&label=Build&color=00D2D3" alt="Build">
  </a>
  <a href="https://papermc.io">
    <img src="https://img.shields.io/badge/Paper-1.20+-F26C63?style=for-the-badge&logo=databricks&label=API" alt="Paper">
  </a>
  <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">
    <img src="https://img.shields.io/badge/PlaceholderAPI-2.11+-00ADD8?style=for-the-badge&logo=curseforge&label=Required" alt="PlaceholderAPI">
  </a>
</p>

<p align="center">
  <a href="#✨-features">Features</a> •
  <a href="#⚡-quick-start">Quick Start</a> •
  <a href="#🎮-commands">Commands</a> •
  <a href="#🔒-permissions">Permissions</a> •
  <a href="#⚙️-configuration">Configuration</a> •
  <a href="#🌐-localization">Localization</a> •
  <a href="#🔌-placeholders">Placeholders</a> •
  <a href="#🧩-dependencies">Dependencies</a> •
  <a href="#🛠️-building-from-source">Building</a> •
  <a href="#🤝-contributing">Contributing</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Powered%20by-Adventure-6C5CE7?style=flat-square&logo=databricks&logoColor=white">
  <img src="https://img.shields.io/badge/Maintained%3F-Yes-00C853?style=flat-square&logo=github&logoColor=white">
</p>

---

<details>
  <summary><b>📋 Table of Contents</b></summary>
  <br>

- [✨ Features](#✨-features)
- [⚡ Quick Start](#⚡-quick-start)
- [🎮 Commands](#🎮-commands)
- [🔒 Permissions](#🔒-permissions)
- [⚙️ Configuration](#⚙️-configuration)
- [📡 Chat Channels](#📡-chat-channels)
- [🌐 Localization](#🌐-localization)
- [🔌 Placeholders](#🔌-placeholders)
- [🛡️ Filters](#🛡️-filters)
- [📦 Broadcasts](#📦-broadcasts)
- [📨 Mail (Offline Messages)](#📨-mail-offline-messages)
- [😴 AFK](#😴-afk)
- [🔌 Developer API](#🔌-developer-api)
- [🌐 Proxy note](#🌐-proxy-note-velocity--bungeecord)
- [🧩 Dependencies](#🧩-dependencies)
- [🛠️ Building from Source](#🛠️-building-from-source)
- [🤝 Contributing](#🤝-contributing)
- [📄 License](#📄-license)

</details>

---

## ✨ Features

<table>
  <tr>
    <td align="center" width="33%">
      <b>💬 Direct Messages</b><br>
      <sub>Customizable format with hover tooltips and click-to-reply</sub>
    </td>
    <td align="center" width="33%">
      <b>🔔 @Mentions</b><br>
      <sub>Configurable sound, highlight color, and personal notification</sub>
    </td>
    <td align="center" width="33%">
      <b>👁️ Spy Mode</b><br>
      <sub>Monitor private messages with a dedicated permission</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>🚫 Ignore System</b><br>
      <sub>Persistent per-player block list that survives reloads</sub>
    </td>
    <td align="center" width="33%">
      <b>🌍 Multi-language</b><br>
      <sub>YAML localization, including per-player language selection</sub>
    </td>
    <td align="center" width="33%">
      <b>🔗 PlaceholderAPI</b><br>
      <sub><code>%hchat_spy_enabled%</code>, <code>%hchat_ignored%</code>, DND, last seen…</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>🎨 Hex Colors</b><br>
      <sub><code>&amp;#RRGGBB</code> hex + classic <code>&amp;</code> legacy codes</sub>
    </td>
    <td align="center" width="33%">
      <b>🖱️ Hover & Click</b><br>
      <sub>Tooltips and clickable actions on every chat line</sub>
    </td>
    <td align="center" width="33%">
      <b>📦 Broadcasts</b><br>
      <sub>Manual <code>/bc</code> plus auto-broadcast scheduler</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>🛡️ Moderation</b><br>
      <sub>Slowmode, chat lock and staff chat (<code>/sc</code>)</sub>
    </td>
    <td align="center" width="33%">
      <b>🔤 Filter Chain</b><br>
      <sub>Word filter + anti-caps, anti-unicode, anti-ad, anti-spam</sub>
    </td>
    <td align="center" width="33%">
      <b>📜 Chat Logger</b><br>
      <sub>Rotating daily logs with configurable retention</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>📡 Chat Channels</b><br>
      <sub>Per-world, per-permission, aliases, cooldowns</sub>
    </td>
    <td align="center" width="33%">
      <b>📬 Mail</b><br>
      <sub>Offline messages with <code>/hchat mail read|clear|send</code></sub>
    </td>
    <td align="center" width="33%">
      <b>😴 AFK</b><br>
      <sub>Automatic inactivity detection + <code>/afk [message]</code></sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>📢 Mass Mentions</b><br>
      <sub><code>@everyone</code> / <code>@here</code> with cooldown + sound</sub>
    </td>
    <td align="center" width="33%">
      <b>⏱️ Ping & Seen</b><br>
      <sub><code>/ping [player]</code> and <code>/seen &lt;player&gt;</code></sub>
    </td>
    <td align="center" width="33%">
      <b>🔄 Chat Replay</b><br>
      <sub><code>/hchat replay [player]</code> on a ring buffer</sub>
    </td>
  </tr>
</table>

---

## ⚡ Quick Start

### 📥 Installation

```bash
# 1. Download the latest JAR from Releases
wget https://github.com/hauchdev/hChat/releases/latest/download/hChat.jar

# 2. Drop it into your server's plugins folder
mv hChat.jar /your-server/plugins/

# 3. Make sure PlaceholderAPI and LuckPerms are installed (required)

# 4. Restart or reload your server
```

hChat generates `plugins/hChat/config.yml` and `plugins/hChat/lang/lang_en.yml`
on first start. Edit them, then run `/hchat reload`.

---

## 🎮 Commands

| Command                              | Aliases                        | Description                                                | Permission           |
|--------------------------------------|--------------------------------|------------------------------------------------------------|----------------------|
| `/message <player> <message>`        | `/msg`, `/tell`, `/w`, `/m`, `/whisper` | Send a private message                          | `hchat.message`      |
| `/reply <message>`                   | `/r`                           | Reply to the most recent private message                   | `hchat.reply`        |
| `/broadcast <message>`               | `/bc`                          | Broadcast a message to the whole server                    | `hchat.broadcast`    |
| `/ignore <player>`                   | —                              | Toggle ignoring a specific player                          | `hchat.ignore`       |
| `/ignore list`                       | —                              | List ignored players (click to unignore)                   | `hchat.ignore`       |
| `/spy`                               | —                              | Toggle spy mode (see all private messages)                | `hchat.spy`          |
| `/clear`                             | —                              | Clear the chat of every player                             | `hchat.clear`        |
| `/channel [name]`                    | `/ch`                          | List or switch your active chat channel                    | `hchat.channel`      |
| `/slowmode <seconds\|off\|status>`     | —                              | Global chat slowmode (in-memory, bypassable)               | `hchat.slowmode`     |
| `/chatlock <on\|off\|status>`         | —                              | Lock / unlock the whole chat                               | `hchat.chatlock`     |
| `/sc <message>`                      | —                              | Staff-only chat channel                                    | `hchat.sc`           |
| `/dnd [on\|off]`                     | —                              | Toggle do-not-disturb (blocks incoming DMs)                | `hchat.dnd`          |
| `/ping [player]`                     | —                              | Show your (or someone else's) latency                      | `hchat.ping`         |
| `/seen <player>`                     | —                              | When the player last sent you a message                    | `hchat.seen`         |
| `/afk [message]`                     | —                              | Toggle AFK (auto-detected after inactivity too)            | `hchat.afk`          |
| `/hchat reload`                      | —                              | Reload `config.yml` and language files                     | `hchat.reload`       |
| `/hchat help`                        | —                              | Show the in-game help menu                                 | `hchat.help`         |
| `/hchat lang <lang>`                 | —                              | Switch your own player language                            | `hchat.lang`         |
| `/hchat about`                       | —                              | Version, authors and feature overview                      | `hchat.help`         |
| `/hchat mail <read\|clear\|send>`    | —                              | Read / clear / send offline messages                       | `hchat.help`         |
| `/hchat replay [player]`             | —                              | Reprint recent chat (optionally filtered by player)        | `hchat.help`         |
| `/hchat replay clear-buffer`         | —                              | Empty the replay buffer                                    | `hchat.clear`        |

> ⌨️ **Tab completion** — every command supports it where it makes sense:
> `/hchat` completes its subcommands (`reload`, `help`, `lang`, `update`,
> filtered by permission) and `/hchat lang` completes the language codes
> from `lang/`; `/message` and `/ignore` complete online player names
> (`/message` also suggests offline players, since it can deliver offline
> messages); `/channel` completes the configured channel ids.

---

## 🔒 Permissions

| Permission                  | Default | Description                                                    |
|-----------------------------|---------|----------------------------------------------------------------|
| `hchat.*`                   | `op`    | Access to every hChat command and feature                      |
| `hchat.message`             | `true`  | Allows `/msg`, `/tell`, `/w`, `/m`, `/whisper`                 |
| `hchat.reply`               | `true`  | Allows `/reply`, `/r`                                          |
| `hchat.broadcast`           | `op`    | Allows `/broadcast`, `/bc`                                     |
| `hchat.broadcast.color`     | `op`    | Allows color codes inside broadcast messages                   |
| `hchat.ignore`              | `true`  | Allows `/ignore <player>`                                      |
| `hchat.spy`                 | `op`    | Allows toggling `/spy`                                         |
| `hchat.spy.all`             | `op`    | Receive every private message without exception               |
| `hchat.clear`               | `op`    | Allows `/clear`                                                |
| `hchat.reload`              | `op`    | Allows `/hchat reload`                                         |
| `hchat.help`                | `true`  | Allows `/hchat help`                                           |
| `hchat.lang`                | `true`  | Allows `/hchat lang <lang>`                                    |
| `hchat.channel`             | `true`  | Allows `/channel` and channel switching                        |
| `hchat.slowmode`            | `op`    | Allows `/slowmode`                                             |
| `hchat.slowmode.bypass`     | `op`    | Bypass the global chat slowmode                                |
| `hchat.chatlock`            | `op`    | Allows `/chatlock`                                             |
| `hchat.chatlock.bypass`     | `op`    | Keep talking while the chat is locked                          |
| `hchat.sc`                  | `op`    | Allows `/sc` staff chat                                        |
| `hchat.dnd`                 | `true`  | Allows `/dnd`                                                  |
| `hchat.bypass.dnd`          | `op`    | Send DMs to players with DND enabled                           |
| `hchat.ping`                | `true`  | Allows `/ping`                                                 |
| `hchat.ping.others`         | `op`    | Allows `/ping <player>`                                        |
| `hchat.seen`                | `true`  | Allows `/seen`                                                 |
| `hchat.afk`                 | `true`  | Allows `/afk`                                                  |
| `hchat.mention.everyone`    | `op`    | Allows using `@everyone`                                       |
| `hchat.mention.here`        | `op`    | Allows using `@here`                                           |
| `hchat.monitor.filter`      | `op`    | Receive warnings when the word filter trips on a player        |
| `hchat.bypass-log`          | `op`    | Send private messages without writing them to the chat logger |

---

## ⚙️ Configuration

`config.yml` is generated automatically on first start. The same file
also controls every audio / visual cue the plugin emits.

<details>
  <summary><b>📄 View full config.yml</b></summary>

```yaml
# config-version is bumped automatically when new options are added;
# your settings are always kept (old file backed up as
# config-backup-v{n}.yml).
config-version: 7

lang: en
debug: false

update-checker:
  enabled: true
  notify-admins: true

chat:
  default-format: "&7%luckperms_prefix% {player} %luckperms_suffix%&8: &f{message}"
  hex-colors: true
  formats:
    vip:
      permission: hchat.chat.format.vip
      format: "&6&lVIP &8» &e{prefix}{player}{suffix} &8: &f{message}"
    mvp:
      permission: hchat.chat.format.mvp
      format: "&b&lMVP &8» &b{prefix}{player}{suffix} &8: &7{message}"
    admin:
      permission: hchat.chat.format.admin
      format: "&c&lADMIN &8» &c{prefix}{player}{suffix} &8: &7{message}"

# Dynamic chat tokens: [ping] [item] [coords] [world] [afk]
placeholders:
  ping:
    enabled: true
    symbol: "▪"
    show-value: true
    value-suffix: "ms"
    good-max: 80
    medium-max: 150
    good-color: "&a"
    medium-color: "&e"
    bad-color: "&c"
    value-color: "&7"
  item:
    enabled: true
  coords:
    enabled: true
    format: "&7({x}, {y}, {z})"
  world:
    enabled: true
    format: "&7{world}"
  afk:
    enabled: true
    placeholder: "%essentials_afk%"
    afk-values: ["yes", "true"]
    format: "&c[AFK]&r "

welcome:
  enabled: true
  message: "&a&lWelcome &e{player} &a&lto the server!"
  hide-vanilla: true
  motd-on-join: false
  sound:
    enabled: true
    sound: "entity.player.levelup"
    volume: 0.6
    pitch: 1.0
  first-join:
    enabled: true
    message: "&6&l★ &e{player} &6just joined for the first time!"
    hide-vanilla: true

quit:
  enabled: true
  message: "&c{player} &7left the server."
  hide-vanilla: true

mentions:
  sound:
    enabled: true
    sound: "entity.experience_orb.pickup"
    volume: 1.0
    pitch: 1.0
  colors:
    enabled: true
    color: "&#54A3FF"
  # Bold highlight (a real background color is not possible in chat).
  highlight: true
  everyone-permission: hchat.mention.everyone
  here-permission: hchat.mention.here
  everyone-cooldown-seconds: 60
  everyone-sound:
    enabled: true
    sound: "entity.wither.spawn"
    volume: 1.0
    pitch: 1.0

direct-messages:
  sender:
    format: "&7[&eYou &7-> &e{receiver}&7] &7{message}"
    hover-text:
      enabled: false
  receiver:
    format: "&7[&e{sender} &7-> &eYou&7] &7{message}"
    hover-text:
      enabled: true
  clickable-actions:
    enabled: true
    reply-command: "/msg {sender}"
  silenced-action-bar:
    enabled: true
    text: "&e{receiver} is muted - the message was not delivered."

spy-format: "&e{sender} &7-> &e{target}&7: &7{message}"

hover-text:
  enabled: true
  format:
    - "&#54FF7F %player_name%"
    - "&7"
    - "&eClick to send a private message"

channels:
  default-by-permission:
    - permission: hchat.channel.default.vip
      channel: vip
  hint-on-switch: true
  hint-on-join: true
  global:
    range: -1
  local:
    format: "&8[Local] &7{player} &8» &f{message}"
    range: 80
    action-bar-hint: "&eThere are people talking near you"
    alias: "#l"
  staff:
    format: "&c[Staff] {player} &8» &f{message}"
    speak-permission: hchat.channel.staff
    see-permission: hchat.channel.staff.see
    alias: "#staff"
  vip:
    format: "&e[VIP] {player} &8» &f{message}"
    speak-permission: hchat.chat.format.vip
    alias: "#vip"
  trade:
    format: "&6[Trade] {player} &8» &f{message}"
    cooldown-ms: 5000
    alias: "#trade"
  world:
    format: "&7[{world}] {player} &8» &f{message}"
    range: -1
    per-world: true
    alias: "#world"

broadcast:
  enabled: true
  cooldown-ms: 0
  format:
    - ""
    - "&#2791F5Broadcast:"
    - "{message}"
  example1:
    enabled: true
    message: "&#F52727This broadcast sent every 10 minutes. Edit in /hChat/config.yml"
    interval: 600
    display-in-console: true

offline-messages:
  max-pending: 10
  sound:
    enabled: true
    sound: "entity.experience_orb.pickup"
    volume: 1.0
    pitch: 1.0

logging:
  private-messages:
    enabled: true
    path: logs
    retention-days: 30

word-filter:
  enabled: true
  words: ["blacklist1", "blacklist2"]
  action: "block"   # block | mask | warn

# Advanced filters (config v6): anti-caps, anti-unicode, anti-ad,
# anti-spam - see the "Filters" section below.
filters:
  anti-caps:
    enabled: true
    max-uppercase-percent: 70
    min-length: 8
    action: "mask"
  anti-unicode:
    enabled: true
    block-invisible: true
    action: "block"
  anti-ad:
    enabled: true
    patterns:
      - "(?<![a-zA-Z0-9])([a-z0-9-]+\\.)+(com|net|org|gg|io)"
      - "(discord\\.gg/|discordapp\\.com/invite/)"
    sensitivity: medium
    whitelist:
      - "tu-servidor.com"
      - "discord.gg/tuinvite"
    action: "warn"
  anti-spam:
    enabled: true
    global-cooldown-ms: 1500
    flood:
      max-messages: 5
      window-seconds: 3
      similarity-threshold: 0.8
    action: "block"

replay:
  capacity: 50

afk:
  enabled: true
  auto-timeout-seconds: 300
  check-interval-seconds: 30
  unset-on-activity: true
  notify-unset: true

slowmode:
  enabled: true
  seconds: 3
  bypass-permission: hchat.slowmode.bypass

chat-lock:
  enabled: true
  bypass-permission: hchat.chatlock.bypass

staff-chat:
  format: "&c[Staff] {player} &8» &f{message}"
  log-to-console: true

death-message:
  enabled: true
  format: "&c☠ &7{player} &fdied by {cause}"
  custom:
    FALL: "&c{player} learned to fly."
    LAVA: "&c{player} made friends with the magma."
```

</details>

### 🔁 Available placeholders

| Placeholder     | Replaced with                                          |
|-----------------|--------------------------------------------------------|
| `{sender}`      | Sender's display name                                  |
| `{receiver}`    | Recipient's display name                               |
| `{target}`      | Target name (spy view)                                 |
| `{message}`     | The actual message body                                |
| `{player}`      | Player name (mentions, offline messages, etc.)        |
| `%player_name%` | Resolved by PlaceholderAPI for any hook registered on it|

The renderer resolves every entry in two passes: first placeholders are
substituted, then `MessageFormatter` parses `&`-color codes and hex
(`&#RRGGBB`) so styling works everywhere.

#### 🎯 Dynamic chat tokens

hChat renders the following NoNChat-style tokens itself, inside every chat
format (default format, `chat.formats.*` and channel formats):

| Token      | Replaced with                                                    |
|------------|------------------------------------------------------------------|
| `[ping]`   | Colored latency indicator (green ≤ `good-max`, yellow ≤ `medium-max`, red above) with optional `ms` value |
| `[item]`   | Main-hand item; hover shows a tooltip with name, enchantments, durability bar and lore |
| `[coords]` | Player position as `x, y, z`                                     |
| `[world]`  | Current world name                                               |
| `[afk]`    | `[AFK]` prefix when the player is away (built-in `/afk` + inactivity detection, PAPI fallback) |

Example format: `"[ping] [afk]&7{player}&8: &f{message}"`. Each token
can be enabled/disabled and tuned under the `placeholders:` section of
`config.yml` (ping colors/thresholds, coords/world formats, the AFK
placeholder and its `afk-values`). Tokens are resolved at the `Component`
level after PAPI, so a literal `[ping]` typed by a player is never
turned into an indicator.

---

## 📡 Chat Channels

Channels give every conversation its own format, visibility range,
permissions and cooldown. Ship config includes `global`, `local`
(distance-based), `staff`, `vip`, `trade` and `world` (per-world).

| Feature | Where |
|---------|-------|
| Channel format, range, cooldown, aliases | `channels.<id>.*` |
| Speak / see permissions | `speak-permission`, `see-permission` |
| Distance-based local chat + action-bar hint | `range`, `action-bar-hint` |
| Per-world channel (never crosses worlds) | `per-world: true` + `{world}` |
| Auto-join by permission on entry | `default-by-permission` |
| Action-bar hint on switch / join | `hint-on-switch`, `hint-on-join` |

```yaml
channels:
  local:
    format: "&8[Local] &7{player} &8» &f{message}"
    range: 80
    action-bar-hint: "&eThere are people talking near you"
    alias: "#l"
  staff:
    format: "&c[Staff] {player} &8» &f{message}"
    speak-permission: hchat.channel.staff
    see-permission: hchat.channel.staff.see
    alias: "#staff"
```

In game: `/channel` lists channels (click to switch), `/channel <name>`
switches your active channel (remembered between sessions) and
`#staff hello` sends to a channel from anywhere. The default channel
(`global`) uses `chat.default-format` / the permission-based
`chat.formats` (vip/mvp/admin).

---

## 🌐 Localization

Language files live in `plugins/hChat/lang/`. The plugin ships
`lang_en.yml` and supports any number of additional files.

```yaml
# lang_es.yml
no-permission: "&cYou do not have permission!"
player-only: "&cOnly players can use this command!"
player-offline: "&cPlayer is offline!"
mentioned: "&#ffffffYou were mentioned in chat by &#84FFB8{player}!"
```

Switch the **server-wide** language from `config.yml`:

```yaml
lang: es
```

Each player can also override their own language with
`/hchat lang <lang>` (stored in `plugins/hChat/storage/player-lang.yml`).
At runtime, messages are looked up in the per-player file first and
fall back to the server-wide `lang` setting from `config.yml` when the
key is missing or the player has not chosen a language.

---

## 🔌 Placeholders

When [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
is installed, hChat registers the placeholders listed below.

| Placeholder                | Description                                                                         |
|----------------------------|-------------------------------------------------------------------------------------|
| `%hchat_spy_enabled%`      | `Spy Mode: Enabled` / `Spy Mode: Disabled`                                          |
| `%hchat_ignored%`          | `You are ignoring players` / `You are not ignoring anyone`                          |
| `%hchat_ignored_<name>%`   | Per-player ignore check (`true` / `false`, case-insensitive)                        |
| `%hchat_ignored_count%`    | Number of players this player is currently ignoring                                 |
| `%hchat_dnd_enabled%`      | `DND: Enabled` / `DND: Disabled`                                                    |
| `%hchat_last_sender%`      | Name of the player who most recently sent you a private message                     |
| `%hchat_last_message_part%`| First 30 characters (with ellipsis) of the last private message you received       |
| `%hchat_last_seen%`        | `Received N minutes ago` / `No recent messages`                                     |

Any placeholder that resolves against a player who is currently offline
returns the safe (no-player) phrasing defined in `lang_en.yml`.

---

## 🛡️ Filters

Every chat message runs through a **filter chain** in this order:
`word-filter` → `anti-unicode` → `anti-caps` → `anti-ad` → `anti-spam`.
Each filter supports the same actions:

| Action       | Behavior                                                                                |
|--------------|-----------------------------------------------------------------------------------------|
| `block`      | Drops the message and notifies the sender with `message-filtered`.                      |
| `mask`       | Replaces the offending part with `*`, then forwards the message.                        |
| `warn`       | Lets the message through and pings every player with `hchat.monitor.filter`.            |
| `lowercase`  | Transforms the message to lowercase (anti-caps).                                        |

### Word filter

```yaml
word-filter:
  enabled: true
  words:
    - "blacklist1"
    - "blacklist2"
  action: "block"   # block | mask | warn
```

### Anti-caps

Blocks / masks / lowercases messages that are mostly uppercase
(`max-uppercase-percent`, `min-length`).

### Anti-unicode

Blocks invisible characters used to bypass filters: zero-width spaces,
direction overrides, BOM, tag characters (`U+E0000+`) and soft hyphens.

### Anti-advertisement

Matches URLs and Discord invites with configurable regex patterns plus a
whitelist (substring match) that always passes.

```yaml
filters:
  anti-ad:
    enabled: true
    patterns:
      - "(?<![a-zA-Z0-9])([a-z0-9-]+\\.)+(com|net|org|gg|io)"
      - "(discord\\.gg/|discordapp\\.com/invite/)"
    whitelist:
      - "tu-servidor.com"
    action: "warn"
```

> ℹ️ The default TLD list omits `me` on purpose — `help.me` would
> false-positive on normal prose.

### Anti-spam

A global per-player cooldown (`global-cooldown-ms`) plus flood detection:
more than `max-messages` within `window-seconds`, or a near-identical
repeat (`similarity-threshold`), are blocked.

```yaml
filters:
  anti-spam:
    enabled: true
    global-cooldown-ms: 1500
    flood:
      max-messages: 5
      window-seconds: 3
      similarity-threshold: 0.8
    action: "block"
```

---

## 📦 Broadcasts

`/broadcast <message>` (alias `/bc`) shows a configurable multi-line
broadcast to every player on the server. The format is set under
`broadcast.format` in `config.yml` and supports placeholders plus hex
colors. Players with `hchat.broadcast.color` can use color codes inside
the broadcast body.

For scheduled announcements, drop additional entries under
`broadcast.<id>` with `enabled`, `message`, `interval` (in seconds) and
`display-in-console`:

```yaml
broadcast:
  example1:
    enabled: true
    message: "&#F52727This is a recurring broadcast every 10 minutes"
    interval: 600
    display-in-console: true
```

The scheduler is implemented in `AutoBroadcastManager`.

> ℹ️ **Discord bridge:** when DiscordSRV is installed at startup,
> hChat logs a confirmation and reserves the integration for the
> upcoming cross-server chat bridge (Phase 5 in `ROADMAP.md`). No
> broadcast or `/msg` is currently relayed to Discord.

---

## 📨 Mail (Offline Messages)

If a player runs `/msg Steve hola` while `Steve` is offline, the message
is stored with the **sender name and timestamp** in `OfflineMessageStore`
(capped at `offline-messages.max-pending` per recipient). When Steve logs
in he sees a clickable summary — `&eYou have 3 pending messages. Click to
read.` — instead of automatic delivery:

| Command                          | What it does                                  |
|----------------------------------|-----------------------------------------------|
| `/hchat mail read`               | Lists every pending message (click to reply)  |
| `/hchat mail clear`              | Purges your own pending messages              |
| `/hchat mail send <p> <msg>`     | Shortcut for `/msg`                           |

Messages are only removed when read or cleared, and a sound plays on
join if `offline-messages.sound.enabled: true`. If the target has never
joined the server (`hasPlayedBefore() == false`), `/msg` falls back to
the standard `player-not-found` message.

---

## 😴 AFK

hChat has a built-in AFK system — no Essentials needed:

- `/afk` toggles it; `/afk <message>` goes AFK with a reason.
- After `afk.auto-timeout-seconds` (default 300) of inactivity, players
  are marked AFK automatically.
- Any real activity (movement, chat, commands, interactions) removes the
  state (`afk.unset-on-activity`); returning players are announced in
  chat (`afk.notify-unset`).
- The `[afk]` chat token uses this state (with a PAPI fallback when the
  built-in feature is disabled).

---

## 🔌 Developer API

`HChatProvider` is the public entry point for other plugins. Get the
singleton, then use its accessors:

```java
import dev.hauch.hchat.api.HChatProvider;
import dev.hauch.hchat.model.ChatChannel;

public class MyListener implements Listener {

    public void demo() {
        HChatProvider api = HChatProvider.get();

        // who is spying right now
        for (java.util.UUID uuid : api.spyManager().getSpies()) {
            // ...
        }

        // last private message received by a player
        api.messageHistory().getLastMessage(player.getUniqueId());

        // the player's active channel
        ChatChannel channel = api.channelManager().activeChannel(player);

        // broadcast through an existing channel (staff, vip, ...),
        // honoring its see-permission
        api.publishToChannel(api.channelManager().get("staff"),
                "Server restarting in 5 minutes!");

        // DND state
        api.dndManager().isDnd(player);
    }
}
```

> ℹ️ `HChatProvider.get()` throws `IllegalStateException` before the plugin
> is fully initialized (first ticks). Access it from `ServerLoadEvent` or
> later. Other plugins depending on hChat should declare it as a
> `softdepend` (or `depend`) in their `plugin.yml`.

---

## 🌐 Proxy note (Velocity / BungeeCord)

hChat runs inside the backend servers, not on the proxy:

- `Player#getUniqueId` and `%player_name%` keep working behind Velocity and
  BungeeCord — Paper forwards the real UUID and name to the backend.
- Private messages, offline messages, ignore and DND state are **per
  backend server**. A player connected to server A cannot `/msg` a player
  on server B; for cross-server chat use a proxy-side solution or the
  DiscordSRV bridge.
- `{world}` and `[coords]` placeholders resolve on the backend the player
  is currently on.
- The update checker, bStats and the auto-broadcast scheduler run once per
  backend instance — disable auto-broadcast on all but one server if you
  run a network and do not want duplicated announcements.

---

## 🧩 Dependencies

### ✅ Required

| Plugin                                                                                                  | Version | Purpose                            |
|----------------------------------------------------------------------------------------------------------|---------|------------------------------------|
| [Paper](https://papermc.io)                                                                             | 1.20+   | Server API (Adventure, `AsyncChatEvent`) |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)                               | 2.11+   | Placeholders, Vault/LuckPerms bridge |
| [LuckPerms](https://luckperms.net)                                                                       | 5.4+    | Permission management              |

### 🟡 Optional (`softdepend`)

| Plugin                                          | Purpose                                         |
|-------------------------------------------------|-------------------------------------------------|
| [Vault](https://github.com/milkbowl/Vault)      | Economy + chat formatting hook                  |
| [DiscordSRV](https://github.com/DiscordSRV/DiscordSRV) | Detected at startup; full cross-chat bridge planned for a future release |

If DiscordSRV is present at startup, hChat logs a confirmation so
admins know the hook is registered. Forwarding of `/msg` and
`/broadcast` to Discord is **not** implemented yet — see `ROADMAP.md`
Phase 5 for the planned bridge.

### 📊 Metrics (bStats)

hChat bundles [bStats](https://bstats.org) for anonymous plugin
statistics — no extra download needed. Server owners can disable it with
`metrics.enabled: false` in `config.yml` or globally in
`plugins/bStats/config.yml` (the toggle applies on the next server
start). The plugin id is defined in `HChat.java` (`BSTATS_PLUGIN_ID`);
register hChat at bstats.org and set it there to start collecting data.

---

## 🛠️ Building from Source

Requirements: **JDK 21** and **Maven 3.5+**.

```bash
# Clone the repository
git clone https://github.com/hauchdev/hChat.git
cd hChat

# Compile & package
mvn clean package

# The shaded JAR is at: plugin-dist/target/hChat-<version>.jar
```

> ℹ️ **Releases** — the plugin version lives in **one place**: the
> `<revision>` property at the top of the root `pom.xml`. Change it there
> (or pass `-Drevision=1.3.0` to Maven) and every module, the filtered
> `plugin.yml` version and the final jar name follow automatically. You
> never have to touch the other 13 POM files.

CI is wired through `.github/workflows/build.yml`: every push to `dev`
runs `mvn clean verify`. Tagged `v*` builds additionally publish a
GitHub Release with the packaged JAR.

---

## 🤝 Contributing

<p align="center">
  Pull requests and ideas are very welcome.
  <br><br>
  <a href="https://github.com/hauchdev/hChat/issues">
    <img src="https://img.shields.io/badge/🐛%20Report%20a%20Bug-ED8B00?style=for-the-badge" alt="Report Bug">
  </a>
  <a href="https://github.com/hauchdev/hChat/pulls">
    <img src="https://img.shields.io/badge/✨%20Submit%20a%20PR-6C5CE7?style=for-the-badge" alt="Submit PR">
  </a>
  <a href="https://github.com/hauchdev/hChat/discussions">
    <img src="https://img.shields.io/badge/💬%20Start%20a%20Discussion-00CEC9?style=for-the-badge" alt="Discussion">
  </a>
</p>

Please follow Conventional Commits (Spanish or English both fine),
update `lang_en.yml` / `plugin.yml` / `README.md` whenever you add a
command, permission, or feature, and make sure `mvn clean verify`
stays green. See [ROADMAP.md](ROADMAP.md) for the larger picture and
upcoming phases.

---

## 📄 License

<p align="center">
  Released under the MIT License.
  <br>
  <a href="https://github.com/hauchdev/hChat/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/hauchdev/hChat?style=for-the-badge&color=00CEC9" alt="License">
  </a>
</p>

---

<p align="center">
  <sub>
    Made with ❤️ by <a href="https://github.com/hauchdev">Hauchdev</a>
    <br>
  </sub>
</p>

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,4,6&height=120&section=footer" width="100%"/>
</p>
