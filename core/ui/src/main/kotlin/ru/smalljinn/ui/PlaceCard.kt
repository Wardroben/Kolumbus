package ru.smalljinn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.datetime.Clock
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position

@Composable
fun PlaceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    isSelected: Boolean = false,
    compactStyle: Boolean,
    place: Place
) {
    val headerImageUrl = remember(place.images) {
        with(place) {
            images.find { image -> image.id == headerImageId }?.url ?: images.firstOrNull()?.url
        }
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        modifier = modifier
    ) {
        if (compactStyle) {
            CompactPlaceCard(
                headerImageUrl = headerImageUrl,
                place = place,
                onFavorite = onFavorite
            )
        } else {
            FullPlaceCard(headerImageUrl = headerImageUrl, place = place, onFavorite = onFavorite)
        }
    }
}

@Composable
private fun CompactPlaceCard(
    headerImageUrl: String?,
    place: Place,
    onFavorite: () -> Unit
) {
    val maxCardHeight = 128.dp
    Row(Modifier.height(maxCardHeight), verticalAlignment = Alignment.CenterVertically) {
        PlaceHeaderImage(headerImageUrl = headerImageUrl, Modifier.size(maxCardHeight))
        PlaceTitleWithDescription(place = place, compact = true, onFavorite = onFavorite)
    }
}

@Composable
private fun FullPlaceCard(
    headerImageUrl: String?,
    place: Place,
    onFavorite: () -> Unit
) {
    Column {
        PlaceHeaderImage(
            headerImageUrl = headerImageUrl,
            modifier = Modifier.height(180.dp)
        )
        //Text column
        PlaceTitleWithDescription(place = place, compact = false, onFavorite = onFavorite)
    }
}

@Composable
private fun PlaceTitleWithDescription(place: Place, compact: Boolean, onFavorite: () -> Unit) {
    val contentPadding = remember(compact) { if (compact) 8.dp else 16.dp }
    val verticalArrangement = remember(compact) { if (compact) 2.dp else 8.dp }
    Column(
        modifier = Modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalArrangement)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,) {
            PlaceTitle(
                placeTitle = place.title,
                compact = compact,
                modifier = Modifier.fillMaxWidth(.8f)
            )
            Spacer(Modifier.weight(1f))
            FavoriteButton(
                isFavorite = place.favorite,
                onClick = onFavorite
            )
        }
        CreationDate(place.creationDate)
        if (place.description.isNotBlank()) {
            PlaceDescription(
                placeDescription = place.description,
                compact = compact
            )
        }
    }
}

@Composable
private fun PlaceHeaderImage(headerImageUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(headerImageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier
    ) {
        if (isFavorite) Icon(Icons.Default.Favorite, contentDescription = "Favorite")
        else Icon(Icons.Default.FavoriteBorder, contentDescription = "Unfavorite")
    }
}

@Composable
private fun PlaceTitle(placeTitle: String, compact: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = placeTitle,
        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
        modifier = modifier,
        maxLines = if (compact) 2 else 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PlaceDescription(
    placeDescription: String,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        placeDescription,
        style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        maxLines = if (compact) 2 else 4,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true)
@Composable
private fun CompactPlaceCardPreview() {
    CompactPlaceCard(
        null, place = Place(
            id = 1,
            title = "I love panckakes and what?",
            description = "This is beautiful day to eat some carrots and pumpkins! 😇\n" +
                    "I very glad to see you 💝",
            position = Position.initialPosition(),
            creationDate = Clock.System.now(),
            headerImageId = null,
            favorite = true,
            images = emptyList()
        )
    ) { }
}

@Preview(showBackground = true)
@Composable
private fun FullPlaceCardPreview() {
    FullPlaceCard(null, place = Place(
        id = 1,
        title = "I love panckakes and what?",
        description = "This is beautiful day to eat some carrots and pumpkins! 😇\n" +
                "I very glad to see you 💝",
        position = Position.initialPosition(),
        creationDate = Clock.System.now(),
        headerImageId = null,
        favorite = true,
        images = emptyList()
    )) { }
}