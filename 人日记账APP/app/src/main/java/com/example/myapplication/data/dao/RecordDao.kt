package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.entity.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: Record): Long

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT * FROM records WHERE user_id = :userId ORDER BY date DESC")
    fun getRecordsByUserId(userId: Long): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE user_id = :userId AND type = :type ORDER BY date DESC")
    fun getRecordsByUserIdAndType(userId: Long, type: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getRecordsByUserIdAndDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE user_id = :userId AND category_id = :categoryId ORDER BY date DESC")
    fun getRecordsByUserIdAndCategory(userId: Long, categoryId: Long): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): Record?

    @Query("SELECT SUM(amount) FROM records WHERE user_id = :userId AND type = :type")
    fun getTotalByUserIdAndType(userId: Long, type: String): Flow<Double?>

    @Query("SELECT * FROM records WHERE user_id = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentRecords(userId: Long, limit: Int = 10): Flow<List<Record>>
}
