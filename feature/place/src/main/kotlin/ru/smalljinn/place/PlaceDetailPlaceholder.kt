package ru.smalljinn.place

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaceDetailPlaceholder(modifier: Modifier = Modifier) {
    val roundSize = remember { 24.dp }
    val noRoundSize = remember { 24.dp }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(roundSize, roundSize, noRoundSize, noRoundSize)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                20.dp,
                Alignment.CenterVertically
            )
        ) {
            //TODO change icon
            Icon(Icons.Default.Info, contentDescription = null)
            Text(
                text = "Select an place",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}