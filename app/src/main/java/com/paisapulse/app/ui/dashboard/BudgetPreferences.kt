package com.paisapulse.app.ui.dashboard

import android.content.Context

object BudgetPreferences {
    private const val FILE = "budget_preferences"
    private const val KEY = "monthly_budget"
    fun get(context: Context): Double? = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, null)?.toDoubleOrNull()
    fun save(context: Context, amount: Double) { context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY, amount.toString()).apply() }
    fun categoryLimit(context: Context, category: String): Double? = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString("category_$category", null)?.toDoubleOrNull()
    fun saveCategoryLimit(context: Context, category: String, amount: Double) { context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString("category_$category", amount.toString()).apply() }
}
