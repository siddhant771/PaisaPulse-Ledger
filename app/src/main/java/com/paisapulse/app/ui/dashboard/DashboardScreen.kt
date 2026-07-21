package com.paisapulse.app.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paisapulse.app.data.SmsStatusStore
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.ui.components.EmptyState
import com.paisapulse.app.ui.settings.SettingsScreen
import com.paisapulse.app.ui.theme.PulseBlue
import com.paisapulse.app.ui.theme.PulseOrange
import com.paisapulse.app.ui.theme.PulseTeal
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
    val context = LocalContext.current
    val smsStatus by remember(context) { SmsStatusStore.messages(context) }
        .collectAsStateWithLifecycle(initialValue = SmsStatusStore.get(context))
    var destination by remember { mutableStateOf(Destination.Home) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCategoryBudgetDialog by remember { mutableStateOf(false) }
    var budget by remember { mutableStateOf(BudgetPreferences.get(context)) }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (destination == Destination.Settings) {
        SettingsScreen(onBack = { destination = Destination.Home })
        return
    }

    val expenses = transactions.filter { it.isExpense }.sumOf { it.amount }
    val categoryTotals = transactions.filter { it.isExpense }.groupBy { it.category }
        .mapValues { (_, values) -> values.sumOf { it.amount } }
        .toList().sortedByDescending { it.second }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Add transaction", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        when (destination) {
            Destination.Home -> HomeContent(
                modifier = Modifier.padding(padding), context = context, month = month,
                expenses = expenses, transactions = transactions,
                categoryTotals = categoryTotals, budget = budget, smsStatus = smsStatus,
                onMonthChange = viewModel::setMonth, onSettings = { destination = Destination.Settings },
                onAnalytics = { destination = Destination.Analytics }, onLedger = { destination = Destination.Ledger },
                onExport = viewModel::exportTransactions, onSetBudget = { showBudgetDialog = true },
                onSetCategoryBudget = { showCategoryBudgetDialog = true }, onEdit = { editingTransaction = it }
            )
            Destination.Analytics -> AnalyticsContent(
                modifier = Modifier.padding(padding), month = month, categoryTotals = categoryTotals,
                expenses = expenses, transactionCount = transactions.size,
                onBack = { destination = Destination.Home }
            )
            Destination.Ledger -> LedgerContent(
                modifier = Modifier.padding(padding), transactions = transactions, totalCount = allTransactions.size,
                onBack = { destination = Destination.Home }, onEdit = { editingTransaction = it },
                onDelete = viewModel::deleteTransaction
            )
            Destination.Settings -> Unit
        }
    }
    if (showAddDialog) AddTransactionDialog(onDismiss = { showAddDialog = false }, onConfirm = { amount, description, category, isExpense ->
        viewModel.addTransaction(amount, description, category, isExpense, System.currentTimeMillis()); showAddDialog = false
    })
    editingTransaction?.let { existing ->
        AddTransactionDialog(existing = existing, onDismiss = { editingTransaction = null }, onConfirm = { amount, description, category, isExpense ->
            viewModel.updateTransaction(existing.copy(amount = amount, description = description, category = category, isExpense = isExpense)); editingTransaction = null
        })
    }
    if (showBudgetDialog) BudgetDialog(onDismiss = { showBudgetDialog = false }) { value -> BudgetPreferences.save(context, value); budget = value; showBudgetDialog = false }
    if (showCategoryBudgetDialog) CategoryBudgetDialog(onDismiss = { showCategoryBudgetDialog = false }) { category, value -> BudgetPreferences.saveCategoryLimit(context, category, value); showCategoryBudgetDialog = false }
}

private enum class Destination { Home, Analytics, Ledger, Settings }

@Composable
private fun HomeContent(
    modifier: Modifier, context: Context, month: Int, expenses: Double,
    transactions: List<TransactionEntity>, categoryTotals: List<Pair<String, Double>>, budget: Double?, smsStatus: String?,
    onMonthChange: (Int) -> Unit, onSettings: () -> Unit, onAnalytics: () -> Unit, onLedger: () -> Unit,
    onExport: ((String) -> Unit) -> Unit, onSetBudget: () -> Unit, onSetCategoryBudget: () -> Unit, onEdit: (TransactionEntity) -> Unit
) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Header(context, month, onMonthChange, onExport, onSettings) }
        smsStatus?.let { item { ProcessingStatus(it) } }
        item { BalanceCard(expenses, budget) }
        item { BudgetSummary(expenses, budget, onSetBudget, onSetCategoryBudget) }
        item { SectionTitle("Quick overview", action = "Analytics", onAction = onAnalytics) }
        item { QuickOverview(categoryTotals, expenses, transactions.size, onAnalytics) }
        item { SectionTitle("Recent activity", action = "View all", onAction = onLedger) }
        if (transactions.isEmpty()) item { EmptyState("No transactions yet. Tap + to add one or enable SMS tracking.") }
        else items(transactions.take(3), key = { it.id }) { transaction -> CompactTransactionRow(transaction, onEdit = { onEdit(transaction) }) }
        item { Box(Modifier.height(70.dp)) }
    }
}

