package com.meigetsu.core.common

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Dispatcher(val meigetsuDispatcher: MeigetsuDispatchers)

enum class MeigetsuDispatchers {
    Default,
    Main,
    IO,
}

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(MeigetsuDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(MeigetsuDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(MeigetsuDispatchers.Main)
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
