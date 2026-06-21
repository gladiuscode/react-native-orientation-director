# react-native-orientation-director

React Native turbo-module library (Kotlin + ObjC, new architecture only). Monorepo with Yarn workspaces.

## Commands

| Command | Purpose |
|---|---|
| `yarn` | Install deps (Yarn 4.11.0, **npm won't work**) |
| `yarn lint` | ESLint on `src/` + `example/` |
| `yarn expo-plugin lint` | ESLint on `plugin/` |
| `yarn typecheck` | tsc (root) |
| `yarn expo-plugin typecheck` | tsc (plugin) |
| `yarn test` | Jest (root library tests) |
| `yarn expo-plugin test` | Jest (plugin tests, snapshots) |
| `yarn prepare` | Build expo plugin + bob build → generates `lib/` |
| `yarn release` | release-it (publish to npm + GitHub release) |

CI runs: lint → typecheck → test → build-library → build-android → build-ios.

## Architecture

- **Library entry**: `src/index.tsx` — exports `RNOrientationDirector` class, 3 hooks, 3 enums
- **Native spec**: `src/NativeOrientationDirector.ts` — TurboModule (`RNOrientationDirectorSpec`), codegen config in `package.json`
- **Expo plugin**: `plugin/` workspace, output → `lib/plugin/`, re-exported via `app.plugin.js`
- **Example app**: `example/` workspace, references local lib via workspace
- **Build**: `react-native-builder-bob` → `lib/module/` (ESM) + `lib/typescript/` (declarations)

## Key constraints

- **Only Yarn** (v4.11.0, nodeLinker: node-modules). Do not use npm.
- **New architecture only** (Fabric, TurboModules). Old arch not supported since v3.0.0.
- `lib/` is gitignored (generated). After modifying source, run `yarn prepare` to rebuild.
- JS changes hot-reload in example app; native changes need `yarn example android` / `yarn example ios` rebuild.
- Plugin runs via Expo config plugins (SDK 50+).
- Commits must follow [conventional commits](https://www.conventionalcommits.org/) — enforced by commitlint + lefthook.
- Pre-commit hooks lint + typecheck staged files in parallel.

## Testing

- Root tests: `src/__tests__/` (Jest, preset `react-native`)
- Plugin tests: `plugin/__tests__/` (Jest via `expo-module-scripts`, snapshot-based)
- `modulePathIgnorePatterns`: `example/node_modules`, `lib/`
- Run both suites before CI-relevant changes.
