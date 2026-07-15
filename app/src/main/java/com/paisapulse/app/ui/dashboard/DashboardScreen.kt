package com.paisapulse.app.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.ui.components.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File


@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    // Observe the FILTERED list instead of the full transactionList
    val transactionList by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val categoryTotals by viewModel.categoryTotals.collectAsStateWithLifecycle()
    val month by viewModel.currentMonth.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    if(isLoading){
        Box(modifier=Modifier.fillMaxSize(),contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }

    else{
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { paddingValues ->
            // Move your content INSIDE the Scaffold and use paddingValues
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                IconButton(onClick = {
                    viewModel.exportTransactions { csvContent ->
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM,writeCsvToCache(context,csvContent))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(sendIntent,"Export Transactions"))
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Export")
                }

                MonthSelector(currentMonth = month, onMonthChange = { viewModel.setMonth(it) })

                SpendingSummaryChart(categoryTotals)

                if(transactionList.isEmpty()){
                    EmptyState(message = "No transactions for this month.")
                }
                else{
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {

                        items(transactionList) { transaction ->
                            Text(
                                text = "${transaction.category} : ₹${transaction.amount}",
                                modifier = Modifier.padding(vertical = 4.dp)

                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AddTransactionDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { amt, desc, exp ->
                        viewModel.addTransaction(amt, desc, exp, System.currentTimeMillis())
                    }
                )
            }
        }
    }
}
@Composable
fun MonthSelector(currentMonth:Int,onMonthChange:(Int)->Unit){
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
        Button(onClick = {onMonthChange(if(currentMonth>1) currentMonth-1 else 12)}){
            Text("<")
        }
        Button(onClick = {onMonthChange(if(currentMonth>1) currentMonth+1 else 1)}){
            Text(">")
        }
    }
}
fun writeCsvToCache(context: Context, content: String): Uri {
    val file = File(context.cacheDir, "transactions_${System.currentTimeMillis()}.csv")
    file.writeText(content)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}