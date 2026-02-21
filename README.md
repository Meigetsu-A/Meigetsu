# Meigetsu - Content Aggregator

Meigetsu is a high-performance, open-source content aggregator for anime, manga, and light novels. Built with **Jetpack Compose**, **Clean Architecture**, and **QuickJS**, it provides a unified interface for accessing media from various third-party extensions.

## ⚠️ LEGAL / DMCA SAFETY (READ CAREFULLY)

*   **Meigetsu does NOT host or store any copyrighted content.**
*   **Meigetsu does NOT provide copyrighted media.**
*   The application works solely as a content aggregator using publicly available APIs and third-party extensions.
*   The extension system loads external source definitions dynamically.
*   **Users are solely responsible for the extensions they install.**
*   The developer is not liable for any content provided by third-party sources.
*   Meigetsu contains no hardcoded piracy sources.

### DMCA Takedown
If you believe that your copyrighted work is being aggregated via a third-party extension and you wish to have the metadata removed from the search index (powered by AniList), please contact:
**Email:** meigetsu.app@gmail.com

## Features
*   **Unified Search:** Search across multiple sources for Anime, Manga, and Novels.
*   **Modern Video Player:** Netflix-style ExoPlayer with gesture controls, subtitle support, and picture-in-picture.
*   **Advanced Manga Reader:** Supports Vertical, Horizontal, and Webtoon modes with smooth animations.
*   **Theme Builder:** Fully customizable primary colors, theme modes, and typography.
*   **Offline Tracking:** Local library persistence using Room database.
*   **Extension System:** Dynamically load scrapers written in JavaScript.

## Architecture
Meigetsu follows **Clean Architecture** principles and is highly modularized:
- `:app`: Main application entry point and navigation.
- `:core:data`: Repository implementations, Room database, and DataStore.
- `:core:domain`: UseCases and repository interfaces.
- `:core:network`: GraphQL (Apollo) and REST (Retrofit/Ktor) clients.
- `:core:ui`: Design system, themes, and shared components.
- `:feature:*`: Feature-specific modules (Home, Search, Player, Reader, etc.).

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Dependency Injection:** Hilt
- **Database:** Room
- **Networking:** Apollo GraphQL, Ktor, Retrofit
- **Async:** Coroutines + Flow
- **Media:** ExoPlayer (Media3), Coil
- **Scraping Engine:** QuickJS Android

## Building
1. Clone the repository.
2. Open in Android Studio Jellyfish or newer.
3. Sync Gradle.
4. Run `./gradlew assembleDebug`.

## License
Licensed under the **MIT License**. See `LICENSE` for more details.

---
**Official Links:**
- Discord: https://discord.gg/JskMdb4cS
- GitHub: https://github.com/Azu-na/Meigetsu-
