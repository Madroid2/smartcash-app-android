package com.smartcash.app.core.util

import com.smartcash.app.core.network.ImpressionApi
import com.smartcash.app.core.network.ImpressionPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImpressionTracker @Inject constructor(
    private val api: ImpressionApi,
    private val applicationScope: CoroutineScope,
) {
    private val channel = Channel<ImpressionPayload>(capacity = Channel.UNLIMITED)

    init {
        applicationScope.launch {
            for (event in channel) {
                sendWithRetry(event)
            }
        }
    }

    fun track(creativeId: String, placementId: String, adFormat: String) {
        val payload = ImpressionPayload(
            creativeId = creativeId,
            placementId = placementId,
            adFormat = adFormat,
            appId = "com.smartcash.app",
            timestamp = Instant.now().toString(),
        )
        applicationScope.launch { channel.send(payload) }
    }

    private suspend fun sendWithRetry(payload: ImpressionPayload, maxRetries: Int = 3) {
        repeat(maxRetries) { attempt ->
            try {
                val response = api.postImpression(payload)
                if (response.isSuccessful) return
            } catch (e: Exception) {
                if (attempt < maxRetries - 1) {
                    delay((1L shl attempt) * 1000L) // exponential backoff: 1s, 2s, 4s
                }
            }
        }
    }
}
