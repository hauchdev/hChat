<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,4,6&height=200&section=header&text=hChat&fontSize=70&fontAlignY=35&desc=Minecraft%20Chat%20Redefined&descAlignY=55" width="100%"/>
</p>

<p align="center">
  <b>hChat</b> — A modern and complete chat system for Paper 1.20+ servers.
  <br>
  Direct messages, mentions, spy mode, ignored players, broadcasts, offline messages, word filter, multi-language, and more.
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
- [🌐 Localization](#🌐-localization)
- [🔌 Placeholders](#🔌-placeholders)
- [🛡️ Word Filter](#🛡️-word-filter)
- [📦 Broadcasts](#📦-broadcasts)
- [📨 Offline Messages](#📨-offline-messages)
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
      <b>🛡️ Word Filter</b><br>
      <sub>Block, mask, or warn on blacklisted words</sub>
    </td>
    <td align="center" width="33%">
      <b>📨 Offline Messages</b><br>
      <sub>Delivered with a sound effect on next login</sub>
    </td>
    <td align="center" width="33%">
      <b>📜 Chat Logger</b><br>
      <sub>Rotating daily logs with configurable retention</sub>
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
| `/spy`                               | —                              | Toggle spy mode (see all private messages)                | `hchat.spy`          |
| `/clear`                             | —                              | Clear the chat of every player                             | `hchat.clear`        |
| `/hchat reload`                      | —                              | Reload `config.yml` and language files                     | `hchat.reload`       |
| `/hchat help`                        | —                              | Show the in-game help menu                                 | `hchat.help`         |
| `/hchat lang <lang>`                 | —                              | Switch your own player language                            | `hchat.lang`         |

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
| `hchat.monitor.filter`      | `op`    | Receive warnings when the word filter trips on a player        |
| `hchat.bypass-log`          | `op`    | Send private messages without writing them to the chat logger |

---

## ⚙️ Configuration

`config.yml` is generated automatically on first start. The same file
also controls every audio / visual cue the plugin emits.

<details>
  <summary><b>📄 View full config.yml</b></summary>

```yaml
lang: en
debug: false

mentions:
  sound:
    enabled: true
    sound: "entity.experience_orb.pickup"
    volume: 1.0
    pitch: 1.0
  colors:
    enabled: true
    color: "&#54A3FF"

direct-messages:
  sender:
    format: "&7[&eYou &7-> &e{receiver}&7] &7{message}"
    hover-text:
      enabled: false
      text:
        - "&7Sent to &e{receiver}"
        - "&eClick to reply"
  receiver:
    format: "&7[&e{sender} &7-> &eYou&7] &7{message}"
    hover-text:
      enabled: true
      text:
        - "&7From &e{sender}"
        - "&eClick to reply"
  clickable-actions:
    enabled: true
    reply-command: "/msg {sender}"

spy-format: "&e{sender} &7-> &e{target}&7: &7{message}"

hover-text:
  enabled: true
  format:
    - "&#54FF7F %player_name%"
    - "&7"
    - "&eClick to send a private message"

broadcast:
  enabled: true
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
  words:
    - "blacklist1"
    - "blacklist2"
  action: "block"   # block | mask | warn
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

## 🛡️ Word Filter

The built-in filter checks every chat and `/msg` message before it is
sent. Three actions are available:

| Action  | Behavior                                                                                  |
|---------|-------------------------------------------------------------------------------------------|
| `block` | Drops the message and notifies the sender with `message-filtered` from the language file. |
| `mask`  | Replaces every character of every matching word with `*`, then forwards the message.       |
| `warn`  | Lets the message through and pings every player with `hchat.monitor.filter`.               |

```yaml
word-filter:
  enabled: true
  words:
    - "blacklist1"
    - "blacklist2"
  action: "block"   # block | mask | warn
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

## 📨 Offline Messages

If a player runs `/msg Steve hola` while `Steve` is offline, the
message is stored in `OfflineMessageStore` (capped at
`offline-messages.max-pending` per recipient). When Steve logs in, the
`PlayerJoinListener`:

1. Drains every pending message.
2. Sends them in order.
3. Optionally plays `offline-messages.sound` if `sound.enabled: true`.

If the target has never joined the server (`hasPlayedBefore() == false`),
the command falls back to the standard `player-not-found` language
message.

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

---

## 🛠️ Building from Source

Requirements: **JDK 17** and **Maven 3.8+**.

```bash
# Clone the repository
git clone https://github.com/hauchdev/hChat.git
cd hChat

# Compile & package
mvn clean package

# The shaded JAR is at: target/hchat-<version>.jar
```

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
