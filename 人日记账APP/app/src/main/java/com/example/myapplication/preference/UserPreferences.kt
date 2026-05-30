package com.example.myapplication.preference

import android.content.Context
import android.content.SharedPreferences

/**
 * 轻量化存储：使用SharedPreferences保存用户登录状态
 * 实现免密码登录功能
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 保存当前登录用户信息
     */
    fun saveLoginUser(userId: Long, username: String, autoLogin: Boolean) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_AUTO_LOGIN, autoLogin)
            apply()
        }
    }

    /**
     * 获取保存的用户ID
     */
    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1)

    /**
     * 获取保存的用户名
     */
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    /**
     * 是否开启自动登录
     */
    fun isAutoLogin(): Boolean = prefs.getBoolean(KEY_AUTO_LOGIN, false)

    /**
     * 清除登录状态（登出）
     */
    fun clearLogin() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_AUTO_LOGIN)
            apply()
        }
    }

    /**
     * 判断是否有已登录用户
     */
    fun isLoggedIn(): Boolean = getUserId() != -1L

    companion object {
        private const val PREFS_NAME = "personal_ledger_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_AUTO_LOGIN = "auto_login"
    }
}
