package com.smartcash.app.feature.earn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcash.app.data.model.Portal
import com.smartcash.app.data.model.PortalCategory
import com.smartcash.app.data.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EarnUiState {
    object Loading : EarnUiState()
    data class Success(val portals: List<Portal>) : EarnUiState()
    data class Error(val message: String) : EarnUiState()
}

@HiltViewModel
class EarnViewModel @Inject constructor(
    private val portalRepository: PortalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EarnUiState>(EarnUiState.Loading)
    val uiState: StateFlow<EarnUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow(PortalCategory.SURVEY)
    val selectedCategory: StateFlow<PortalCategory> = _selectedCategory.asStateFlow()

    init {
        loadPortals()
    }

    fun selectCategory(category: PortalCategory) {
        _selectedCategory.value = category
        loadPortals()
    }

    private fun loadPortals() {
        viewModelScope.launch {
            try {
                portalRepository.getPortals(_selectedCategory.value).collect { portals ->
                    _uiState.value = EarnUiState.Success(portals)
                }
            } catch (e: Exception) {
                _uiState.value = EarnUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
