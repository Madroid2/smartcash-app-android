# SmartCash — Rewards & Earning App

SmartCash is a native Android rewards application that lets users earn real money by completing surveys, watching videos, and playing games on popular reward portals — all from within a seamless in-app WebView experience. Earnings accumulate in a real-time wallet and can be withdrawn via PayPal.

---

## Features

- **Earn Hub** — Browse and launch real reward portals across three categories:
  - *Surveys* — Swagbucks, Survey Junkie, InboxDollars, Toluna, PrizeRebel
  - *Videos* — Swagbucks TV, InboxDollars Videos, MyPoints
  - *Games* — Swagbucks Games, InboxDollars Games, Mistplay
- **Full-Screen WebView** — JavaScript & DOM storage enabled; back-navigation respects in-page history
- **Live Wallet** — Animated balance counter, earnings breakdown chart, full transaction history
- **PayPal Withdrawals** — Sandbox-mode PayPal checkout with a $5.00 minimum threshold
- **Home Dashboard** — Auto-scrolling featured offers, daily earnings, streak tracker
- **Profile** — Stats overview, referral code sharing, notification & dark mode toggles
- **Apex Ad SDK** — Banner, Interstitial, Rewarded Video, and App Open ad formats
- **Apex Analytics** — Impression events are tracked in real time via our in-house analytics portal *(see below)*

---

## Architecture — MVVM + Clean Architecture

SmartCash is built following the **Model-View-ViewModel (MVVM)** pattern layered with Clean Architecture principles, ensuring clear separation of concerns, testability, and scalability.

```
app/
└── src/main/java/com/smartcash/app/
    ├── core/
    │   ├── design/          # Color, Typography, Theme
    │   ├── di/              # Hilt dependency injection modules
    │   ├── network/         # Retrofit API interfaces & DTOs
    │   └── util/            # ImpressionTracker, helpers
    ├── data/
    │   ├── model/           # Domain models (Portal, Transaction, …)
    │   └── repository/      # PortalRepository, WalletRepository
    ├── feature/
    │   ├── home/            # HomeScreen + HomeViewModel
    │   ├── earn/            # EarnScreen + EarnViewModel + WebViewScreen
    │   ├── wallet/          # WalletScreen + WalletViewModel
    │   └── profile/         # ProfileScreen + ProfileViewModel
    ├── ads/                 # BannerAdSlot, InterstitialAdHelper, VideoAdHelper
    └── navigation/          # NavGraph, Screen routes
```

**Key patterns:**
- Every ViewModel exposes a `sealed UiState` and a `StateFlow` — no mutable UI state leaks into the View layer
- Repositories return `Flow<T>` or `suspend fun … : Result<T>` — the UI never touches raw data sources
- Hilt handles all dependency injection from `@HiltAndroidApp` down to `@HiltViewModel`
- `applicationScope` (tied to `Application` lifecycle) is used for work that must survive screen rotation (e.g. `ImpressionTracker`)
- Navigation is handled by Navigation Compose with animated slide transitions

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Async | Coroutines + StateFlow / SharedFlow |
| Navigation | Navigation Compose |
| Networking | Retrofit 2 + OkHttp |
| Image loading | Coil |
| Ads | Apex Ad SDK (local AARs) |
| Payments | PayPal Android SDK (sandbox) |
| Min SDK | 21 (Android 5.0) |
| Target SDK | 35 (Android 15) |

---

## Apex Ad SDK Integration

SmartCash integrates the **Apex Ad SDK** for monetisation. Four ad formats are active:

| Format | Placement ID | Trigger |
|---|---|---|
| Banner | `smartcash-home-banner` | Home screen, pinned bottom |
| Interstitial | `smartcash-earn-interstitial` | After each portal session |
| Rewarded Video | `smartcash-video-rewarded` | Opt-in from Earn screen |
| App Open | `smartcash-appopen` | App foregrounded |

Initialisation follows the standard Apex SDK pattern in `SmartCashApplication`:

```kotlin
val config = ApexAdsConfig.Builder(APP_TOKEN)
    .debugLogging(true)
    .testMode(true)
    .build()
ApexAds.init(this, config)
ServiceLocator.register(AdNetworkClient::class.java, MockAdExchange())
WalletAdExtension.install()
AppOpenAd.initialize(this, PLACEMENT_APP_OPEN, listener)
```

---

## Impression Tracking

Every ad impression is dispatched to **Apex Analytics** — our in-house real-time analytics portal — via `ImpressionTracker`, a Hilt `@Singleton` that:

1. Accepts impression events from any ad listener callback
2. Enqueues them in a `Channel<ImpressionPayload>` (non-blocking)
3. Drains the channel in `applicationScope`, POSTing to `http://10.0.2.2:3001/api/impressions`
4. Retries up to 3 times with exponential backoff on network failure

```kotlin
impressionTracker.track(
    creativeId = "creative-banner-001",
    placementId = "smartcash-home-banner",
    adFormat = "BANNER",
)
```

---

## PayPal Integration

Withdrawals use the **PayPal Android SDK** in sandbox mode. The wallet enforces a $5.00 minimum before the checkout flow is launched. On success the local balance is updated and a confetti animation is shown.

---

## Apex Analytics *(Beta)*

SmartCash ships alongside **Apex Analytics** — a purpose-built, in-house ad impression dashboard developed to give full visibility into SDK performance without relying on third-party analytics platforms.

> ⚠️ Apex Analytics is currently in **beta**. Features and APIs are subject to change.

The portal is a companion project (`/SmartCashAnalytics`) running as a Docker container on the host machine. It receives impression events from the emulator via the loopback address and renders them in real time:

- KPI cards (Total, Banner, Interstitial, Video, App Open)
- 24-hour impressions area chart broken down by format
- Creative performance table (sortable)
- Live feed of the last 20 impressions

**Quick start:**
```bash
cd SmartCashAnalytics
docker-compose up --build
# Dashboard → http://localhost:3001
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Build
```bash
git clone https://github.com/Madroid2/SmartCash.git
cd SmartCash
# Open in Android Studio and sync Gradle
```

The project references Apex Ad SDK AARs from `app/libs/`. These are included in the repository.

---

## License

Private repository. All rights reserved.
# smart-cash-app-android
