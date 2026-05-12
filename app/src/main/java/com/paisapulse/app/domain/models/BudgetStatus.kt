package com.paisapulse.app.domain.models

data class BudgetStatus(
    val category: String,
    val spentAmount: Double,
    val limitAmount: Double,
    val percentUsed: Int,
    val isOverThreshold: Boolean
)
