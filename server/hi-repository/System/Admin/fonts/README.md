# Admin fonts

Managed custom fonts for Helical Insight. Files in this folder are the application-owned font store (separate from OS fonts).

## Supported files

| Extension | Use |
|-----------|-----|
| `.ttf`, `.otf`, `.ttc` | Browser (HCR), JVM / AWT, Jasper PDF |
| `.woff`, `.woff2` | Browser / Chrome only |
| `.zip` / `.jar` | Upload packages; font entries are extracted here |

Place font files **directly** in this directory (no nested folders).

## How fonts get here

- Upload via the existing import flow with `type=font` (`/importFile`), or
- Copy supported font files into this folder and refresh (import refresh / restart / next report that loads fonts).

## How they are used

- **Catalog:** `GET /getFonts` lists managed fonts (`isManaged: true`) plus OS fonts (`isManaged: false`).
- **HCR canvas:** managed fonts download via `/getExternalResource?path=System/Admin/fonts/<fileName>`.
- **Jasper PDF:** TTF/OTF/TTC are registered with FontFactory under the catalog display name so `pdfFontName` resolves.

Display names come from font metadata when possible; if two files share the same metadata name, the later file is listed under its file base name so each file stays selectable.

## Notes

- Prefer one file per face you need; duplicate copies of the same font (different filenames, same metadata) clutter the picker.
- OS fonts appear in the picker when the JVM can see them; they are not stored in this folder.
- After adding or removing files here, managed fonts are re-scanned on the next font refresh (import, report generate, or export).
