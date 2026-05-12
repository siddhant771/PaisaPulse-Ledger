package com.paisapulse.app.domain.models

/**
 * SDE-3 data model for pie chart and analytics.
 * Groups Room aggregates by category.
 */
data class CategorySum(
    val category: String,
    val total: Double
)