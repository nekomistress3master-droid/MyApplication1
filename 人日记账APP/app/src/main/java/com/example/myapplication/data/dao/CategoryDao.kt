package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId ORDER BY type DESC, name ASC")
    fun getCategoriesByUserId(userId: Long): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type ORDER BY name ASC")
    fun getCategoriesByUserIdAndType(userId: Long, type: String): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): Category?

    @Query("SELECT * FROM categories WHERE user_id = :userId AND name = :name AND type = :type LIMIT 1")
    suspend fun getCategoryByNameAndType(userId: Long, name: String, type: String): Category?
}
