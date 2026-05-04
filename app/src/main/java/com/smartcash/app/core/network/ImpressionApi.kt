package com.smartcash.app.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImpressionApi {
    @POST("api/impressions")
    suspend fun postImpression(@Body event: ImpressionPayload): Response<ImpressionPayload>
}

data class ImpressionPayload(
    val creativeId: String,
    val placementId: String,
    val adFormat: String,
    val appId: String = "com.smartcash.app",
    val timestamp: String,
)
