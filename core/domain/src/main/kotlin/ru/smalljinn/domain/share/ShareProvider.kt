package ru.smalljinn.domain.share

import ru.smalljinn.model.data.Place

interface ShareProvider {
    suspend fun sharePlace(place: Place)
}