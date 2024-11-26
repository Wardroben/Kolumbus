package ru.smalljinn.kolumbus.data.di

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.image.ImageCompressor
import ru.smalljinn.domain.image.ImageGetter
import ru.smalljinn.domain.image.ImageScaler
import ru.smalljinn.kolumbus.data.image.AndroidImageCompressor
import ru.smalljinn.kolumbus.data.image.AndroidImageGetter
import ru.smalljinn.kolumbus.data.image.AndroidImageScaler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageModule {
    @Binds
    @Singleton
    internal abstract fun bindImageCompressor(
        compressor: AndroidImageCompressor
    ): ImageCompressor<Bitmap>

    @Binds
    @Singleton
    internal abstract fun bindImageScaler(
        scaler: AndroidImageScaler
    ): ImageScaler<Bitmap>

    @Binds
    @Singleton
    internal abstract fun bindImageGetter(
        getter: AndroidImageGetter
    ): ImageGetter<Bitmap>
}