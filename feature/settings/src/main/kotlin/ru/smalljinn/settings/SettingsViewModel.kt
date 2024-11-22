package ru.smalljinn.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.smalljinn.domain.repository.ImportExportRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.repository.UserSettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val importExportRepository: ImportExportRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    val uiState = userSettingsRepository.settings.map {
        SettingsUiState.Success(
            settings = SettingsEditable(
                useCompactStyle = it.useCompactPlaceCardMode
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        SettingsUiState.Loading
    )

    fun updateCardStyle(useCompact: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setPlaceCardMode(useCompact)
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            importExportRepository.importPlaces(uri)
        }
    }

    fun createBackup(uri: Uri) {
        viewModelScope.launch {
            val allPlacesIds = placesRepository.getPlacesStream().first()
                .map { it.id }
                .toSet()
            importExportRepository.exportPlaces(fileUri = uri, placeIds = allPlacesIds)
        }
    }
}

data class SettingsEditable(
    val useCompactStyle: Boolean
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: SettingsEditable) : SettingsUiState
}