@Composable
private fun Header(context: Context, month: Int, onMonthChange: (Int) -> Unit, onExport: ((String) -> Unit) -> Unit, onSettings: () -> Unit) {
    Column(Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PaisaPulse", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Text("Your money, in one calm place", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { onExport { csv -> shareCsv(context, csv) } }) { Icon(Icons.Default.Share, "Export ledger") }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings") }
        }
        MonthSelector(month, onMonthChange)
    }
}

@Composable
private fun BalanceCard(expenses: Double, budget: Double?) {
    val hasBudget = budget != null
    val primaryAmount = if (hasBudget) budget!! - expenses else expenses
    val primaryLabel = if (hasBudget) "Available to spend" else "Spent this month"
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary), shape = RoundedCornerShape(26.dp)) {
        Column(Modifier.padding(22.dp)) {
            Text(primaryLabel, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .78f))
            Text(money(primaryAmount), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.ExtraBold)
            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BalanceMetric("SPENT", money(expenses), Color(0xFFFFC78F), Modifier.weight(1f))
                if (hasBudget) BalanceMetric("MONTHLY BUDGET", money(budget!!), Color(0xFFB3F5E8), Modifier.weight(1f))
            }
        }
    }
}

@Composable private fun BalanceMetric(label: String, amount: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .11f)).padding(12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        Text(amount, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun QuickOverview(categories: List<Pair<String, Double>>, expenses: Double, count: Int, onAnalytics: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onAnalytics), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            if (categories.isEmpty()) Box(Modifier.size(84.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) { Text("₹", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary) }
            else Donut(categories.take(4).map { it.second }, chartColors, Modifier.size(84.dp))
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(if (categories.isEmpty()) "Your insights will appear here" else "${categories.first().first} is your top spend", fontWeight = FontWeight.ExtraBold)
                Text(if (categories.isEmpty()) "Add an expense to see a simple category snapshot." else "${money(expenses)} across $count transactions", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text("Open analytics ›", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun AnalyticsContent(modifier: Modifier, month: Int, categoryTotals: List<Pair<String, Double>>, expenses: Double, transactionCount: Int, onBack: () -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { ScreenHeader("Analytics", "${Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())} spending", onBack) }
        if (categoryTotals.isEmpty()) item { EmptyState("Add expenses to generate your spending analysis.") }
        else {
            item { CategoryBreakdown(categoryTotals.take(6), expenses) }
            item { StatCard("This month", money(expenses), "$transactionCount transactions recorded") }
            item { Text("Tip: set category limits from your budget card to get alerts before overspending.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 24.dp)) }
        }
    }
}

@Composable
private fun LedgerContent(modifier: Modifier, transactions: List<TransactionEntity>, totalCount: Int, onBack: () -> Unit, onEdit: (TransactionEntity) -> Unit, onDelete: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = transactions.filter { query.isBlank() || it.description.contains(query, true) || it.category.contains(query, true) }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader("Transactions", "$totalCount total", onBack) }
        item { OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Search, null) }, label = { Text("Search transactions") }, singleLine = true, shape = RoundedCornerShape(16.dp)) }
        if (filtered.isEmpty()) item { EmptyState(if (query.isBlank()) "No transactions this month." else "No matching transactions.") }
        else items(filtered, key = { it.id }) { transaction -> TransactionRow(transaction, onEdit = { onEdit(transaction) }, onDelete = { onDelete(transaction.id) }) }
        item { Box(Modifier.height(70.dp)) }
    }
}

@Composable private fun ScreenHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TextButton(onClick = onBack, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 0.dp)) {
            Text("‹  Back to home", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Column { Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable private fun SectionTitle(title: String, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Button(onClick = onAction, shape = RoundedCornerShape(12.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 9.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary)) { Text(action) }
    }
}

@Composable private fun ProcessingStatus(status: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary); Text(status, modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable private fun CompactTransactionRow(transaction: TransactionEntity, onEdit: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onEdit), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            CategoryAvatar(transaction)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(transaction.description.ifBlank { "Transaction" }, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${transaction.category} • ${if (transaction.source == "sms_ai") "AI categorized" else "Manual"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Text(money(transaction.amount), fontWeight = FontWeight.ExtraBold, color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable private fun TransactionRow(transaction: TransactionEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.clickable(onClick = onEdit).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            CategoryAvatar(transaction)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(transaction.description.ifBlank { "Transaction" }, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${transaction.category} • ${if (transaction.source == "sms_ai") "AI categorized" else "Manual"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Column(horizontalAlignment = Alignment.End) { Text(money(transaction.amount), fontWeight = FontWeight.ExtraBold, color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary); IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Delete, "Delete transaction", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) } }
        }
    }
}

@Composable private fun CategoryAvatar(transaction: TransactionEntity) { Box(Modifier.size(42.dp).clip(CircleShape).background(if (transaction.isExpense) MaterialTheme.colorScheme.primaryContainer else Color(0xFFE5EEFF)), contentAlignment = Alignment.Center) { Text(transaction.category.take(1).uppercase(), fontWeight = FontWeight.ExtraBold, color = if (transaction.isExpense) MaterialTheme.colorScheme.primary else PulseBlue) } }

@Composable private fun CategoryBreakdown(categories: List<Pair<String, Double>>, total: Double) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Donut(categories.map { it.second }, chartColors, Modifier.size(96.dp)); Column(Modifier.padding(start = 16.dp)) { Text("Spending mix", fontWeight = FontWeight.ExtraBold); Text("${money(total)} this month", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) } }
            categories.forEachIndexed { index, (name, amount) -> CategoryProgress(name, amount, total, chartColors[index % chartColors.size]) }
        }
    }
}

