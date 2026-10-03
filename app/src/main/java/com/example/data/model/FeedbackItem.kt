package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_feedback")
data class FeedbackItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inventoryId: Long? = null,
    val customerName: String,
    val customerPhone: String = "",
    val deviceModel: String,
    val serviceType: String = "Sale", // "Sale" or "Repair"
    val rating: Int = 5, // 1 to 5 stars
    val reviewText: String,
    val tags: String = "Fast Service, Honest Pricing",
    val createdAt: Long = System.currentTimeMillis()
)
