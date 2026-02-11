# Meigetsu

Meigetsu is a production-ready content aggregator application for Android, built with Kotlin, Jetpack Compose, and Clean Architecture.

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

*   **Home:** Smart recommendations, trending anime/manga, and continue watching/reading sections.
*   **Library:** Manage your collection with custom categories and local persistence (Room).
*   **Browse:** Powerful search and filtering using the AniList API.
*   **Video Player:** Netflix-style ExoPlayer with gesture controls, PIP, and subtitle support.
*   **Manga Reader:** Advanced reader with Webtoon, Vertical, and Horizontal modes.
*   **Extension System:** Plugin-based architecture for dynamic source loading.
*   **Settings:** Advanced Theme Builder with instant primary/secondary color customization.

## 🏗️ Architecture

*   **Clean Architecture** (app, core, data, domain, ui, features)
*   **MVVM** Pattern
*   **Hilt** for Dependency Injection
*   **Coroutines + Flow** for asynchronous operations
*   **Apollo GraphQL** for AniList API integration
*   **Room** for local database
*   **Media3/ExoPlayer** for video playback
*   **Coil** for image loading

## 🚀 Getting Started

### How to Build
1.  Clone the repository: `git clone https://github.com/Azu-na/Meigetsu-`
2.  Open in Android Studio (Iguana or newer).
3.  Sync Gradle and run the `:app` module.

### How to Add Extensions
Extensions are dynamically loaded via the `ExtensionManager`. To add a new source:
1.  Implement the `AnimeSource` or `MangaSource` interface.
2.  Register the source in the `ExtensionManager`.
3.  The app will automatically display content from the registered source.

## 🤝 Contact
*   **Discord:** [https://discord.gg/JskMdb4cS](https://discord.gg/JskMdb4cS)
*   **GitHub:** [https://github.com/Azu-na/Meigetsu-](https://github.com/Azu-na/Meigetsu-)
