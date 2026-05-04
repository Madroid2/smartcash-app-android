package com.smartcash.app.data.repository

import com.smartcash.app.data.model.Transaction
import com.smartcash.app.data.model.TransactionStatus
import com.smartcash.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor() {

    private val balanceState = MutableStateFlow(12.50)

    fun getBalance(): Flow<Double> = balanceState.asStateFlow()

    fun getTransactions(): Flow<List<Transaction>> = flow {
        val now = Instant.now()
        val transactions = listOf(
            Transaction(
                id = "txn-1",
                type = TransactionType.SURVEY,
                amount = 1.50,
                description = "Completed Swagbucks Survey",
                timestamp = now.minus(2, ChronoUnit.HOURS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-2",
                type = TransactionType.VIDEO,
                amount = 0.10,
                description = "Watched InboxDollars Video",
                timestamp = now.minus(4, ChronoUnit.HOURS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-3",
                type = TransactionType.GAME,
                amount = 0.75,
                description = "Played Mistplay Game",
                timestamp = now.minus(1, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-4",
                type = TransactionType.BONUS,
                amount = 2.00,
                description = "Sign-up Bonus",
                timestamp = now.minus(2, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-5",
                type = TransactionType.SURVEY,
                amount = 2.25,
                description = "Completed Survey Junkie Survey",
                timestamp = now.minus(3, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-6",
                type = TransactionType.VIDEO,
                amount = 0.05,
                description = "Watched Swagbucks TV Video",
                timestamp = now.minus(4, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-7",
                type = TransactionType.REFERRAL,
                amount = 1.00,
                description = "Friend Referral Bonus",
                timestamp = now.minus(5, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-8",
                type = TransactionType.GAME,
                amount = 1.50,
                description = "Played Swagbucks Games",
                timestamp = now.minus(6, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-9",
                type = TransactionType.SURVEY,
                amount = 0.50,
                description = "Completed Toluna Survey",
                timestamp = now.minus(7, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
            Transaction(
                id = "txn-10",
                type = TransactionType.WITHDRAWAL,
                amount = -5.00,
                description = "PayPal Withdrawal",
                timestamp = now.minus(10, ChronoUnit.DAYS),
                status = TransactionStatus.COMPLETED,
            ),
        )
        emit(transactions)
    }

    suspend fun withdraw(amount: Double): Result<Unit> {
        return try {
            val currentBalance = balanceState.value
            if (currentBalance < amount) {
                Result.failure(Exception("Insufficient balance"))
            } else if (amount < 5.0) {
                Result.failure(Exception("Minimum withdrawal amount is $5.00"))
            } else {
                // Simulate PayPal withdrawal
                balanceState.value = currentBalance - amount
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
