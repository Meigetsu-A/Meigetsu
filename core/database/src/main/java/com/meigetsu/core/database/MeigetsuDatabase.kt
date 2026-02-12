package com.meigetsu.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.meigetsu.core.database.dao.DownloadDao
import com.meigetsu.core.database.dao.LibraryDao
import com.meigetsu.core.database.entity.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        LibraryEntity::class,
        CategoryEntity::class,
        WatchHistoryEntity::class,
        ReadHistoryEntity::class,
        DownloadEntity::class
    ],
    version = 3
)
abstract class MeigetsuDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
    abstract fun downloadDao(): DownloadDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MeigetsuDatabase {
        return Room.databaseBuilder(
            context,
            MeigetsuDatabase::class.java,
            "meigetsu.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideLibraryDao(db: MeigetsuDatabase): LibraryDao = db.libraryDao()

    @Provides
    fun provideDownloadDao(db: MeigetsuDatabase): DownloadDao = db.downloadDao()
}
