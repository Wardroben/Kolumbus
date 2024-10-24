package ru.smalljinn.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.smalljinn.ui.PlacesTabContent

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    onPlaceClicked: (placeId: Long) -> Unit,
) {
    val uiState = viewModel.searchResultUiState.collectAsStateWithLifecycle().value
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        onPlaceClicked = onPlaceClicked,
        onFavoritePlace = viewModel::makeFavoritePlace,
        onBackClick = onBackClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged
    )
}

@Composable
fun SearchScreen(
    uiState: SearchResultUiState,
    searchQuery: String = "",
    onSearchQueryChanged: (String) -> Unit,
    onPlaceClicked: (placeId: Long) -> Unit,
    onFavoritePlace: (Long, Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = onSearchQueryChanged,
            searchQuery = searchQuery
        )
        when (uiState) {
            SearchResultUiState.EmptyQuery -> SupportingSearchText(text = stringResource(R.string.empty_query_hint))

            SearchResultUiState.Loading -> SupportingSearchText(stringResource(R.string.results_loading_label))
            SearchResultUiState.LoadFailed -> SupportingSearchText(stringResource(R.string.error_loading_search_results))

            SearchResultUiState.NothingToLookFor -> SupportingSearchText(text = "Ну и что ты собрался искать, когда у тебя меток кот наплакал?")
            is SearchResultUiState.Success -> {
                if (uiState.isEmpty()) {
                    SupportingSearchText(
                        stringResource(
                            R.string.places_with_content_not_found_label,
                            searchQuery
                        )
                    )
                } else {
                    PlacesTabContent(
                        modifier = Modifier.fillMaxSize(),
                        places = uiState.places,
                        useCompactMode = true,
                        onPlaceClicked = onPlaceClicked,
                        highlightSelectedPlace = false,
                        favoritePlace = { place, favorite -> onFavoritePlace(place.id, favorite) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchToolbar(
    onBackClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        IconButton(onBackClick) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.go_back_action)
            )
        }
        SearchTextField(searchQuery = searchQuery, onSearchQueryChanged = onSearchQueryChanged)
    }
}

@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        value = searchQuery,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_cd))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search_text_cd)
                    )
                }
            }
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester),
        shape = RoundedCornerShape(32.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
        }),
        maxLines = 1,
        singleLine = true
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SupportingSearchText(text: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(24.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )
    }
}