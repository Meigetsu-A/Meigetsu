package com.meigetsu.core.extensions

import com.apollographql.apollo.ApolloClient
import com.meigetsu.core.extensions.model.AniListProvider
import com.meigetsu.core.extensions.model.MangaDexProvider
import com.meigetsu.core.extensions.model.ConsumetProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExtensionsModule {

    @Provides
    @Singleton
    fun provideAniListProvider(apolloClient: ApolloClient): AniListProvider {
        return AniListProvider(apolloClient)
    }

    @Provides
    @Singleton
    fun provideMangaDexProvider(client: HttpClient): MangaDexProvider {
        return MangaDexProvider(client)
    }

    @Provides
    @Singleton
    fun provideConsumetProvider(client: HttpClient): ConsumetProvider {
        return ConsumetProvider(client)
    }
}
