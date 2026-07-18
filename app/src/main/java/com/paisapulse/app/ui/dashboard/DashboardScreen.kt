package com.paisapulse.app.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.ui.components.EmptyState
import com.paisapulse.app.ui.settings.SettingsScreen
import java.io.File
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.transactions.collectAsStateWithLifecycle()
    val month by viewModel.currentMonth.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    val context = LocalContext.current
    var budget by remember { mutableStateOf(BudgetPreferences.get(context)) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCategoryBudgetDialog by remember { mutableStateOf(false) }
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
        return
    }
    val expenses = transactions.filter { it.isExpense }.sumOf { it.amount }
    val income = transactions.filterNot { it.isExpense }.sumOf { it.amount }
    val categoryTotals = transactions.filter { it.isExpense }.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }.toList().sortedByDescending { it.second }.take(4)
    val overBudgetCategories = categoryTotals.filter { (category, spent) -> BudgetPreferences.categoryLimit(context, category)?.let { spent > it } == true }
    val visibleTransactions = transactions.filter {
        searchQuery.isBlank() || it.description.contains(searchQuery, true) || it.category.contains(searchQuery, true)
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "Add transaction") } }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { DashboardHeader(context, month, viewModel::setMonth, viewModel::exportTransactions, onSettings = { showSettings = true }) }
            item { BalanceCard(income, expenses) }
            item { BudgetSummary(expenses, budget, onSetBudget = { showBudgetDialog = true }, onSetCategoryBudget = { showCategoryBudgetDialog = true }) }
            item { AnalyticsHighlights(income, expenses, transactions.size) }
            if (overBudgetCategories.isNotEmpty()) item { Text("Budget alert: ${overBudgetCategories.joinToString { it.first }} is over its category limit.", color = MaterialTheme.colorScheme.error) }
            item { SectionTitle("Spending overview") }
            item {
                if (categoryTotals.isEmpty()) EmptyState("Add an expense to see your spending insights.")
                else CategorySummary(categoryTotals)
            }
            item { SectionTitle("Recent transactions", "${allTransactions.size} total") }
            item { OutlinedTextField(searchQuery, { searchQuery = it }, Modifier.fillMaxWidth(), label = { Text("Search transactions") }, singleLine = true) }
            if (visibleTransactions.isEmpty()) item { EmptyState(if (searchQuery.isBlank()) "No transactions for this month." else "No matching transactions.") }
            else items(visibleTransactions, key = { it.id }) { transaction ->
                TransactionRow(transaction, onEdit = { editingTransaction = transaction }, onDelete = { viewModel.deleteTransaction(transaction.id) })
            }
        }
        if (showDialog) AddTransactionDialog(
            onDismiss = { showDialog = false },
            onConfirm = { amount, description, category, isExpense -> viewModel.addTransaction(amount, description, category, isExpense, System.currentTimeMillis()) }
        )
        editingTransaction?.let { existing -> AddTransactionDialog(
            existing = existing,
            onDismiss = { editingTransaction = null },
            onConfirm = { amount, description, category, isExpense ->
                viewModel.updateTransaction(existing.copy(amount = amount, description = description, category = category, isExpense = isExpense))
                editingTransaction = null
            }
        ) }
        if (showBudgetDialog) BudgetDialog(onDismiss = { showBudgetDialog = false }) { amount -> BudgetPreferences.save(context, amount); budget = amount; showBudgetDialog = false }
        if (showCategoryBudgetDialog) CategoryBudgetDialog(onDismiss = { showCategoryBudgetDialog = false }) { category, amount -> BudgetPreferences.saveCategoryLimit(context, category, amount); showCategoryBudgetDialog = false }
    }
}

@Composable private fun AnalyticsHighlights(income: Double, expenses: Double, count: Int) {
    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
        Text("Analytics", style = MaterialTheme.typography.titleMedium)
        Text("${count} transactions this month")
        Text("Savings rate: ${if (income > 0) "%.0f".format(((income - expenses) / income) * 100) else "0"}%")
    } }
}

@Composable private fun BudgetDialog(onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Set monthly budget") }, text = { OutlinedTextField(value, { value = it }, label = { Text("Budget amount") }) }, confirmButton = { Button(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let(onSave) }) { Text("Save") } }, dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } })
}

@Composable private fun CategoryBudgetDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var category by remember { mutableStateOf("") }; var value by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Category limit") }, text = { Column { OutlinedTextField(category, { category = it }, label = { Text("Category") }); OutlinedTextField(value, { value = it }, label = { Text("Monthly limit") }) } }, confirmButton = { Button(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let { if (category.isNotBlank()) onSave(category.trim(), it) } }) { Text("Save") } }, dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun DashboardHeader(context: Context, month: Int, onMonthChange: (Int) -> Unit, onExport: ((String) -> Unit) -> Unit, onSettings: () -> Unit) {
    Column(Modifier.padding(top = 18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PaisaPulse", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Your money, made clear", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {
                onExport { csv ->
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"; putExtra(Intent.EXTRA_STREAM, writeCsvToCache(context, csv)); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }, "Export transactions"))
                }
            }) { Icon(Icons.Default.Share, "Export transactions") }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings and privacy") }
        }
        MonthSelector(month, onMonthChange)
    }
}

@Composable
private fun BalanceCard(income: Double, expenses: Double) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(20.dp)) {
            Text("This month", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("₹${"%.0f".format(income - expenses)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("Income", income, MaterialTheme.colorScheme.primary)
                Metric("Spent", expenses, MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable private fun Metric(label: String, amount: Double, color: androidx.compose.ui.graphics.Color) {
    Column { Text(label, style = MaterialTheme.typography.labelMedium); Text("₹${"%.0f".format(amount)}", color = color, fontWeight = FontWeight.SemiBold) }
}

@Composable private fun SectionTitle(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (trailing != null) Text(trailing, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun CategorySummary(categories: List<Pair<String, Double>>) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            categories.forEachIndexed { index, (name, amount) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name); Text("₹${"%.0f".format(amount)}", fontWeight = FontWeight.SemiBold)
                }
                if (index != categories.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable private fun TransactionRow(transaction: TransactionEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card {
        Row(Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(transaction.description.ifBlank { "Transaction" }, fontWeight = FontWeight.SemiBold)
                Text("${transaction.category} · ${if (transaction.source == "sms_ai") "Auto-categorized" else "Manual"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${if (transaction.isExpense) "−" else "+"}₹${"%.0f".format(transaction.amount)}", fontWeight = FontWeight.Bold, color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            IconButton(onClick = onDelete, modifier = Modifier.padding(start = 6.dp).size(36.dp)) { Icon(Icons.Default.Delete, "Delete transaction") }
        }
    }
}

@Composable fun MonthSelector(currentMonth: Int, onMonthChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.material3.TextButton(onClick = { onMonthChange(if (currentMonth > 1) currentMonth - 1 else 12) }) { Text("‹ Previous") }
        Text(Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()), fontWeight = FontWeight.SemiBold)
        androidx.compose.material3.TextButton(onClick = { onMonthChange(if (currentMonth < 12) currentMonth + 1 else 1) }) { Text("Next ›") }
    }
}

fun writeCsvToCache(context: Context, content: String): Uri {
    val file = File(context.cacheDir, "transactions_${System.currentTimeMillis()}.csv")
    file.writeText(content)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
