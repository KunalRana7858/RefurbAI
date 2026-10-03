package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY createdAt DESC")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE status = :status ORDER BY createdAt DESC")
    fun getInventoryByStatus(status: String): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory WHERE id = :id LIMIT 1")
    suspend fun getInventoryById(id: Long): InventoryItem?

    @Query("SELECT COUNT(*) FROM inventory WHERE status != 'SOLD'")
    fun getActiveStockCount(): Flow<Int>

    @Query("SELECT SUM(purchasePrice + repairCost) FROM inventory WHERE status != 'SOLD'")
    fun getTotalCapitalLocked(): Flow<Double?>

    @Query("SELECT SUM(targetSalePrice - (purchasePrice + repairCost)) FROM inventory WHERE status = 'SOLD'")
    fun getTotalRealizedProfit(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM inventory WHERE status = 'IN_REPAIR'")
    fun getUnitsInRepairCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItem>)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)
}
