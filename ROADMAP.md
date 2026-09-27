# hChat — Roadmap

> Estado del desarrollo público de **hChat**. Última actualización: 2026-09-20 ·
> Versión actual: **1.4.0** · Core MIT y gratuito en Modrinth.

---

## ✅ v1.4.0 — "Adoption release" (gratuita)

- [x] **Soporte MiniMessage** — `chat.format-mode` global (`legacy` |
  `minimessage` | `auto`) más override `format-mode` por canal; los códigos
  clásicos `&` y `&#RRGGBB` siguen traduciéndose en modo MiniMessage y el
  texto de jugadores se escapa para evitar inyección de tags.
- [x] **LuckPerms → softdepend** — detección en runtime, degradación elegante;
  ya no es requisito instalarlo.
- [x] **faststats.dev** — sustituye a bStats como sistema de métricas
  (token en `metrics.token`; vacío = desactivado).
- [x] Migración automática de `config.yml` (config-version 7 → 8).

## 🔜 v1.5.0 — "Storage layer" (gratuita)

- [ ] Capa de almacenamiento con interfaz `StorageProvider`
- [ ] **SQLite** (embebida, default) y **MySQL/MariaDB** (HikariCP)
- [ ] Migrador automático YAML → SQL (primera ejecución)
- [ ] Async everywhere (no bloquear el hilo principal)
- [ ] `/hchat storage migrate` para admins

## 🔮 v1.7.0 — "Discord bridge" (gratuita)

- [ ] Puente bidireccional canal ↔ canal de Discord (vía DiscordSRV)
- [ ] `/msg` desde Discord hacia el juego (con toggle)
- [ ] Formatos independientes juego→Discord y Discord→juego
- [ ] Webhooks propios opcionales (sin DiscordSRV)

## 🌐 v1.6.0+ — Extensiones

- [ ] Módulo companion para **Velocity** (+ BungeeCord si hay demanda)
- [ ] Chat global cross-server + `/msg` cross-server
- [ ] Badges equipables con GUI, chat reactions y emojis de resource pack

## 🚀 v2.0.0 — "hChat Pro v2"

- [ ] Dashboard web (editor de formatos en vivo, opcional self-hosted)
- [ ] API ampliada para desarrolladores terceros

---

## 📌 Notas

- El núcleo es y seguirá siendo **MIT** y gratuito.
- Las extensiones de pago viven en un repositorio privado separado
  (`/premium/`, ignorado por git) y nunca afectan al core.
- Versiones y fechas son orientativas; el orden puede ajustarse según
  feedback de la comunidad.
