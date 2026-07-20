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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlin.math.max

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.transactions.collectAsStateWithLifecycle()
    val month by viewModel.currentMonth.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val smsStatus by remember(context) { SmsStatusStore.messages(context) }
        .collectAsStateWithLifecycle(initialValue = SmsStatusStore.get(context))
    var screen by remember { mutableStateOf(Screen.Dashboard) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCategoryBudgetDialog by remember { mutableStateOf(false) }
    var budget by remember { mutableStateOf(BudgetPreferences.get(context)) }

    if (screen == Screen.Settings) {
        SettingsScreen(onBack = { screen = Screen.Dashboard })
        return
    }
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val expenses = transactions.filter { it.isExpense }.sumOf { it.amount }
    val income = transactions.filterNot { it.isExpense }.sumOf { it.amount }
    val categoryTotals = transactions.filter { it.isExpense }.groupBy { it.category }
        .mapValues { (_, values) -> values.sumOf { it.amount } }
        .toList().sortedByDescending { it.second }
    val visibleTransactions = transactions.filter {
        searchQuery.isBlank() || it.description.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { DashboardHeader(context, month, viewModel::setMonth, viewModel::exportTransactions, onSettings = { screen = Screen.Settings }) }
            smsStatus?.let { status -> item { ProcessingStatus(status) } }
            item { BalanceCard(income = income, expenses = expenses) }
            item {
                BudgetSummary(
                    spent = expenses,
                    budget = budget,
                    onSetBudget = { showBudgetDialog = true },
                    onSetCategoryBudget = { showCategoryBudgetDialog = true }
                )
            }
            item { SectionTitle("This month’s picture", "Live from your ledger") }
            item { InsightStrip(income, expenses, transactions.size) }
            item { SectionTitle("Spending by category", "See all") }
            item {
                if (categoryTotals.isEmpty()) EmptyState("Add an expense to unlock your spending insights.")
                else CategoryBreakdown(categoryTotals.take(5), expenses)
            }
            item { SectionTitle("Recent transactions", "${allTransactions.size} total") }
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Search transactions") },
                    placeholder = { Text("Merchant, category, or amount") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            if (visibleTransactions.isEmpty()) item {
                EmptyState(if (searchQuery.isBlank()) "No transactions recorded in ${Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())}." else "No matching transactions.")
            } else items(visibleTransactions, key = { it.id }) { transaction ->
                TransactionRow(transaction, onEdit = { editingTransaction = transaction }, onDelete = { viewModel.deleteTransaction(transaction.id) })
            }
        }
    }
    if (showAddDialog) AddTransactionDialog(onDismiss = { showAddDialog = false }, onConfirm = { amount, description, category, isExpense ->
        viewModel.addTransaction(amount, description, category, isExpense, System.currentTimeMillis())
        showAddDialog = false
    })
    editingTransaction?.let { existing ->
        AddTransactionDialog(existing = existing, onDismiss = { editingTransaction = null }, onConfirm = { amount, description, category, isExpense ->
            viewModel.updateTransaction(existing.copy(amount = amount, description = description, category = category, isExpense = isExpense))
            editingTransaction = null
        })
    }
    if (showBudgetDialog) BudgetDialog(onDismiss = { showBudgetDialog = false }) { value ->
        BudgetPreferences.save(context, value); budget = value; showBudgetDialog = false
    }
    if (showCategoryBudgetDialog) CategoryBudgetDialog(onDismiss = { showCategoryBudgetDialog = false }) { category, value ->
        BudgetPreferences.saveCategoryLimit(context, category, value); showCategoryBudgetDialog = false
    }
}

private enum class Screen { Dashboard, Settings }

@Composable
private fun DashboardHeader(context: Context, month: Int, onMonthChange: (Int) -> Unit, onExport: ((String) -> Unit) -> Unit, onSettings: () -> Unit) {
    Column(Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PaisaPulse", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Text("A calmer way to manage money", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {
                onExport { csv ->
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, writeCsvToCache(context, csv))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }, "Export transactions"))
                }
            }) { Icon(Icons.Default.Share, "Export transactions") }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings") }
        }
        MonthSelector(month, onMonthChange)
    }
}

