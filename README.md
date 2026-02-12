# Meigetsu

Meigetsu is an advanced, production-ready content aggregator for Android, built with Kotlin, Jetpack Compose, and Clean Architecture. Inspired by Mihon and Cloudstream, it offers a sophisticated plugin-based extension system and a premium media consumption experience.

## ⚖️ LEGAL / DMCA SAFETY REQUIREMENTS

**IMPORTANT: READ CAREFULLY**

*   **Meigetsu does NOT host or store any copyrighted content.**
*   **Meigetsu does NOT provide copyrighted media.**
*   All content is aggregated from publicly available third-party APIs and extensions.
*   Users are solely responsible for the extensions they install and the content they access.
*   The developer of Meigetsu is not liable for any third-party content accessed through the app.
*   Meigetsu functions as a browser-like tool for media metadata and stream extraction from external sources.

### 🛡️ DMCA Takedown
If you believe that your copyrighted work is being linked to or accessed through Meigetsu in a way that constitutes copyright infringement, please contact the third-party source directly. Since Meigetsu does not host content, we cannot remove it from the internet. However, for any concerns regarding the application itself, you may contact:
**Email:** meigetsu.app@gmail.com

### 📜 Terms of Use
By using Meigetsu, you agree that:
1.  You will not use the app for any illegal purposes.
2.  You understand that all media content is provided by external sources.
3.  You use the extension system at your own risk.

### 📄 License
This project is licensed under the **MIT License**.

---

## 📱 Features

*   **Home:** Smart recommendation engine, vertical/horizontal carousels, and "Continue Watching/Reading" with persistent progress tracking.
*   **Library:** Advanced collection management with custom categories, drag-and-drop reordering, and Room persistence.
*   **Browse & Global Search:** Unified search across all installed providers and powerful AniList-powered metadata browsing.
*   **Video Player (Netflix-Style):** Custom ExoPlayer implementation with volume/brightness gestures, double-tap seek, PIP, and advanced playback controls.
*   **Manga Reader:** Multi-mode reader (Webtoon, Vertical, Paged Horizontal) with smooth animations and pinch-to-zoom.
*   **Plugin System (Mihon/Cloudstream Style):** Dynamic extension loading with sophisticated metadata, lang support, and auto-update capabilities.
*   **Advanced Settings:**
    *   **Theme Builder:** Real-time primary/secondary color customization and corner radius adjustments.
    *   **Incognito Mode:** Browse without history or progress saving.
    *   **Detailed Statistics:** Track your watch time and reading history.
    *   **Security:** Parental controls and NSFW toggles.
    *   **Data Management:** JSON-based backup and restore functionality.

## 🏗️ Architecture

*   **Clean Architecture & Modularization**
*   **MVVM** with StateFlow
*   **Hilt** for DI
*   **Apollo GraphQL** (AniList)
*   **Ktor & Retrofit** (Networking)
*   **Room & DataStore** (Persistence)
*   **Media3/ExoPlayer** (Playback)

## 🚀 Getting Started

### How to Build
1.  Clone the repository: `git clone https://github.com/Azu-na/Meigetsu-`
2.  Open in Android Studio (Iguana or newer).
3.  Sync Gradle and run the `:app` module.

### How to Add Extensions
Meigetsu supports dynamic APK and JSON-based extensions. Register your provider in the `ExtensionManager` or host a repository for remote fetching.

## 🤝 Contact
*   **Discord:** [https://discord.gg/JskMdb4cS](https://discord.gg/JskMdb4cS)
*   **GitHub:** [https://github.com/Azu-na/Meigetsu-](https://github.com/Azu-na/Meigetsu-)
