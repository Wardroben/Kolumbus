package ru.smalljinn.kolumbus.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import ru.smalljinn.places.navigation.PlacesRoute
import kotlin.reflect.KClass
import ru.smalljinn.places.R as placesR

enum class TopLevelDestination(
    @StringRes val titleTextId: Int,
    val navigationIcon: ImageVector,
    @StringRes val navigationContentDescriptionId: Int,
    val route: KClass<*>
) {
    PLACES(
        titleTextId = placesR.string.feature_places_title,
        route = PlacesRoute::class,
        navigationIcon = Icons.Default.Search,
        navigationContentDescriptionId = placesR.string.feature_places_navigation_content_description,
    )
}