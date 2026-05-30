package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.entity.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = AppDatabase.getDatabase(application).categoryDao()
    private var userId: Long = -1

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    /**
     * 设置当前用户并加载分类
     */
    fun setUserId(id: Long) {
        userId = id
        loadCategories()
    }

    /**
     * 加载当前用户的所有分类
     */
    private fun loadCategories() {
        if (userId <= 0) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                categoryDao.getCategoriesByUserId(userId).collect { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载分类失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 添加分类
     */
    fun addCategory(name: String, type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                if (name.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "分类名称不能为空"
                    )
                    return@launch
                }
                // 检查是否已存在同名同类型
                val existing = categoryDao.getCategoryByNameAndType(userId, name, type)
                if (existing != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "该分类已存在"
                    )
                    return@launch
                }
                categoryDao.insertCategory(
                    Category(name = name, type = type, userId = userId)
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "添加成功"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "添加失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 更新分类名称
     */
    fun updateCategory(category: Category, newName: String) {
        viewModelScope.launch {
            try {
                if (newName.isBlank()) return@launch
                categoryDao.updateCategory(category.copy(name = newName))
                _uiState.value = _uiState.value.copy(successMessage = "修改成功")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "修改失败: ${e.message}")
            }
        }
    }

    /**
     * 删除分类
     */
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryDao.deleteCategory(category)
                _uiState.value = _uiState.value.copy(successMessage = "删除成功")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "删除失败: ${e.message}")
            }
        }
    }

    /**
     * 清除消息
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