@Composable
private fun ProcessingStatus(status: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
            Text(status, modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BalanceCard(income: Double, expenses: Double) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(Modifier.padding(22.dp)) {
            Text("Available this month", color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .78f))
            Text(money(income - expenses), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.ExtraBold)
            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BalanceMetric("INCOME", money(income), Color(0xFFB3F5E8), Modifier.weight(1f))
                BalanceMetric("SPENT", money(expenses), Color(0xFFFFC78F), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BalanceMetric(label: String, amount: String, accent: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .11f)).padding(12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Bold)
        Text(amount, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InsightStrip(income: Double, expenses: Double, count: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        InsightCard("Savings rate", "${if (income > 0) ((income - expenses) / income * 100).toInt().coerceAtLeast(0) else 0}%", Modifier.weight(1f))
        InsightCard("Transactions", count.toString(), Modifier.weight(1f))
        InsightCard("Top spend", if (expenses > 0) money(expenses / max(count, 1)) else "—", Modifier.weight(1f))
    }
}

@Composable
private fun InsightCard(label: String, value: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CategoryBreakdown(categories: List<Pair<String, Double>>, total: Double) {
    val colors = listOf(PulseTeal, PulseBlue, PulseOrange, Color(0xFF8C6DD7), Color(0xFFDD6D91))
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Donut(categories.map { it.second }, colors, Modifier.size(92.dp))
                Column(Modifier.padding(start = 16.dp)) {
                    Text("${categories.size} active categories", fontWeight = FontWeight.Bold)
                    Text("${money(total)} tracked this month", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            categories.forEachIndexed { index, (name, amount) ->
                val fraction = if (total > 0) amount / total else 0.0
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, fontWeight = FontWeight.SemiBold)
                        Text("${money(amount)} · ${(fraction * 100).toInt()}%", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    Box(Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(Modifier.fillMaxWidth(fraction.toFloat()).height(7.dp).clip(CircleShape).background(colors[index % colors.size]))
                    }
                }
            }
        }
    }
}

@Composable
private fun Donut(values: List<Double>, colors: List<Color>, modifier: Modifier) {
    val total = values.sum().takeIf { it > 0 } ?: 1.0
    Canvas(modifier) {
        var start = -90f
        values.forEachIndexed { index, value ->
            val sweep = (value / total * 360f).toFloat()
            drawArc(colors[index % colors.size], start, sweep, false, style = Stroke(width = size.minDimension * .20f, cap = StrokeCap.Butt))
            start += sweep
        }
    }
}

@Composable
private fun TransactionRow(transaction: TransactionEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(if (transaction.isExpense) MaterialTheme.colorScheme.primaryContainer else Color(0xFFE5EEFF)), contentAlignment = Alignment.Center) {
                Text(transaction.category.take(1).uppercase(), fontWeight = FontWeight.ExtraBold, color = if (transaction.isExpense) MaterialTheme.colorScheme.primary else PulseBlue)
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(transaction.description.ifBlank { "Transaction" }, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${transaction.category} • ${if (transaction.source == "sms_ai") "AI categorized" else "Manual"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${if (transaction.isExpense) "−" else "+"}${money(transaction.amount)}", fontWeight = FontWeight.ExtraBold, color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Delete, "Delete transaction", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(trailing, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun BudgetDialog(onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Set your monthly budget") }, text = { OutlinedTextField(value, { value = it }, label = { Text("Budget in rupees") }, singleLine = true) }, confirmButton = { TextButton(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let(onSave) }) { Text("Save budget") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun CategoryBudgetDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var category by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Category budget") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(category, { category = it }, label = { Text("Category") }); OutlinedTextField(value, { value = it }, label = { Text("Monthly limit in rupees") }) } }, confirmButton = { TextButton(onClick = { value.toDoubleOrNull()?.takeIf { it > 0 }?.let { if (category.isNotBlank()) onSave(category.trim(), it) } }) { Text("Save limit") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
fun MonthSelector(currentMonth: Int, onMonthChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = { onMonthChange(if (currentMonth > 1) currentMonth - 1 else 12) }) { Text("‹ Previous") }
        Text(Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()), fontWeight = FontWeight.Bold)
        TextButton(onClick = { onMonthChange(if (currentMonth < 12) currentMonth + 1 else 1) }) { Text("Next ›") }
    }
}

private fun money(amount: Double): String = "₹${"%.0f".format(Locale("en", "IN"), amount)}"

fun writeCsvToCache(context: Context, content: String): Uri {
    val file = File(context.cacheDir, "transactions_${System.currentTimeMillis()}.csv")
    file.writeText(content)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
