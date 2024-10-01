package ru.smalljinn.core.photo_store.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.core.photo_store.PhotoManager
import ru.smalljinn.core.photo_store.PhotoManagerImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoManagerModule {
    @Binds
    abstract fun bindPhotoManager(
        photoManager: PhotoManagerImpl
    ): PhotoManager
}