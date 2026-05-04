package com.smartcash.app.feature.wallet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcash.app.core.design.DeepNavy
import com.smartcash.app.core.design.EmeraldGreen
import com.smartcash.app.core.design.EmeraldGreenLight
import com.smartcash.app.core.design.ErrorRed
import com.smartcash.app.core.design.NavyCard
import com.smartcash.app.core.design.SlateGray
import com.smartcash.app.core.design.SuccessGreen
import com.smartcash.app.core.design.White
import com.smartcash.app.data.model.Transaction
import com.smartcash.app.data.model.TransactionStatus
import com.smartcash.app.data.model.TransactionType
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is WalletEvent.ShowMinimumBalanceError -> {
                    snackbarHostState.showSnackbar("Minimum withdrawal amount is $5.00")
                }
                is WalletEvent.WithdrawalSuccess -> {
                    snackbarHostState.showSnackbar("Withdrawal successful!")
                }
                is WalletEvent.WithdrawalFailure -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
    ) {
        when (val state = uiState) {
            is WalletUiState.Loading -> LoadingState()
            is WalletUiState.Success -> SuccessState(
                balance = state.balance,
                transactions = state.transactions,
                viewModel = viewModel,
            )
            is WalletUiState.Error -> ErrorState(state.message)
        }

        SnackbarHost(hostState = snackbarHostState)

        if (showConfetti) {
            ConfettiAnimation()
            LaunchedEffect(Unit) {
                delay(2500)
                viewModel.dismissConfetti()
            }
        }
    }
}

@Composable
private fun SuccessState(
    balance: Double,
    transactions: List<Transaction>,
    viewModel: WalletViewModel,
) {
    var withdrawAmount by remember { mutableStateOf("") }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            BalanceCard(balance)
        }

        item {
            EarningsBreakdownChart(transactions)
        }

        item {
            WithdrawCard(
                balance = balance,
                onWithdraw = { showWithdrawDialog = true },
            )
        }

        item {
            Text(
                "Transaction History",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        items(transactions.size) { index ->
            TransactionItem(transactions[index])
        }

        item {
            Box(modifier = Modifier.height(16.dp))
        }
    }

    if (showWithdrawDialog) {
        WithdrawDialog(
            balance = balance,
            onDismiss = { showWithdrawDialog = false },
            onWithdraw = { amount ->
                viewModel.withdraw(amount)
                withdrawAmount = ""
                showWithdrawDialog = false
            },
        )
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(EmeraldGreen.copy(alpha = 0.3f), EmeraldGreen),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Available Balance",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                AnimatedContent(
                    targetState = balance,
                    label = "balance",
                ) { amount ->
                    Text(
                        "$${"%.2f".format(amount)}",
                        color = White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun EarningsBreakdownChart(transactions: List<Transaction>) {
    val surveyEarnings = transactions
        .filter { it.type == TransactionType.SURVEY && it.amount > 0 }
        .sumOf { it.amount }
    val videoEarnings = transactions
        .filter { it.type == TransactionType.VIDEO && it.amount > 0 }
        .sumOf { it.amount }
    val gameEarnings = transactions
        .filter { it.type == TransactionType.GAME && it.amount > 0 }
        .sumOf { it.amount }
    val bonusEarnings = transactions
        .filter { it.type == TransactionType.BONUS && it.amount > 0 }
        .sumOf { it.amount }

    val total = surveyEarnings + videoEarnings + gameEarnings + bonusEarnings
    if (total == 0.0) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Earnings Breakdown",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BarSegment(label = "Surveys", value = surveyEarnings, total = total, color = EmeraldGreen, modifier = Modifier.weight(1f))
                BarSegment(label = "Videos", value = videoEarnings, total = total, color = EmeraldGreenLight, modifier = Modifier.weight(1f))
                BarSegment(label = "Games", value = gameEarnings, total = total, color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                BarSegment(label = "Bonus", value = bonusEarnings, total = total, color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BarSegment(label: String, value: Double, total: Double, color: Color, modifier: Modifier = Modifier) {
    val percentage = (value / total * 100).toInt()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth(if (percentage > 0) (percentage / 100f) else 0f)
                    .background(color, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)),
            )
        }
        Text(
            label,
            color = SlateGray,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            "$percentage%",
            color = White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun WithdrawCard(balance: Double, onWithdraw: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Withdraw to PayPal",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Minimum withdrawal: $5.00",
                color = SlateGray,
                fontSize = 12.sp,
            )
            Button(
                onClick = onWithdraw,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                enabled = balance >= 5.0,
            ) {
                Text(
                    "Withdraw Now",
                    color = White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    val typeIcon = when (transaction.type) {
        TransactionType.SURVEY -> "📋"
        TransactionType.VIDEO -> "📺"
        TransactionType.GAME -> "🎮"
        TransactionType.BONUS -> "🎁"
        TransactionType.WITHDRAWAL -> "💸"
        TransactionType.REFERRAL -> "👥"
    }

    val statusColor = when (transaction.status) {
        TransactionStatus.COMPLETED -> SuccessGreen
        TransactionStatus.PENDING -> Color(0xFFF59E0B)
        TransactionStatus.FAILED -> ErrorRed
    }

    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())
    val dateString = formatter.format(transaction.timestamp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(typeIcon, fontSize = 20.sp)
                Column {
                    Text(
                        transaction.description,
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        dateString,
                        color = SlateGray,
                        fontSize = 12.sp,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (transaction.amount > 0) "+" else ""}${"%.2f".format(transaction.amount)}",
                    color = if (transaction.amount > 0) SuccessGreen else ErrorRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    transaction.status.name,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun WithdrawDialog(
    balance: Double,
    onDismiss: () -> Unit,
    onWithdraw: (Double) -> Unit,
) {
    var amount by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("PayPal Checkout (Sandbox)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Available: $${"%.2f".format(balance)}", color = White)
                androidx.compose.material3.OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("$") },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { onWithdraw(it) }
                },
            ) {
                Text("Authorize")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = NavyCard,
        titleContentColor = White,
        textContentColor = White,
    )
}

@Composable
private fun ConfettiAnimation() {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        val duration = 2500L
        while (progress < 1f) {
            progress = ((System.currentTimeMillis() - start).toFloat() / duration).coerceIn(0f, 1f)
            delay(16L)
        }
    }

    val particles = remember {
        List(60) {
            floatArrayOf(
                Random.nextFloat(),                    // x  (0..1)
                Random.nextFloat() * 0.3f,             // yOffset
                0.5f + Random.nextFloat() * 0.5f,      // speed
                Random.nextInt(4).toFloat(),            // colorIndex
                3f + Random.nextFloat() * 5f,           // radius
            )
        }
    }

    val particleColors = listOf(EmeraldGreen, EmeraldGreenLight, Color(0xFFF4C542), SuccessGreen)

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yPos = (size.height * (p[1] + progress * p[2] * 1.8f)) % size.height
            drawCircle(
                color = particleColors[p[3].toInt()],
                radius = p[4],
                center = Offset(p[0] * size.width, yPos),
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Loading wallet...",
            color = SlateGray,
        )
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
            color = ErrorRed,
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
