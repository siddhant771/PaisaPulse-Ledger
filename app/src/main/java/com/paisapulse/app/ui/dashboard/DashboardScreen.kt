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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paisapulse.app.data.local.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable

fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val transactionList by viewModel.transactions.collectAsStateWithLifecycle()
    LazyColumn {
        items(transactionList) { transaction ->
            Text(text = "${transaction.category} : ${transaction.amount}")
        }
    }

}
