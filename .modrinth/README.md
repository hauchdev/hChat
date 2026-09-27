# Publishing hChat to Modrinth

Project: https://modrinth.com/plugin/hchat (owner: `hauchdev`)
Project ID: `P0DczmuD`

This folder holds everything needed to publish releases without touching
the Modrinth website.

| File | Purpose |
|------|---------|
| `project.json` | Project-level metadata reference (slug, categories, license, links) |
| `page.md` | Project page body (Markdown) |
| `version-template.json` | Reusable version payload: loaders, game versions, dependency IDs |
| `changelogs/<version>.md` | Per-release changelog shown on the version page |
| `publish.sh` | One-command publisher (curl against the Modrinth API) |

Dependency IDs (from the Modrinth API):

| Plugin | ID | Type |
|--------|----|------|
| PlaceholderAPI | `lKEzGugV` | required |
| LuckPerms | `Vebnzrzj` | optional |
| DiscordSRV | `UmLGoGij` | optional |

Vault is not on Modrinth, so it is not listed.

## Publishing a release

1. Build the shaded jar:
   `mvn clean package`
2. Write the changelog: `.modrinth/changelogs/<version>.md`
   (copy the matching section from the root `CHANGELOG.md`).
3. Bump `<revision>` in the root `pom.xml` if not done yet, then commit
   and tag `v<version>`.
4. Get a personal access token with the **Create versions** scope from
   https://modrinth.com/settings/pats and export it:
   `export MODRINTH_TOKEN=...`
5. Publish:
   `.modrinth/publish.sh <version> plugin-dist/target/hChat-<version>.jar`

The script fills `changelog` from the matching file, uploads the jar and
marks the version featured. New game versions only need a one-line edit
in `version-template.json`.

## CI (optional)

The GitHub workflow builds `hChat-*.jar` on every tag push and creates the
GitHub Release with the jar. **Modrinth publishing is always manual** so
each upload can be reviewed first:

1. Download the jar from the freshly created GitHub Release (or build it
   locally).
2. `export MODRINTH_TOKEN=...` (PAT with the **Create versions** scope).
3. `.modrinth/publish.sh <version> hChat-<version>.jar`
