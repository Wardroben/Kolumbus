package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.kolumbus.data.repository.DefaultSearchPlacesRepository
import ru.smalljinn.kolumbus.data.repository.ImageRepository
import ru.smalljinn.kolumbus.data.repository.OfflineImagesRepository
import ru.smalljinn.kolumbus.data.repository.OfflinePlacesRepository
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.repository.SearchPlacesRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindPlaceRepository(
        placesRepository: OfflinePlacesRepository
    ): PlacesRepository

    @Binds
    internal abstract fun bindPhotoRepository(
        imagesRepository: OfflineImagesRepository
    ): ImageRepository

    @Binds
    internal abstract fun bindSearchPlacesRepository(
        searchPlacesRepository: DefaultSearchPlacesRepository
    ): SearchPlacesRepository
}