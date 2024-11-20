package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.kolumbus.data.repository.DefaultSearchPlacesRepository
import ru.smalljinn.kolumbus.data.repository.ImageRepository
import ru.smalljinn.kolumbus.data.repository.ImportExportRepository
import ru.smalljinn.kolumbus.data.repository.OfflineImagesRepository
import ru.smalljinn.kolumbus.data.repository.OfflineImportExportRepository
import ru.smalljinn.kolumbus.data.repository.OfflinePlacesRepository
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.repository.SearchPlacesRepository

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindPlaceRepository(
        placesRepository: OfflinePlacesRepository
    ): PlacesRepository

    @Binds
    fun bindImageRepository(
        implementation: OfflineImagesRepository
    ): ImageRepository

    @Binds
    fun bindSearchPlacesRepository(
        searchPlacesRepository: DefaultSearchPlacesRepository
    ): SearchPlacesRepository

    @Binds
    fun bindImportExportRepository(
        impl: OfflineImportExportRepository
    ): ImportExportRepository
}