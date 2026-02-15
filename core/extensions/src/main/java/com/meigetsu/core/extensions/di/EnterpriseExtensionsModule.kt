package com.meigetsu.core.extensions.di
import com.meigetsu.core.extensions.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnterpriseExtensionsModule {
    @Provides @Singleton fun provideEnterpriseExtensions(): Set<Extension> {
        val extensions = mutableSetOf<Extension>()
        for (i in 1..100) {
            try {
                val clazz = Class.forName("com.meigetsu.core.extensions.enterprise.Source_" + i + "Provider")
                extensions.add(clazz.getDeclaredConstructor().newInstance() as Extension)
            } catch (e: Exception) { e.printStackTrace() }
        }
        return extensions
    }
}