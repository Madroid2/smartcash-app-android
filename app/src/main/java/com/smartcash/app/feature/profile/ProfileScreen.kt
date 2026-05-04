package com.smartcash.app.feature.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcash.app.core.design.DeepNavy
import com.smartcash.app.core.design.EmeraldGreen
import com.smartcash.app.core.design.EmeraldGreenLight
import com.smartcash.app.core.design.GoldAccent
import com.smartcash.app.core.design.NavyCard
import com.smartcash.app.core.design.SlateGray
import com.smartcash.app.core.design.White

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val stats by viewModel.stats.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AvatarSection(stats.referralCode)
        }

        item {
            StatsGrid(stats)
        }

        item {
            SettingsSection(
                notificationsEnabled = notificationsEnabled,
                darkModeEnabled = darkModeEnabled,
                onNotificationsChange = { viewModel.toggleNotifications(it) },
                onDarkModeChange = { viewModel.toggleDarkMode(it) },
            )
        }

        item {
            ReferralCard(
                referralCode = stats.referralCode,
                onShare = {
                    val intent = Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join SmartCash and earn rewards! Use my referral code: ${stats.referralCode}",
                            )
                            type = "text/plain"
                        },
                        "Share Referral Code",
                    )
                    context.startActivity(intent)
                },
            )
        }

        item {
            AppVersionFooter()
        }

        item {
            Box(modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun AvatarSection(referralCode: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AvatarCircle(initials = "SC")
        Text(
            "SmartCash User",
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            referralCode,
            color = EmeraldGreenLight,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AvatarCircle(initials: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(EmeraldGreen, EmeraldGreenLight),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials,
            color = White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun StatsGrid(stats: ProfileStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileStatCard(
                label = "Total Earned",
                value = "$${"%.2f".format(stats.totalEarned)}",
                modifier = Modifier.weight(1f),
            )
            ProfileStatCard(
                label = "Surveys",
                value = "${stats.surveysCompleted}",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileStatCard(
                label = "Videos",
                value = "${stats.videosWatched}",
                modifier = Modifier.weight(1f),
            )
            ProfileStatCard(
                label = "Games",
                value = "${stats.gamesPlayed}",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                label,
                color = SlateGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                value,
                color = EmeraldGreenLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SettingsSection(
    notificationsEnabled: Boolean,
    darkModeEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Settings",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            SettingItem(
                label = "Notifications",
                enabled = notificationsEnabled,
                onToggle = onNotificationsChange,
            )

            SettingItem(
                label = "Dark Mode",
                enabled = darkModeEnabled,
                onToggle = onDarkModeChange,
            )
        }
    }
}

@Composable
private fun SettingItem(label: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = White,
            fontSize = 14.sp,
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmeraldGreen,
                checkedTrackColor = EmeraldGreen.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun ReferralCard(referralCode: String, onShare: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Refer a Friend",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                "Share your unique referral code and earn \$5.00 for each friend who signs up!",
                color = SlateGray,
                fontSize = 12.sp,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepNavy, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    referralCode,
                    color = GoldAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            ) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = White,
                )
                Text("Share Code", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AppVersionFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "SmartCash v1.0.0",
            color = SlateGray,
            fontSize = 12.sp,
        )
    }
}
