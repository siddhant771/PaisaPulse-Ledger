package com.paisapulse.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paisapulse.app.data.local.TransactionEntity

@Composable

fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val deepBackground = Color(0xFF121616)   // Dark background
    val cardBackground = Color(0xFF1C2128)   // Card surface
    val neonPurple = Color(0xFF8A56FF)       // Primary accent // PaisaPulse primary electric accent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBackground)
            .padding(top = 24.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = neonPurple
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Block
                item {
                    Text(
                        text = "PaisaPulse Summary",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // Balance Summary Display Matrix
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = cardBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Total Spent", color = Color.Gray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${uiState.totalExpenses}", color = Color(0xFFFF6B6B), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = cardBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Total Income", color = Color.Gray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("₹${uiState.totalIncome}", color = Color(0xFF51CF7A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Live Ledger List Header
                item {
                    Text(
                        text = "Live Ledger Feed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Individual Transaction Render Elements
                items(uiState.recentTransactions) { tx ->
                    TransactionRow(transaction = tx, cardColor = cardBackground, accentColor = neonPurple)
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: TransactionEntity, cardColor: Color, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = transaction.merchant, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = transaction.category, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "₹${transaction.amount}",
                color = if (transaction.category == "Income") Color(0xFF51CF7A) else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
