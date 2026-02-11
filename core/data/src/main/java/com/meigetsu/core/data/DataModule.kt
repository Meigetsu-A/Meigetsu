package com.meigetsu.core.data

import com.meigetsu.core.data.repository.LibraryRepositoryImpl
import com.meigetsu.core.data.repository.MediaRepositoryImpl
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.repository.MediaRepository
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
}
