# Meigetsu

Meigetsu is a high-performance, automated media discovery and consumption application for Android. Built with a focus on speed, efficiency, and a premium aesthetic, Meigetsu utilizes advanced automation to provide seamless access to anime and manga from public internet sources.

## ⚖️ LEGAL DISCLAIMER

**IMPORTANT: FOR EDUCATIONAL PURPOSES ONLY**

*   **Meigetsu is a content aggregator tool.** It does NOT host, store, or provide any copyrighted media content.
*   The application functions as a specialized browser that automates the process of finding and viewing publicly available content on the third-party websites.
*   Users are solely responsible for the extensions they install and the content they choose to access.
*   The developer of Meigetsu is NOT liable for any third-party content or any misuse of the application.
*   Meigetsu does not provide copyrighted media. All content is provided by third-party sources.

### 🛡️ DMCA Takedown & Copyright
If you believe that your copyrighted work is being linked to or accessed through Meigetsu in a way that constitutes copyright infringement, please contact the third-party source directly. Since Meigetsu does not host content, we cannot remove it from the internet. However, for any concerns regarding the application itself, you may contact:
**Email:** meigetsu.app@gmail.com

### 📜 Terms of Use
By using Meigetsu, you agree that:
1.  You will not use the app for any illegal purposes.
2.  You understand that all media content is provided by external sources.
3.  You use the extension system and scraping features at your own risk.

### 📄 License
This project is licensed under the **MIT License**.

---

## 📱 Features

*   **Home Discovery:** Smart discovery engine with horizontal carousels for Trending, Popular, and Recommended media.
*   **Integrated Search:** Unified search across metadata providers and installed extensions.
*   **Library Management:** Persist your collection with custom sections and status tracking (Watching, Completed, On Hold, etc.).
*   **Schedule Tab:** Keep track of upcoming anime episodes by day of the week.
*   **Advanced Video Player:** Netflix-style player using ExoPlayer with gesture controls, PIP, and custom UI.
*   **Manga Reader:** Multi-mode reader (Webtoon, Vertical, Paged) with smooth transitions.
*   **Theme Builder:** Advanced customization for primary/secondary colors, corner radius, and more.
*   **Security:** Biometric lock and Incognito mode for privacy.

## 🏗️ Architecture

Meigetsu follows **Clean Architecture** principles and is highly modularized:
- `:app`: The main Android entry point.
- `:core`: Shared logic, models, and networking.
- `:feature`: Feature-specific modules (Home, Library, Player, etc.).
- `:extensions`: The dynamic scraping engine and provider interfaces.

## 🚀 Getting Started

### How to Build
1.  Clone the repository.
2.  Open in Android Studio.
3.  Sync Gradle and run the `:app` module.

### How to Add Extensions
Meigetsu loads external source definitions dynamically. Place your JSON source definitions in the `extensions` directory of the app's external storage or bundle them in `assets/sources`.

## 🤝 Contact
*   **Email:** meigetsu.app@gmail.com
*   **Discord:** [https://discord.gg/JskMdb4cS](https://discord.gg/JskMdb4cS)
*   **GitHub:** [https://github.com/Azu-na/Meigetsu-](https://github.com/Azu-na/Meigetsu-)
