package ru.smalljinn.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.repository.SearchPlacesRepository
import ru.smalljinn.model.data.Place
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchPlacesRepository: SearchPlacesRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchPlacesRepository.getCount()
            .flatMapLatest { totalCount ->
                if (totalCount <= SEARCH_MIN_ENTITY_COUNT) {
                    flowOf<SearchResultUiState>(SearchResultUiState.NothingToLookFor)
                } else {
                    searchQuery.flatMapLatest { query ->
                        if (query.length < SEARCH_MIN_QUERY_LENGTH) {
                            flowOf<SearchResultUiState>(SearchResultUiState.EmptyQuery)
                        } else {
                            searchPlacesRepository.searchPlaces("%$query%")
                                .map<List<Place>,SearchResultUiState> { places ->
                                    SearchResultUiState.Success(places)
                                }
                                .catch { emit(SearchResultUiState.LoadFailed) }
                        }
                    }
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                SearchResultUiState.Loading
            )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun makeFavoritePlace(placeId: Long,favorite: Boolean) {
        viewModelScope.launch {
            placesRepository.makePlaceFavorite(placeId, favorite)
        }
    }
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_MIN_ENTITY_COUNT = 1
private const val SEARCH_MIN_QUERY_LENGTH = 2

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState
    data object EmptyQuery : SearchResultUiState
    data class Success(val places: List<Place>) : SearchResultUiState {
        fun isEmpty() = places.isEmpty()
    }

    data object NothingToLookFor : SearchResultUiState
    data object LoadFailed: SearchResultUiState
}