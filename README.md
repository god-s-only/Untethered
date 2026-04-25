# Untethered

A lightweight, Shizuku-powered terminal emulator for Android — built for developers and power users who need real shell access on-device, instantly, without a PC.

---

## The Problem It Solves

Every Android developer occasionally needs to run a shell command on their phone. The existing options are painful:

- **Termux** — powerful but massive overkill. Downloads hundreds of megabytes of Linux userland just so you can run `pm list packages`.
- **Connect to a PC** — defeats the point of doing something quickly on the device itself.
- **ADB over Wireless** — requires Wireless Debugging to be on, only works on Android 11+, and the ADB handshake is slow.

**Untethered** solves all three. It is a focused, no-setup terminal that starts instantly, uses Shizuku for real shell-user privileges, and runs entirely on the device itself.

---

## Screenshots

> _Add your screenshots here._

---

## Features

- **Real shell access** via Shizuku — commands run as `shell` user, not your unprivileged app user
- **Live streaming output** — stdout and stderr stream line by line in real time
- **ANSI color rendering** — terminal colors, bold, italic, and underline rendered natively in Compose via `AnnotatedString`
- **Persistent command history** — stored in Room, swipe left to delete individual items
- **Saved snippets** — bookmark frequently used commands with optional labels
- **Ctrl+C** — kill any running process instantly
- **Session history navigation** — up/down arrows to cycle through previous commands
- **Clear session** — wipe output and start fresh without restarting the app
- **Shizuku status banner** — clear feedback when Shizuku is not running or permission has not been granted

---

## Requirements

| Requirement | Details |
|---|---|
| Android version | 8.0 (API 26) and above |
| [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) | Must be installed and running |
| Developer Options | Must be enabled to start Shizuku |
| Root | **Not required** |

> Untethered does not require root. Shizuku works via wireless ADB or root, but does not mandate root itself.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (Domain / Data / Presentation) |
| Dependency Injection | Hilt |
| Async | Kotlin Coroutines + Flow |
| Database | Room |
| Shell access | Shizuku API 13 |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |

---

## Architecture

The project follows strict Clean Architecture with three layers and one-directional dependencies:

```
Presentation  →  Domain  ←  Data
```

**Domain** is pure Kotlin with zero Android dependencies. It contains models, repository interfaces, and use cases. Every use case is a single callable class with one responsibility. This layer is fully unit testable without mocking Android.

**Data** implements the domain contracts. It contains the Room database, DAOs, entity mappers, the Shizuku shell executor, and the ANSI parser.

**Presentation** contains Jetpack Compose screens and Hilt ViewModels. It observes `StateFlow` from ViewModels and never touches repositories directly — only use cases.

### Package Structure

```
com.untethered.app
├── data
│   ├── local
│   │   ├── dao/                    # Room DAOs
│   │   ├── entity/                 # Room entities
│   │   ├── mapper/                 # Entity ↔ Domain model mappers
│   │   └── UntetheredDatabase.kt
│   ├── process
│   │   ├── AnsiParser.kt           # ANSI escape codes → AnnotatedString
│   │   └── ShizukuShellExecutor.kt # Process spawning + stream management
│   └── repository/                 # Repository implementations
├── di
│   ├── DatabaseModule.kt           # Hilt: Room + DAO providers
│   ├── RepositoryModule.kt         # Hilt: interface → impl bindings
│   └── ShizukuHelper.kt            # Shizuku state + permission helper
├── domain
│   ├── model/                      # Pure Kotlin data models
│   ├── repository/                 # Repository interfaces
│   └── usecase/                    # One class per use case
├── presentation
│   ├── drawer
│   │   ├── DrawerUiState.kt
│   │   ├── DrawerViewModel.kt
│   │   └── TerminalDrawer.kt
│   ├── terminal
│   │   ├── components
│   │   │   ├── SaveSnippetDialog.kt
│   │   │   ├── ShizukuBanner.kt
│   │   │   ├── TerminalInputBar.kt
│   │   │   ├── TerminalLineItem.kt
│   │   │   └── TerminalWelcome.kt
│   │   ├── TerminalScreen.kt
│   │   ├── TerminalUiState.kt
│   │   └── TerminalViewModel.kt
│   └── theme/
├── MainActivity.kt
└── UntetheredApp.kt
```

---

## How It Works

### Shell Execution

Commands are executed via `Shizuku.newProcess()` which spawns a `sh -c <command>` process running under the `shell` user. The process's stdout and stderr are consumed concurrently by two coroutines inside a `callbackFlow`. When both streams drain and the process exits, the flow emits a final `CommandResult.Exit` and closes.

The entire process lifecycle — start, stream stdout, stream stderr, write to stdin, kill, and cleanup on cancellation — is managed as a single coroutine-aware system using `coroutineScope` with a `finally` block that guarantees cleanup regardless of how the flow terminates.

