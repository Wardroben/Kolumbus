package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.share.ShareProvider
import ru.smalljinn.kolumbus.data.share.AndroidShareProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShareModule {
    @Binds
    @Singleton
    internal abstract fun bindShareProvider(
        provider: AndroidShareProvider
    ): ShareProvider
}