package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.entity.User
import com.example.myapplication.preference.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUserId: Long = -1,
    val currentUsername: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val prefs = UserPreferences(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkAutoLogin()
    }

    /**
     * 检查是否有自动登录用户
     */
    private fun checkAutoLogin() {
        viewModelScope.launch {
            if (prefs.isAutoLogin() && prefs.isLoggedIn()) {
                val userId = prefs.getUserId()
                val user = userDao.getUserById(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        currentUserId = user.id,
                        currentUsername = user.username
                    )
                } else {
                    prefs.clearLogin()
                }
            }
        }
    }

    /**
     * 用户登录
     */
    fun login(username: String, password: String, rememberPassword: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                if (username.isBlank() || password.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "用户名和密码不能为空"
                    )
                    return@launch
                }
                val user = userDao.login(username, password)
                if (user != null) {
                    // 更新自动登录状态
                    userDao.setAutoLogin(user.id, rememberPassword)
                    prefs.saveLoginUser(user.id, user.username, rememberPassword)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUserId = user.id,
                        currentUsername = user.username,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "用户名或密码错误"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "登录失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 用户注册
     */
    fun register(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                if (username.isBlank() || password.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "用户名和密码不能为空"
                    )
                    return@launch
                }
                if (password != confirmPassword) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "两次输入的密码不一致"
                    )
                    return@launch
                }
                if (password.length < 6) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "密码长度不能少于6位"
                    )
                    return@launch
                }
                // 检查用户名是否已存在
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "用户名已存在，请更换"
                    )
                    return@launch
                }
                // 插入新用户
                val newUser = User(username = username, password = password)
                val userId = userDao.insertUser(newUser)

                // 初始化默认分类
                initDefaultCategories(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "注册成功，请登录",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "注册失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 初始化默认分类（收入 + 支出）
     */
    private suspend fun initDefaultCategories(userId: Long) {
        val categoryDao = AppDatabase.getDatabase(getApplication()).categoryDao()
        val defaultIncome = listOf("工资", "奖金", "兼职", "利息", "投资收益", "其他收入")
        val defaultExpense = listOf("娱乐", "教育", "交通", "购物", "生活用品", "餐饮", "医疗", "其他支出")

        defaultIncome.forEach { name ->
            categoryDao.insertCategory(
                com.example.myapplication.data.entity.Category(
                    name = name,
                    type = "收入",
                    userId = userId
                )
            )
        }
        defaultExpense.forEach { name ->
            categoryDao.insertCategory(
                com.example.myapplication.data.entity.Category(
                    name = name,
                    type = "支出",
                    userId = userId
                )
            )
        }
    }

    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            prefs.clearLogin()
            // 清除数据库中的自动登录标记
            val userId = _uiState.value.currentUserId
            if (userId > 0) {
                userDao.setAutoLogin(userId, false)
            }
            _uiState.value = LoginUiState()
        }
    }

    /**
     * 清除消息
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
