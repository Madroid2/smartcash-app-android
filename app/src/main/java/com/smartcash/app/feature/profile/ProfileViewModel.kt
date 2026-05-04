package com.smartcash.app.feature.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ProfileStats(
    val totalEarned: Double,
    val surveysCompleted: Int,
    val videosWatched: Int,
    val gamesPlayed: Int,
    val referralCode: String,
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _stats = MutableStateFlow(
        ProfileStats(
            totalEarned = 156.75,
            surveysCompleted = 87,
            videosWatched = 243,
            gamesPlayed = 34,
            referralCode = "SMARTCASH2024",
        ),
    )
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(true)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }
}
