package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE is_auto_login = 1 LIMIT 1")
    suspend fun getAutoLoginUser(): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET is_auto_login = :isAutoLogin WHERE id = :userId")
    suspend fun setAutoLogin(userId: Long, isAutoLogin: Boolean)

    @Query("UPDATE users SET is_auto_login = 0")
    suspend fun clearAllAutoLogin()
}
