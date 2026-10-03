package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DiagnosticRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
    @Query("SELECT * FROM diagnostic_history ORDER BY timestamp DESC")
    fun getAllDiagnostics(): Flow<List<DiagnosticRecord>>

    @Query("SELECT * FROM diagnostic_history WHERE imei = :imei ORDER BY timestamp DESC")
    fun getDiagnosticsForImei(imei: String): Flow<List<DiagnosticRecord>>

    @Query("SELECT * FROM diagnostic_history WHERE id = :id LIMIT 1")
    suspend fun getDiagnosticById(id: Long): DiagnosticRecord?

    @Query("SELECT COUNT(*) FROM diagnostic_history")
    fun getTotalDiagnosticsCount(): Flow<Int>

    @Query("SELECT AVG(healthScore) FROM diagnostic_history")
    fun getAverageHealthScore(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnostic(record: DiagnosticRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DiagnosticRecord>)

    @Delete
    suspend fun deleteDiagnostic(record: DiagnosticRecord)

    @Query("DELETE FROM diagnostic_history")
    suspend fun clearAll()
}
