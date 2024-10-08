package ru.smalljinn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.smalljinn.model.data.Place

@Composable
fun PlaceCard(
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    isSelected: Boolean = false,
    place: Place,
    modifier: Modifier = Modifier
) {
    val headerImageUrl = remember {
        with(place) {
            //TODO maybe save url instead of id of header image?
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
        Box {
            Column {
                PlaceHeaderImage(
                    headerImageUrl = headerImageUrl,
                    modifier = Modifier.height(180.dp)
                )
                //Text column
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (place.title.isNotBlank()) {
                            PlaceTitle(place.title)
                            Spacer(Modifier.weight(1f))
                        }
                        FavoriteButton(
                            isFavorite = place.favorite,
                            onClick = onFavorite
                        )
                    }
                    CreationDate(place.creationDate)
                    PlaceDescription(place.description)
                }
            }
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
private fun PlaceTitle(placeTitle: String, modifier: Modifier = Modifier) {
    Text(placeTitle, style = MaterialTheme.typography.headlineSmall, modifier = modifier)
}

@Composable
private fun PlaceDescription(placeDescription: String, modifier: Modifier = Modifier) {
    Text(
        placeDescription,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}