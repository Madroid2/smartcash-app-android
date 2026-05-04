package com.smartcash.app.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcash.app.data.model.Transaction
import com.smartcash.app.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WalletUiState {
    object Loading : WalletUiState()
    data class Success(
        val balance: Double,
        val transactions: List<Transaction>,
    ) : WalletUiState()
    data class Error(val message: String) : WalletUiState()
}

sealed class WalletEvent {
    object ShowMinimumBalanceError : WalletEvent()
    object WithdrawalSuccess : WalletEvent()
    data class WithdrawalFailure(val message: String) : WalletEvent()
}

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WalletEvent>()
    val events: SharedFlow<WalletEvent> = _events.asSharedFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            try {
                combine(
                    walletRepository.getBalance(),
                    walletRepository.getTransactions(),
                ) { balance, transactions ->
                    WalletUiState.Success(balance, transactions)
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = WalletUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun withdraw(amount: Double) {
        viewModelScope.launch {
            if (amount < 5.0) {
                _events.emit(WalletEvent.ShowMinimumBalanceError)
            } else {
                val result = walletRepository.withdraw(amount)
                if (result.isSuccess) {
                    _showConfetti.value = true
                    _events.emit(WalletEvent.WithdrawalSuccess)
                    loadWalletData()
                } else {
                    _events.emit(WalletEvent.WithdrawalFailure(result.exceptionOrNull()?.message ?: "Withdrawal failed"))
                }
            }
        }
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}
