package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FeedbackItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM customer_feedback ORDER BY createdAt DESC")
    fun getAllFeedback(): Flow<List<FeedbackItem>>

    @Query("SELECT AVG(rating) FROM customer_feedback")
    fun getAverageRating(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM customer_feedback")
    fun getTotalFeedbackCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(feedbackList: List<FeedbackItem>)
}
