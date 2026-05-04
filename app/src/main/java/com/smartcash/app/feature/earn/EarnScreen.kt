package com.smartcash.app.feature.earn

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcash.app.core.design.DeepNavy
import com.smartcash.app.core.design.EmeraldGreen
import com.smartcash.app.core.design.EmeraldGreenLight
import com.smartcash.app.core.design.NavyCard
import com.smartcash.app.core.design.NavySurface
import com.smartcash.app.core.design.SlateGray
import com.smartcash.app.core.design.White
import com.smartcash.app.data.model.Portal
import com.smartcash.app.data.model.PortalCategory

@Composable
fun EarnScreen(
    viewModel: EarnViewModel,
    onPortalClick: (Portal) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
    ) {
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                viewModel.selectCategory(category)
            },
        )

        when (val state = uiState) {
            is EarnUiState.Loading -> LoadingState()
            is EarnUiState.Success -> SuccessState(
                portals = state.portals,
                onPortalClick = onPortalClick,
            )
            is EarnUiState.Error -> ErrorState(state.message)
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: PortalCategory,
    onCategorySelected: (PortalCategory) -> Unit,
) {
    TabRow(
        selectedTabIndex = PortalCategory.entries.indexOf(selectedCategory),
        modifier = Modifier
            .fillMaxWidth()
            .background(NavySurface),
        containerColor = NavySurface,
        contentColor = EmeraldGreen,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 12.dp)
                    .background(EmeraldGreen, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
            )
        },
    ) {
        PortalCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        category.name,
                        color = if (isSelected) EmeraldGreen else SlateGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                },
            )
        }
    }
}

@Composable
private fun SuccessState(
    portals: List<Portal>,
    onPortalClick: (Portal) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(portals.size) { index ->
            val portal = portals[index]
            EarnPortalCard(portal = portal, onPortalClick = onPortalClick)
        }

        item {
            Box(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EarnPortalCard(portal: Portal, onPortalClick: (Portal) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onPortalClick(portal) },
            ),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(portal.iconEmoji, fontSize = 40.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    portal.name,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    portal.description,
                    color = SlateGray,
                    fontSize = 12.sp,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        "Earn: ${portal.estimatedReward}",
                        color = EmeraldGreenLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "Time: ${portal.estimatedTime}",
                        color = SlateGray,
                        fontSize = 12.sp,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(EmeraldGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    "Go Earn",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(NavyCard, RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Error",
            color = Color.Red,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            message,
            color = SlateGray,
            fontSize = 14.sp,
        )
    }
}

