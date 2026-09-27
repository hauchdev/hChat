# hChat — Roadmap

> Public development status of **hChat**. Last updated: 2026-09-27 ·
> Current version: **1.4.1** · MIT core, free on Modrinth.

---

## ✅ v1.4.0 — "Adoption release" (free)

- [x] **MiniMessage support** — global `chat.format-mode` (`legacy` |
  `minimessage` | `auto`) plus a per-channel `format-mode` override; classic
  `&` codes and `&#RRGGBB` hex are still translated in MiniMessage mode and
  player-typed text is escaped to prevent tag injection.
- [x] **LuckPerms → softdepend** — runtime detection, graceful degradation;
  it is no longer a requirement.
- [x] **faststats.dev** — replaces bStats as the metrics system
  (always on, zero configuration).
- [x] Automatic `config.yml` migration (config-version 7 → 10).

## 🔜 v1.5.0 — "Storage layer" (free)

- [ ] Storage layer with a `StorageProvider` interface
- [ ] **SQLite** (embedded, default) and **MySQL/MariaDB** (HikariCP)
- [ ] Automatic YAML → SQL migrator (first run)
- [ ] Async everywhere (never block the main thread)
- [ ] `/hchat storage migrate` for admins

## 🔮 v1.7.0 — "Discord bridge" (free)

- [ ] Bidirectional channel ↔ Discord channel bridge (via DiscordSRV)
- [ ] `/msg` from Discord into the game (with toggle)
- [ ] Separate formats for game→Discord and Discord→game
- [ ] Optional first-party webhooks (no DiscordSRV required)

## 🌐 v1.6.0+ — Extensions

- [ ] Companion module for **Velocity** (+ BungeeCord if there is demand)
- [ ] Cross-server global chat + cross-server `/msg`
- [ ] Equippable badges with GUI, chat reactions and resource pack emojis

## 🚀 v2.0.0 — "hChat Pro v2"

- [ ] Web dashboard (live format editor, optional self-hosted)
- [ ] Extended API for third-party developers

---

## 📌 Notes

- The core is and will remain **MIT** and free.
- Paid extensions live in a separate private repository
  (`/premium/`, ignored by git) and never affect the core.
- Versions and dates are tentative; the order may be adjusted based on
  community feedback.
