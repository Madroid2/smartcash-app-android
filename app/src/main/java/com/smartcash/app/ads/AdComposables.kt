package com.smartcash.app.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.apexads.sdk.banner.BannerAd
import com.apexads.sdk.banner.BannerAdListener
import com.apexads.sdk.banner.BannerAdView
import com.apexads.sdk.core.error.AdError
import com.apexads.sdk.core.models.AdSize
import com.apexads.sdk.interstitial.InterstitialAd
import com.apexads.sdk.interstitial.InterstitialAdListener
import com.apexads.sdk.video.VideoAd
import com.apexads.sdk.video.VideoAdListener
import com.smartcash.app.SmartCashApplication
import com.smartcash.app.core.util.ImpressionTracker
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

// ── Hilt EntryPoint — lets ad helpers resolve ImpressionTracker without injection ──

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AdEntryPoint {
    fun impressionTracker(): ImpressionTracker
}

private fun impressionTracker(context: Context): ImpressionTracker =
    EntryPointAccessors
        .fromApplication(context.applicationContext, AdEntryPoint::class.java)
        .impressionTracker()

// ── Banner Ad (Composable wrapper — matches Velora's BannerAdSlot pattern) ────

/**
 * Wraps [BannerAdView] inside Compose via [AndroidView].
 * Fires an impression event via [ImpressionTracker] on [BannerAdListener.onAdLoaded].
 */
@Composable
fun BannerAdSlot(
    placementId: String,
    adSize: AdSize = AdSize.BANNER_320x50,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bannerView = remember(placementId) { BannerAdView(context) }
    val adHolder = remember(placementId) { arrayOfNulls<BannerAd>(1) }

    remember(placementId) {
        val tracker = impressionTracker(context)
        val ad = BannerAd.Builder(placementId)
            .adSize(adSize)
            .listener(object : BannerAdListener {
                override fun onAdLoaded() {
                    tracker.track(
                        creativeId = SmartCashApplication.CREATIVE_BANNER,
                        placementId = placementId,
                        adFormat = "BANNER",
                    )
                    bannerView.post { adHolder[0]?.show(bannerView) }
                }
                override fun onAdFailed(error: AdError) {}
            })
            .build()
        adHolder[0] = ad
        ad.load()
    }

    AndroidView(
        factory = { bannerView },
        modifier = modifier
            .fillMaxWidth()
            .height(if (adSize == AdSize.MRECT_300x250) 250.dp else 50.dp),
    )
}

// ── Interstitial Ad ───────────────────────────────────────────────────────────

class InterstitialAdHelper(
    private val placementId: String,
    private val impressionTracker: ImpressionTracker,
) {
    private var interstitialAd: InterstitialAd? = null

    fun load() {
        interstitialAd = InterstitialAd.Builder(placementId)
            .listener(object : InterstitialAdListener {
                override fun onInterstitialLoaded() {}
                override fun onInterstitialFailed(error: AdError) {}
                override fun onInterstitialShown() {
                    impressionTracker.track(
                        creativeId = SmartCashApplication.CREATIVE_INTERSTITIAL,
                        placementId = placementId,
                        adFormat = "INTERSTITIAL",
                    )
                }
                override fun onInterstitialClosed() { load() } // pre-load next
                override fun onInterstitialClicked() {}
            })
            .build()
        interstitialAd?.load()
    }

    fun show(activity: Activity) {
        interstitialAd?.show(activity)
    }
}

// ── Rewarded Video Ad ─────────────────────────────────────────────────────────

class VideoAdHelper(
    private val placementId: String,
    private val impressionTracker: ImpressionTracker,
    private val onRewarded: () -> Unit = {},
) {
    private var videoAd: VideoAd? = null

    fun load() {
        videoAd = VideoAd.Builder(placementId)
            .listener(object : VideoAdListener {
                override fun onVideoAdLoaded() {}
                override fun onVideoAdFailed(error: AdError) {}
                override fun onVideoAdStarted() {
                    impressionTracker.track(
                        creativeId = SmartCashApplication.CREATIVE_VIDEO,
                        placementId = placementId,
                        adFormat = "VIDEO",
                    )
                }
                override fun onVideoAdCompleted() { load() } // pre-load next
                override fun onRewardEarned() { onRewarded() }
            })
            .build()
        videoAd?.load()
    }

    fun show(activity: Activity) {
        videoAd?.show(activity)
    }
}
