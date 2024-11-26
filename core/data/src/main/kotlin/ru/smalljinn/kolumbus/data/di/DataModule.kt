package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.repository.ImageRepository
import ru.smalljinn.domain.repository.ImportExportRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.domain.repository.SearchPlacesRepository
import ru.smalljinn.kolumbus.data.repository.DefaultSearchPlacesRepository
import ru.smalljinn.kolumbus.data.repository.OfflineImagesRepository
import ru.smalljinn.kolumbus.data.repository.OfflineImportExportRepository
import ru.smalljinn.kolumbus.data.repository.OfflinePlacesRepository

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