@Composable private fun CategoryProgress(name: String, amount: Double, total: Double, color: Color) {
    val fraction = if (total > 0) (amount / total).toFloat() else 0f
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(name, fontWeight = FontWeight.SemiBold); Text("${money(amount)} · ${(fraction * 100).toInt()}%", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
        Box(Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) { Box(Modifier.fillMaxWidth(fraction).height(7.dp).clip(CircleShape).background(color)) }
    }
}

@Composable private fun StatCard(label: String, value: String, detail: String) { Card(shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold); Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) } } }

@Composable private fun Donut(values: List<Double>, colors: List<Color>, modifier: Modifier) { val total = values.sum().takeIf { it > 0 } ?: 1.0; Canvas(modifier) { var start = -90f; values.forEachIndexed { index, value -> val sweep = (value / total * 360f).toFloat(); drawArc(colors[index % colors.size], start, sweep, false, style = Stroke(width = size.minDimension * .20f, cap = StrokeCap.Butt)); start += sweep } } }

@Composable private fun BudgetDialog(onDismiss: () -> Unit, onSave: (Double) -> Unit) { var value by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Set your monthly budget") }, text = { OutlinedTextField(value, { value = it }, label = { Text("Budget in rupees") }, singleLine = true) }, confirmButton = { TextButton(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let(onSave) }) { Text("Save budget") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }) }
@Composable private fun CategoryBudgetDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) { var category by remember { mutableStateOf("") }; var value by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Category budget") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(category, { category = it }, label = { Text("Category") }); OutlinedTextField(value, { value = it }, label = { Text("Monthly limit in rupees") }) } }, confirmButton = { TextButton(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let { if (category.isNotBlank()) onSave(category.trim(), it) } }) { Text("Save limit") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }) }

@Composable fun MonthSelector(currentMonth: Int, onMonthChange: (Int) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { TextButton(onClick = { onMonthChange(if (currentMonth > 1) currentMonth - 1 else 12) }) { Text("‹ Previous") }; Text(Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()), fontWeight = FontWeight.Bold); TextButton(onClick = { onMonthChange(if (currentMonth < 12) currentMonth + 1 else 1) }) { Text("Next ›") } } }

private val chartColors = listOf(PulseTeal, PulseBlue, PulseOrange, Color(0xFF8C6DD7), Color(0xFFDD6D91))
private fun money(amount: Double): String = "₹${"%.0f".format(Locale("en", "IN"), amount)}"
private fun shareCsv(context: Context, csv: String) { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/csv"; putExtra(Intent.EXTRA_STREAM, writeCsvToCache(context, csv)); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }, "Export transactions")) }
fun writeCsvToCache(context: Context, content: String): Uri { val file = File(context.cacheDir, "transactions_${System.currentTimeMillis()}.csv"); file.writeText(content); return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file) }
