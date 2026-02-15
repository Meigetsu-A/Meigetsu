package com.meigetsu.core.data.repository

import com.meigetsu.core.database.dao.CharacterDao
import com.meigetsu.core.database.dao.LibraryDao
import com.meigetsu.core.database.dao.HistoryDao
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.meigetsu.core.database.entity.*
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val libraryDao: LibraryDao,
    private val characterDao: CharacterDao,
    private val historyDao: HistoryDao
) : LibraryRepository {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Library Updates"
            val descriptionText = "Notifications for new episodes or chapters"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("library_updates", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendUpdateNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, "library_updates")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun getLibraryAnime(): Flow<List<Anime>> {
        return libraryDao.getAllLibraryItems().map { entities ->
            entities.filter { it.type == "ANIME" }.map { it.toAnime() }
        }
    }

    override fun getLibraryManga(): Flow<List<Manga>> {
        return libraryDao.getAllLibraryItems().map { entities ->
            entities.filter { it.type == "MANGA" }.map { it.toManga() }
        }
    }

    override suspend fun addToLibrary(anime: Anime) {
        sendUpdateNotification("Added to Library", anime.title)
        libraryDao.insertLibraryItem(
            LibraryEntity(
                id = anime.id,
                title = anime.title,
                coverImage = anime.coverImage,
                type = "ANIME",
                status = anime.status.name,
                progress = 0,
                totalEpisodes = anime.episodes,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    override suspend fun addToLibrary(manga: Manga) {
        libraryDao.insertLibraryItem(
            LibraryEntity(
                id = manga.id,
                title = manga.title,
                coverImage = manga.coverImage,
                type = "MANGA",
                status = manga.status.name,
                progress = 0,
                totalEpisodes = manga.chapters,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeFromLibrary(id: String) {
        libraryDao.deleteLibraryItem(id)
    }

    override fun getFavoriteCharacters(): Flow<List<Character>> {
        return characterDao.getAllFavoriteCharacters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addCharacterToLibrary(character: Character) {
        characterDao.insertCharacter(character.toEntity())
    }

    override suspend fun removeCharacterFromLibrary(id: String) {
        // Implementation
    }

    override fun getWatchHistory(): Flow<List<String>> {
        return historyDao.getWatchHistory().map { list -> list.map { it.mediaId } }
    }

    override suspend fun getWatchHistoryById(mediaId: String): WatchHistory? {
        return historyDao.getWatchHistoryById(mediaId)?.let {
            WatchHistory(it.mediaId, it.episodeNumber, it.position, it.duration, it.lastWatched)
        }
    }

    override fun getReadHistory(): Flow<List<String>> {
        return historyDao.getReadHistory().map { list -> list.map { it.mediaId } }
    }

    override suspend fun getReadHistoryById(mediaId: String): ReadHistory? {
        return historyDao.getReadHistoryById(mediaId)?.let {
            ReadHistory(it.mediaId, it.chapterNumber, it.pageNumber, it.lastRead)
        }
    }

    override suspend fun updateWatchHistory(mediaId: String, episode: Int, position: Long, duration: Long) {
        historyDao.insertWatchHistory(
            WatchHistoryEntity(mediaId, episode, position, duration, System.currentTimeMillis())
        )
    }

    override suspend fun updateReadHistory(mediaId: String, chapter: Double, page: Int) {
        historyDao.insertReadHistory(
            ReadHistoryEntity(mediaId, chapter, page, System.currentTimeMillis())
        )
    }

    private fun LibraryEntity.toAnime() = Anime(
        id = id,
        title = title,
        description = null,
        coverImage = coverImage,
        bannerImage = null,
        rating = null,
        status = MediaStatus.valueOf(status),
        format = MediaFormat.TV,
        episodes = totalEpisodes,
        nextEpisode = null,
        genres = emptyList(),
        averageScore = null,
        popularity = null,
        season = null,
        year = null,
        studio = null
    )

    private fun LibraryEntity.toManga() = Manga(
        id = id,
        title = title,
        description = null,
        coverImage = coverImage,
        bannerImage = null,
        rating = null,
        status = MediaStatus.valueOf(status),
        format = MediaFormat.MANGA,
        chapters = null,
        volumes = null,
        genres = emptyList(),
        averageScore = null,
        popularity = null
    )

    private fun CharacterEntity.toDomain() = Character(
        id = id,
        name = name,
        image = image,
        description = description
    )

    private fun Character.toEntity() = CharacterEntity(
        id = id,
        name = name,
        image = image,
        description = description
    )
}
