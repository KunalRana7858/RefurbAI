package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val imei: String,
    val storage: String = "128GB",
    val color: String = "Midnight Black",
    val conditionGrade: String = "A+", // A+, A, B, C
    val purchasePrice: Double,
    val repairCost: Double = 0.0,
    val targetSalePrice: Double,
    val status: String = "IN_STOCK", // IN_STOCK, IN_REPAIR, SOLD, CERTIFIED
    val healthScore: Int = 92,
    val defectsSummary: String = "None. Verified Clean.",
    val customerName: String = "Walking Customer",
    val customerPhone: String = "+91 98123 45678",
    val warrantyType: String = "1-Month Shop Warranty",
    val ceirStatus: String = "CLEAN",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val notes: String = "",
    val utmSource: String = "organic"
)
