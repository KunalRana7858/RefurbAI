package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val shopName: String = "RefurbIQ Store",
    val ownerName: String = "Rana Kunal",
    val phone: String = "+91 98765 43210",
    val gstNumber: String = "07AAAAA0000A1Z5",
    val shopAddress: String = "Connaught Place, New Delhi",
    val role: String = "SELLER", // "CONSUMER", "SELLER", "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)
