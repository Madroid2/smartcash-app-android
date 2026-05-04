package com.smartcash.app

import android.app.Application
import com.apexads.sdk.ApexAds
import com.apexads.sdk.ApexAdsConfig
import com.apexads.sdk.appopen.AppOpenAd
import com.apexads.sdk.core.di.ServiceLocator
import com.apexads.sdk.core.error.AdError
import com.apexads.sdk.core.network.AdNetworkClient
import com.apexads.sdk.core.network.MockAdExchange
import com.apexads.sdk.core.utils.AdLog
import com.apexads.sdk.wallet.WalletAdExtension
import com.smartcash.app.core.util.ImpressionTracker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class SmartCashApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Inject
    lateinit var impressionTracker: ImpressionTracker

    override fun onCreate() {
        super.onCreate()
        initAdSdk()
    }

    private fun initAdSdk() {
        val config = ApexAdsConfig.Builder(APP_TOKEN)
            .debugLogging(true)
            .testMode(true)
            .cacheTtlSeconds(120)
            .build()

        ApexAds.init(this, config)

        ServiceLocator.register(AdNetworkClient::class.java, MockAdExchange())

        WalletAdExtension.install()

        AppOpenAd.initialize(this, PLACEMENT_APP_OPEN, object : AppOpenAd.Listener {
            override fun onAppOpenAdLoaded() {
                AdLog.i("SmartCash: App Open Ad preloaded")
            }

            override fun onAppOpenAdFailedToLoad(error: AdError) {
                AdLog.w("SmartCash: App Open Ad failed — %s", error.message)
            }

            override fun onAppOpenAdImpression() {
                AdLog.d("SmartCash: App Open Ad impression")
                impressionTracker.track(
                    creativeId = CREATIVE_APPOPEN,
                    placementId = PLACEMENT_APP_OPEN,
                    adFormat = "APP_OPEN",
                )
            }

            override fun onAppOpenAdDismissed() {
                AdLog.d("SmartCash: App Open Ad dismissed")
            }
        })
        AppOpenAd.setAdExpiryMinutes(30)
    }

    override fun onTerminate() {
        AppOpenAd.destroy()
        super.onTerminate()
    }

    companion object {
        private const val APP_TOKEN = "smartcash-app-token-001"
        const val PLACEMENT_APP_OPEN = "smartcash-appopen"
        const val PLACEMENT_HOME_BANNER = "smartcash-home-banner"
        const val PLACEMENT_EARN_INTERSTITIAL = "smartcash-earn-interstitial"
        const val PLACEMENT_VIDEO = "smartcash-video-rewarded"
        const val PLACEMENT_NATIVE = "smartcash-native"
        const val CREATIVE_BANNER = "creative-banner-001"
        const val CREATIVE_INTERSTITIAL = "creative-interstitial-001"
        const val CREATIVE_VIDEO = "creative-video-001"
        const val CREATIVE_APPOPEN = "creative-appopen-001"
        const val CREATIVE_NATIVE = "creative-native-001"
    }
}
