package com.paisapulse.app.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.paisapulse.app.data.local.entities.TransactionEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportProvider {
    fun exportToCSV(transactions: List<TransactionEntity>, context: Context) {
        val fileName = "PaisaPulse_Report_${System.currentTimeMillis()}.csv"
        val content = StringBuilder("Date,Merchant,Amount,Category\n")

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        transactions.forEach {
            val date = formatter.format(Date(it.timestamp))
            content.append("$date,${it.merchant},${it.amount},${it.category}\n")
        }

        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(content.toString())

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share report").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        Toast.makeText(context, "Report exported to Documents", Toast.LENGTH_SHORT).show()
    }
}
