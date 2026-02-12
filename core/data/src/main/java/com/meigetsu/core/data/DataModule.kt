package com.meigetsu.core.data

import com.meigetsu.core.data.repository.DownloadRepositoryImpl
import com.meigetsu.core.data.repository.LibraryRepositoryImpl
import com.meigetsu.core.data.repository.MediaRepositoryImpl
import com.meigetsu.core.data.repository.PreferenceRepositoryImpl
import com.meigetsu.core.domain.repository.DownloadRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.PreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindLibraryRepository(
        libraryRepositoryImpl: LibraryRepositoryImpl
    ): LibraryRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(
        preferenceRepositoryImpl: PreferenceRepositoryImpl
    ): PreferenceRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepositoryImpl: DownloadRepositoryImpl
    ): DownloadRepository
}
