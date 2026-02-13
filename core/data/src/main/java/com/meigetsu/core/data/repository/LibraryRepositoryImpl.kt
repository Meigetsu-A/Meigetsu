package com.meigetsu.core.data.repository

import com.meigetsu.core.database.dao.CharacterDao
import com.meigetsu.core.database.dao.LibraryDao
import com.meigetsu.core.database.entity.CharacterEntity
import com.meigetsu.core.database.entity.LibraryEntity
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    private val characterDao: CharacterDao
) : LibraryRepository {

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
        // characterDao.deleteCharacter(...) - need to fetch first or add a deleteById
    }

    override fun getWatchHistory(): Flow<List<String>> {
        return libraryDao.getWatchHistory().map { list -> list.map { it.mediaId } }
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
