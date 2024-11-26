package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.resource.ResourceManager
import ru.smalljinn.kolumbus.data.providers.AndroidResourceManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ResourceModule {
    @Binds
    @Singleton
    fun bindResourceManager(
        provider: AndroidResourceManager
    ): ResourceManager
}