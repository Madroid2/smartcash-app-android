package com.smartcash.app.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcash.app.ads.BannerAdSlot
import com.smartcash.app.core.design.DeepNavy
import com.smartcash.app.core.design.EmeraldGreen
import com.smartcash.app.core.design.EmeraldGreenLight
import com.smartcash.app.core.design.NavyCard
import com.smartcash.app.core.design.SlateGray
import com.smartcash.app.core.design.White
import com.smartcash.app.data.model.Portal
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPortalClick: (Portal) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingState()
        is HomeUiState.Success -> SuccessState(state = state, onPortalClick = onPortalClick)
        is HomeUiState.Error   -> ErrorState(state.message)
    }
}

// ── Loading shimmer ────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DeepNavy).padding(16.dp),
    ) {
        item {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(180.dp).padding(bottom = 16.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(3) { ShimmerBox(modifier = Modifier.weight(1f).height(100.dp)) }
            }
        }
        item { Text("Featured Offers", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        items(3) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun ShimmerBox(modifier: Modifier = Modifier) {
    var bright by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (bright) 0.3f else 0.6f,
        animationSpec = tween(1000),
        label = "shimmer",
    )
    LaunchedEffect(Unit) { while (true) { delay(1000); bright = !bright } }
    Box(modifier = modifier.background(SlateGray, RoundedCornerShape(12.dp)).alpha(alpha))
}

// ── Success content ────────────────────────────────────────────────────────────

@Composable
private fun SuccessState(state: HomeUiState.Success, onPortalClick: (Portal) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DeepNavy),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { HeroBanner(state.featuredOffers) }
        item { QuickStatsRow(state.earningsToday, state.streakDays, state.totalWithdrawn) }
        item {
            Text("Featured Offers", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        items(state.featuredOffers.size) { index ->
            PortalCard(portal = state.featuredOffers[index], onPortalClick = onPortalClick)
        }
        item { BannerAdSlot(placementId = "smartcash-home-banner") }
        item { Box(modifier = Modifier.height(16.dp)) }
    }
}

// ── Hero banner — uses native androidx.compose.foundation.pager ───────────────

@Composable
private fun HeroBanner(portals: List<Portal>) {
    if (portals.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { portals.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % portals.size)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(180.dp),
        ) { page ->
            val portal = portals[page]
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 4.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(EmeraldGreen.copy(alpha = 0.3f), EmeraldGreen)),
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(portal.iconEmoji, fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Text(portal.name, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Earn ${portal.estimatedReward}",
                            color = EmeraldGreenLight,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(portals.size) { index ->
                val selected = pagerState.currentPage == index
                val dotSize by animateFloatAsState(
                    targetValue = if (selected) 10f else 6f,
                    animationSpec = tween(300),
                    label = "dot_$index",
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(dotSize.dp)
                        .background(
                            color = if (selected) EmeraldGreen else SlateGray,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

// ── Quick stats ────────────────────────────────────────────────────────────────

@Composable
private fun QuickStatsRow(earningsToday: Double, streakDays: Int, totalWithdrawn: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard("Today's Earnings", "$${"%.2f".format(earningsToday)}", Modifier.weight(1f))
        StatCard("Streak",           "$streakDays days",                  Modifier.weight(1f))
        StatCard("Withdrawn",        "$${"%.2f".format(totalWithdrawn)}", Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(label, color = SlateGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(value, color = EmeraldGreenLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Portal card ────────────────────────────────────────────────────────────────

@Composable
private fun PortalCard(portal: Portal, onPortalClick: (Portal) -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "card_scale",
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onPortalClick(portal) },
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(portal.iconEmoji, fontSize = 32.sp)
            }
            Column(
                modifier = Modifier.weight(1f).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(portal.name, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Earn ${portal.estimatedReward}", color = EmeraldGreenLight, fontSize = 12.sp)
                    Text(portal.estimatedTime, color = SlateGray, fontSize = 12.sp)
                }
            }
            Box(
                modifier = Modifier
                    .background(EmeraldGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("Go", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Error ──────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(DeepNavy).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Error", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp))
        Text(message, color = SlateGray, fontSize = 14.sp)
    }
}
