package ru.smalljinn.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.database.KolumbusDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideKolumbusDatabase(
        @ApplicationContext context: Context
    ): KolumbusDatabase = Room.databaseBuilder(
        context = context,
        klass = KolumbusDatabase::class.java,
        name = "kolumbus-database"
    ).build()
}