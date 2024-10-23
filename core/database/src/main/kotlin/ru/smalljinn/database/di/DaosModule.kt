package ru.smalljinn.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.database.KolumbusDatabase
import ru.smalljinn.database.dao.ImageDao
import ru.smalljinn.database.dao.PlaceDao
import ru.smalljinn.database.dao.SearchPlaceDao

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

    @Provides
    fun provideSearchPlacesDao(
        database: KolumbusDatabase
    ): SearchPlaceDao = database.searchPlacesDao()
}