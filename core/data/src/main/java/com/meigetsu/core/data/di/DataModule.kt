package com.meigetsu.core.data.di

import com.meigetsu.core.data.repository.*
import com.meigetsu.core.domain.repository.LibraryRepository
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
    abstract fun bindLibraryRepository(
        libraryRepositoryImpl: LibraryRepositoryImpl
    ): LibraryRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(
        preferenceRepositoryImpl: PreferenceRepositoryImpl
    ): PreferenceRepository
}
