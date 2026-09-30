# 🗺️ hChat Public Roadmap

Welcome to the official roadmap for **hChat**. Our mission is to provide a premium-grade chat experience for Minecraft servers, 100% free and open-source.

---

## ✅ Completed Milestones

### **v1.4.x — The Modern Foundation**
*Status: Released*
- [x] **MiniMessage Support:** Full integration of Adventure's MiniMessage for modern formatting.
- [x] **Format Overrides:** Per-channel formatting modes (Legacy, MiniMessage, or Auto).
- [x] **Smart Migration:** Automatic configuration updates and backups.
- [x] **Performance Metrics:** Implementation of non-intrusive analytics via `faststats.dev`.
- [x] **Dependency Optimization:** LuckPerms is now an optional `softdepend`.

---

## 🚀 Upcoming Releases

### **v1.5.0 — The Infrastructure Update** 🏗️
*Focus: Performance & Data Scalability*
- [ ] **SQL Storage Engine:** Support for MySQL, MariaDB (via HikariCP), and SQLite.
- [ ] **Async Everything:** Moving all I/O operations away from the main server thread.
- [ ] **Data Migrator:** Built-in tool to transition from YAML to SQL seamlessly.
- [ ] **Enhanced Tab-Completion:** Context-aware suggestions for all subcommands and channels.

### **v1.6.0 — The Social Bridge** 🌐
*Focus: External Integrations*
- [ ] **Pro Discord Bridge:** Bidirectional synchronization using Webhooks for rich player identities.
- [ ] **Cross-Server Sync:** Synchronize ignores, DND, and mail across multiple backend servers via SQL.
- [ ] **Discord DMs:** Bridge private messages between the game and Discord.

### **v1.7.0 — The Cosmetic Phase** 🎨
*Focus: Player Expression*
- [ ] **Equippable Badges:** GUI-based badge selection system (`/badges`).
- [ ] **Custom Emojis:** Native support for resource pack glifos and legacy shortcodes (e.g., `:heart:`).
- [ ] **Chat Reactions:** Interactive games and rewards to boost player engagement.

---

## 🔮 Future Vision (v2.0.0+)

- **Proxy-Native Support:** Dedicated companion modules for **Velocity** and **BungeeCord**.
- **Global Network Chat:** True cross-proxy communication for large networks.
- **Developer Ecosystem:** Expanded API and documentation for third-party extensions.
- **Web Dashboard:** An optional self-hosted interface for real-time format editing and moderation.

---

## 💡 Our Philosophy

1. **Free Forever:** The core features of hChat will always be MIT-licensed and free.
2. **Quality First:** We build every feature to compete with the best paid alternatives.
3. **Open for Feedback:** We shape our roadmap based on community needs. [Open a Discussion!](https://github.com/hauchdev/hChat/discussions)

> *Dates and versions are tentative and subject to change based on community feedback and development progress.*
