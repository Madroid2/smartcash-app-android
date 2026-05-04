package com.smartcash.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcash.app.data.model.Portal
import com.smartcash.app.data.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val earningsToday: Double,
        val streakDays: Int,
        val totalWithdrawn: Double,
        val featuredOffers: List<Portal>,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val portalRepository: PortalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            delay(1500) // Simulate loading delay
            try {
                portalRepository.getPortals().collect { allPortals ->
                    val featuredOffers = allPortals.shuffled().take(3)
                    _uiState.value = HomeUiState.Success(
                        earningsToday = 3.75,
                        streakDays = 12,
                        totalWithdrawn = 45.50,
                        featuredOffers = featuredOffers,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
