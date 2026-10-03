package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostic_history")
data class DiagnosticRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceModel: String,
    val imei: String,
    val healthScore: Int,
    val conditionGrade: String = "A+",
    val touchGridScore: Int = 48, // out of 48
    val cameraStatus: String = "PASS", // PASS or WARNING
    val flashStatus: String = "PASS",
    val audioStatus: String = "PASS",
    val micStatus: String = "PASS",
    val batteryCapacity: Int = 88,
    val batteryCycles: Int = 340,
    val batteryStatus: String = "NORMAL",
    val defectsSummary: String = "All hardware sensors certified",
    val technicianNotes: String = "OEM certified. Offline diagnostic passed.",
    val timestamp: Long = System.currentTimeMillis()
)
