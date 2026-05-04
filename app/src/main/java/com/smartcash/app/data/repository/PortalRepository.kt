package com.smartcash.app.data.repository

import com.smartcash.app.data.model.Portal
import com.smartcash.app.data.model.PortalCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepository @Inject constructor() {

    fun getPortals(category: PortalCategory? = null): Flow<List<Portal>> = flow {
        val allPortals = listOf(
            // Surveys
            Portal(
                id = "survey-1",
                name = "Swagbucks",
                url = "https://www.swagbucks.com",
                category = PortalCategory.SURVEY,
                estimatedReward = "$0.50 - $3.00",
                estimatedTime = "5-15 min",
                iconEmoji = "📋",
                description = "Complete surveys and earn points",
            ),
            Portal(
                id = "survey-2",
                name = "Survey Junkie",
                url = "https://www.surveyjunkie.com",
                category = PortalCategory.SURVEY,
                estimatedReward = "$0.75 - $3.50",
                estimatedTime = "10-20 min",
                iconEmoji = "📝",
                description = "Share your opinions for cash",
            ),
            Portal(
                id = "survey-3",
                name = "InboxDollars",
                url = "https://www.inboxdollars.com",
                category = PortalCategory.SURVEY,
                estimatedReward = "$1.00 - $5.00",
                estimatedTime = "5-30 min",
                iconEmoji = "💰",
                description = "Get paid to take surveys",
            ),
            Portal(
                id = "survey-4",
                name = "Toluna",
                url = "https://www.toluna.com",
                category = PortalCategory.SURVEY,
                estimatedReward = "$0.50 - $2.00",
                estimatedTime = "3-10 min",
                iconEmoji = "🗣️",
                description = "Voice your opinion, earn rewards",
            ),
            Portal(
                id = "survey-5",
                name = "PrizeRebel",
                url = "https://www.prizerebel.com",
                category = PortalCategory.SURVEY,
                estimatedReward = "$0.50 - $1.50",
                estimatedTime = "5-15 min",
                iconEmoji = "🏆",
                description = "Answer surveys, win prizes",
            ),
            // Videos
            Portal(
                id = "video-1",
                name = "Swagbucks TV",
                url = "https://www.swagbucks.com/watch",
                category = PortalCategory.VIDEO,
                estimatedReward = "$0.01 - $0.10",
                estimatedTime = "30 sec - 2 min",
                iconEmoji = "📺",
                description = "Watch short videos and earn",
            ),
            Portal(
                id = "video-2",
                name = "InboxDollars Videos",
                url = "https://www.inboxdollars.com/videos",
                category = PortalCategory.VIDEO,
                estimatedReward = "$0.02 - $0.15",
                estimatedTime = "1-3 min",
                iconEmoji = "🎬",
                description = "Earn by watching videos",
            ),
            Portal(
                id = "video-3",
                name = "MyPoints",
                url = "https://www.mypoints.com",
                category = PortalCategory.VIDEO,
                estimatedReward = "$0.01 - $0.20",
                estimatedTime = "30 sec - 5 min",
                iconEmoji = "⭐",
                description = "Watch videos for points",
            ),
            // Games
            Portal(
                id = "game-1",
                name = "Swagbucks Games",
                url = "https://www.swagbucks.com/games",
                category = PortalCategory.GAME,
                estimatedReward = "$0.10 - $1.00",
                estimatedTime = "5-15 min",
                iconEmoji = "🎮",
                description = "Play games and earn rewards",
            ),
            Portal(
                id = "game-2",
                name = "InboxDollars Games",
                url = "https://www.inboxdollars.com/games",
                category = PortalCategory.GAME,
                estimatedReward = "$0.15 - $2.00",
                estimatedTime = "10-20 min",
                iconEmoji = "🕹️",
                description = "Play fun games for cash",
            ),
            Portal(
                id = "game-3",
                name = "Mistplay",
                url = "https://www.mistplay.com",
                category = PortalCategory.GAME,
                estimatedReward = "$0.20 - $3.00",
                estimatedTime = "15-30 min",
                iconEmoji = "🎯",
                description = "Play mobile games, get paid",
            ),
        )

        val filtered = if (category != null) {
            allPortals.filter { it.category == category }
        } else {
            allPortals
        }

        emit(filtered)
    }
}
