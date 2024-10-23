package ru.smalljinn.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.UserSettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
): ViewModel() {
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
}

data class SettingsEditable(
    val useCompactStyle: Boolean
)

sealed interface SettingsUiState {
    data object Loading: SettingsUiState
    data class Success(val settings: SettingsEditable): SettingsUiState
}