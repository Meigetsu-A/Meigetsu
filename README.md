# Meigetsu

Meigetsu is a high-performance, automated media scraping and consumption application for Android. Built with a focus on speed, efficiency, and a premium "AniLab" aesthetic, Meigetsu utilizes advanced automation to provide seamless access to anime and manga from hundreds of sources.

## ⚖️ LEGAL DISCLAIMER

**IMPORTANT: FOR EDUCATIONAL PURPOSES ONLY**

*   **Meigetsu is a tool for automated web scraping.**
*   **The developer does NOT host, store, or provide any copyrighted media content.**
*   The application functions as a specialized web browser that automates the process of finding and viewing publicly available content on the internet.
*   Users are solely responsible for how they use the tool and the content they choose to access.
*   The developer is not liable for any misuse of the application or for any third-party content.

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
*   **Scraping Automation Engine:** Built-in support for 500+ sources with dynamic JSON-based definitions.
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
