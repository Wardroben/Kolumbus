package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.domain.saving.FileNameGenerator
import ru.smalljinn.kolumbus.data.saving.AndroidFileController
import ru.smalljinn.kolumbus.data.saving.AndroidFileNameGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SavingModule {
    @Binds
    @Singleton
    internal abstract fun bindFileController(
        implementation: AndroidFileController
    ): FileController

    @Binds
    @Singleton
    internal abstract fun bindFileNameGenerator(
        implementation: AndroidFileNameGenerator
    ): FileNameGenerator
}