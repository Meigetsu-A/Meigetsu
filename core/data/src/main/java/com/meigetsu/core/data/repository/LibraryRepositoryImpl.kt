package com.meigetsu.core.data.repository

import com.meigetsu.core.database.dao.*
import com.meigetsu.core.database.entity.*
import com.meigetsu.core.model.*
import com.meigetsu.core.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    private val historyDao: HistoryDao,
    private val downloadDao: DownloadDao
) : LibraryRepository {
    override fun getLibrary(): Flow<List<Media>> {
        return libraryDao.getAll().map { entities ->
            entities.map { it.toMedia() }
        }
    }

    override suspend fun addToLibrary(media: Media) {
        libraryDao.insert(media.toEntity())
    }

    override suspend fun removeFromLibrary(id: String) {
        libraryDao.delete(id)
    }

    override fun getHistory(): Flow<List<History>> {
        return historyDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateHistory(mediaId: String, itemNumber: Double, position: Long) {
        historyDao.insert(HistoryEntity(mediaId, itemNumber, position, System.currentTimeMillis()))
    }

    override suspend fun getHistoryForMedia(mediaId: String): History? {
        return historyDao.getById(mediaId)?.toDomain()
    }
}

private fun LibraryEntity.toMedia() = Media(
    id = id,
    title = title,
    description = null,
    coverImage = coverImage,
    bannerImage = null,
    type = MediaType.valueOf(type),
    status = MediaStatus.valueOf(status),
    score = null,
    genres = emptyList(),
    year = null
)

private fun Media.toEntity() = LibraryEntity(
    id = id,
    title = title,
    coverImage = coverImage,
    type = type.name,
    status = status.name,
    lastUpdated = System.currentTimeMillis()
)

private fun HistoryEntity.toDomain() = History(
    mediaId = mediaId,
    lastItemNumber = lastItemNumber,
    position = position,
    timestamp = timestamp
)
