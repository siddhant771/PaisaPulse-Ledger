package com.paisapulse.app.data.repository


import com.paisapulse.app.data.local.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportRepository {
    fun generateCsv(transactions: List<TransactionEntity>): String {
        val header = "Date,Description,Category,Amount,Type\n"
        val rows = transactions.joinToString("\n") {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.date))
            "$date,${it.description},${it.category},${it.amount},${if (it.isExpense) "Expense" else "Income"}"
        }
        return header + rows
    }
}