### ANSI Parsing

Raw terminal output frequently contains ANSI SGR escape sequences. For example `\u001B[1;32m` means bold green and `\u001B[0m` resets all styling. A naive text field renders these as literal characters, producing unreadable garbage.

`AnsiParser` uses a regex to walk through raw strings, maintaining a style state object that accumulates as SGR codes are applied. Each plain-text segment between escape sequences becomes an `AnnotatedString` span with a `SpanStyle` derived from the current style state. The result is a fully styled `AnnotatedString` that Compose renders natively — no third-party library required.

**Supported SGR codes:**
- Reset (0)
- Bold (1), Italic (3), Underline (4)
- Bold off (22), Italic off (23), Underline off (24)
- Standard foreground colors (30–37)
- Bright foreground colors (90–97)
- Standard background colors (40–47)
- Default foreground (39), Default background (49)

### State Management

`TerminalViewModel` owns the terminal session as a single `TerminalUiState` exposed via `StateFlow`. All mutations use `_uiState.update {}` to prevent race conditions when stdout and stderr stream simultaneously. Output is capped at 2000 lines to prevent unbounded memory growth during long-running commands like `logcat`.

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/untethered.git
cd untethered
```

### 2. Open in Android Studio

Android Studio Hedgehog (2023.1.1) or later is recommended.

### 3. Verify Shizuku dependencies

Your app-level `build.gradle.kts` must include:

```kotlin
implementation("dev.rikka.shizuku:api:13.1.5")
implementation("dev.rikka.shizuku:provider:13.1.5")
```

> ⚠️ The old `rikka.shizuku:shizuku-api` artifact is **not** compatible. Use the `dev.rikka.shizuku` namespace shown above. The `newProcess()` method is only public in API 13+.

Your `settings.gradle.kts` must include:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

### 4. Set up Shizuku on your device

1. Install [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) from the Play Store
2. Enable Developer Options on your device
3. Follow Shizuku's in-app instructions to start the service via wireless ADB or root
4. Keep Shizuku running in the background

### 5. Build and run

Connect your physical device, build the project, and launch Untethered. Grant the Shizuku permission prompt when it appears on first launch.

---

## Comparison with Similar Apps

| | Untethered | LADB | Termux |
|---|---|---|---|
| Shell method | Shizuku | Wireless ADB (self-pairing) | Linux userland |
| Min Android version | 8.0 | 11.0 | 7.0 |
| Requires Wireless Debugging | No | Yes, always | No |
| Startup speed | Instant | Slow (ADB handshake) | Instant |
| ANSI color rendering | Full | Partial | Full |
| Saved command snippets | Yes | No | No |
| Persistent history | Yes | Yes | Yes |
| Package manager | No | No | Yes |
| Python / git / SSH | No | No | Yes |
| Built with Jetpack Compose | Yes | No | No |

Untethered is not a Termux replacement. It is focused exclusively on Android shell commands and optimised for developers who want instant access without a Linux environment, a PC, or Wireless Debugging enabled.

---

## Known Limitations

- **No PTY support** — interactive commands that redraw the screen (`top`, `vi`, `nano`) will not render correctly. A pseudo-terminal is required for these and is not yet implemented.
- **Shizuku required for meaningful use** — without Shizuku running and permission granted, commands execute as the unprivileged app user. Most interesting Android shell commands will fail with `Permission denied`.
- **Not a Linux environment** — you cannot install Python packages, run git, or use any native tooling. Those use cases require Termux.

---

## Roadmap

- [ ] PTY support for interactive commands (`top`, `htop`, `vi`)
- [ ] Session persistence — restore the last session on relaunch
- [ ] Font size preference stored in DataStore
- [ ] Export session output as a `.txt` file
- [ ] Wireless ADB fallback when Shizuku is unavailable
- [ ] Tablet and landscape layout optimisation

---

## Contributing

Pull requests are welcome. For significant changes please open an issue first to discuss your proposal.

```bash
# 1. Fork and clone
git clone https://github.com/yourusername/untethered.git

# 2. Create a feature branch
git checkout -b feature/your-feature-name

# 3. Commit your changes
git commit -m "Add your feature"

# 4. Push and open a pull request
git push origin feature/your-feature-name
```

Please follow the existing Clean Architecture structure. New features should have a use case in the domain layer, an implementation in the data layer, and be exposed to the UI only through a ViewModel.

---

## License

```
MIT License

Copyright (c) 2025 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Acknowledgements

- [Shizuku](https://github.com/RikkaApps/Shizuku) by RikkaW — the foundation that makes privileged shell access possible without root
- [Jetpack Compose](https://developer.android.com/compose) — Android's modern declarative UI toolkit
- [Hilt](https://dagger.dev/hilt/) — dependency injection for Android
- [Room](https://developer.android.com/training/data-storage/room) — local persistence for command history and snippets
