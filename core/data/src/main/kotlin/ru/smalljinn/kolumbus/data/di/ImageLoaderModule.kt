package ru.smalljinn.kolumbus.data.di

import android.content.Context
import coil.ImageLoader
import coil.imageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ImageLoaderModule {

    @Provides
    internal fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = context.imageLoader.newBuilder()
        .allowHardware(false)
        .build()
}