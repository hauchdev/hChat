<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=0,2,4,6&height=200&section=header&text=hChat&fontSize=70&fontAlignY=35&desc=Minecraft%20Chat%20Redefined&descAlignY=55" width="100%"/>
</p>

<p align="center">
  <b>hChat</b> — A modern and complete chat system for Paper 1.20+ servers.
  <br>
  Direct messages, mentions, spy mode, ignored messages, placeholders, and more.
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
  <a href="#🔌-placeholders">Placeholders</a> •
  <a href="#🧩-dependencies">Dependencies</a> •
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
      <sub>Customizable format with hover text and click to reply</sub>
    </td>
    <td align="center" width="33%">
      <b>🔔 Mentions @player</b><br>
      <sub>Configurable sound + highlight color + notification</sub>
    </td>
    <td align="center" width="33%">
      <b>👁️ Spy Mode</b><br>
      <sub>Monitor private messages with special permission</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>🚫 Ignored Systems</b><br>
      <sub>Block messages from unwanted players</sub>
    </td>
    <td align="center" width="33%">
      <b>🌍 Multi-language</b><br>
      <sub>Complete YAML localization system</sub>
    </td>
    <td align="center" width="33%">
      <b>🔗 PlaceholderAPI</b><br>
      <sub><code>%hchat_spy_enabled%</code> & <code>%hchat_ignored%</code></sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <b>🎨 Colors & Format</b><br>
      <sub>Support hex <code>&#RRGGBB</code> + legacy codes <code>&</code></sub>
    </td>
    <td align="center" width="33%">
      <b>🖱️ Hover & Click</b><br>
      <sub>Tooltips and clickable actions in each message</sub>
    </td>
    <td align="center" width="33%">
      <b>⏪ /reply</b><br>
      <sub>Respond to the latest private message instantly.</sub>
    </td>
  </tr>
</table>

---

## ⚡ Quick Start

### 📥 Installation

```bash
# 1. Download the JAR from Releases
wget https://github.com/hauchdev/hChat/releases/latest/download/hChat.jar

# 2. Place it in your server's plugins folder.
mv hChat.jar /servidor/plugins/

# 3. Make sure you have PlaceholderAPI and LuckPerms installed

# 4. Restart or reload your server
```

---

## 🎮 Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|---------|
| `/message <player> <msg>` | `/msg`, `/tell`, `/w`, `/m`, `/whisper` | Send a private message | `hchat.message` |
| `/reply <message>` | `/r` | Reply to the last message | `hchat.reply` |
| `/hchat reload` | — | Reload settings and languages | `hchat.reload` |
| `/hchat help` | — | Displays the list of commands | `hchat.help` |
| `/clear` | — | Clear the chat of all players | `hchat.clear` |
| `/ignore <player>` | — | Ignore / un-ignore a player | `hchat.ignore` |
| `/spy` | — | Toggle spy mode | `hchat.spy` |
---

## 🔒 Permissions

<table>
  <tr>
    <th>Permission</th>
    <th>Default</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><code>hchat.*</code></td>
    <td><code>op</code></td>
    <td>Access to all plugin commands</td>
  </tr>
  <tr>
    <td><code>hchat.message</code></td>
    <td><code>true</code></td>
    <td>Allows you to use <code>/msg</code></td>
  </tr>
  <tr>
    <td><code>hchat.reply</code></td>
    <td><code>true</code></td>
    <td>Allows you to use <code>/reply</code></td>
  </tr>
  <tr>
    <td><code>hchat.reload</code></td>
    <td><code>op</code></td>
    <td>Allows you to reload the plugin</td>
  </tr>
  <tr>
    <td><code>hchat.help</code></td>
    <td><code>true</code></td>
    <td>Allows you to view the help</td>
  </tr>
  <tr>
    <td><code>hchat.clear</code></td>
    <td><code>op</code></td>
    <td>It allows you to clear the chat</td>
  </tr>
  <tr>
    <td><code>hchat.ignore</code></td>
    <td><code>true</code></td>
    <td>Allows you to ignore players</td>
  </tr>
  <tr>
    <td><code>hchat.spy</code></td>
    <td><code>op</code></td>
    <td>Allows you to activate spy mode</td>
  </tr>
  <tr>
    <td><code>hchat.spy.all</code></td>
    <td><code>op</code></td>
    <td>Receive ALL messages without exception</td>
  </tr>
</table>

---

## ⚙️ Configuration

The `config.yml` file is automatically generated when you start the plugin for the first time.

<details>
  <summary><b>📄 View full config.yml</b></summary>

```yaml
lang: en

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
```

</details>

### Available variables

| Variable | Replacement |
|----------|-----------|
| `{sender}` | Sender's name |
| `{receiver}` | Name of recipient |
| `{target}` | Target name (spy) |
| `{message}` | Message content |
| `{player}` | Player name (mentions) |

---

## 🌐 Localization

Language files are stored in `plugins/hChat/lang/`. The plugin includes `lang_en.yml` by default.

To add a new language:

```yaml
# lang_es.yml
no-permission: "&c¡No tienes permiso!"
player-only: "&c¡Solo los jugadores pueden usar este comando!"
player-offline: "&c¡El jugador está desconectado!"
mentioned: "&#ffffffFuiste mencionado por &#84FFB8{player}!"
```

Change the active language in `config.yml`:

```yaml
lang: es
```

---

## 🔌 Placeholders

| Placeholder | Description |
|-------------|-------------|
| `%hchat_spy_enabled%` | Muestra "Spy Mode: Enabled" o "Spy Mode: Disabled" |
| `%hchat_ignored%` | It shows if the player is ignoring someone. |

These placeholders require [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

---

## 🧩 Dependencies

### ✅ Necessary

| Plugin | Version | Purpose |
|--------|---------|-----------|
| [Paper](https://papermc.io) | 1.20+ | Server API |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | 2.11+ | Placeholders |
| [LuckPerms](https://luckperms.net) | 5.4+ | Permissions Management |


---

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/hauchdev/hChat.git
cd hChat

# Compile with Maven
mvn clean package

The JAR file will be located at: target/hChat-VERSION.jar
```

---

## 🤝 Contributing

<p align="center">
  Contributions are welcome!
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

---

## 📄 License

<p align="center">
  Distributed under the MIT License.
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
