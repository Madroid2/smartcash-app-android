package com.smartcash.app.data.model

import java.time.Instant

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val timestamp: Instant,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
)

enum class TransactionType { SURVEY, VIDEO, GAME, BONUS, WITHDRAWAL, REFERRAL }
enum class TransactionStatus { COMPLETED, PENDING, FAILED }
