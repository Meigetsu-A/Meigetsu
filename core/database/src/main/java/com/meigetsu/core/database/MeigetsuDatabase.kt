package com.meigetsu.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.meigetsu.core.database.dao.LibraryDao
import com.meigetsu.core.database.entity.CategoryEntity
import com.meigetsu.core.database.entity.LibraryEntity
import com.meigetsu.core.database.entity.WatchHistoryEntity
import com.meigetsu.core.database.entity.ReadHistoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [LibraryEntity::class, CategoryEntity::class, WatchHistoryEntity::class, ReadHistoryEntity::class], version = 2)
abstract class MeigetsuDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
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
}
