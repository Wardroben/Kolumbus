package ru.smalljinn.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.database.KolumbusDatabase
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.dao.PlaceDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun provideImageDao(
        database: KolumbusDatabase
    ): ImageDao = database.imageDao()

    @Provides
    fun providePlaceDao(
        database: KolumbusDatabase
    ): PlaceDao = database.placeDao()
}