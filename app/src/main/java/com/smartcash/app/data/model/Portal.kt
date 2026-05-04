package com.smartcash.app.data.model

data class Portal(
    val id: String,
    val name: String,
    val url: String,
    val category: PortalCategory,
    val estimatedReward: String,
    val estimatedTime: String,
    val iconEmoji: String,
    val description: String,
)

enum class PortalCategory { SURVEY, VIDEO, GAME }
