package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.entity.Record
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RecordUiState(
    val records: List<Record> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val filterType: String? = null // null = all, "收入" or "支出"
)

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val recordDao = AppDatabase.getDatabase(application).recordDao()
    private var userId: Long = -1

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    /**
     * 设置当前用户并加载记录
     */
    fun setUserId(id: Long) {
        userId = id
        loadRecords()
        loadTotals()
    }

    /**
     * 加载记录列表
     */
    private fun loadRecords() {
        if (userId <= 0) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val flow = if (_uiState.value.filterType != null) {
                    recordDao.getRecordsByUserIdAndType(userId, _uiState.value.filterType!!)
                } else {
                    recordDao.getRecordsByUserId(userId)
                }
                flow.collect { records ->
                    _uiState.value = _uiState.value.copy(
                        records = records,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载记录失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 加载收支总计
     */
    private fun loadTotals() {
        if (userId <= 0) return
        viewModelScope.launch {
            try {
                recordDao.getTotalByUserIdAndType(userId, "收入").collect { total ->
                    _uiState.value = _uiState.value.copy(totalIncome = total ?: 0.0)
                }
            } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try {
                recordDao.getTotalByUserIdAndType(userId, "支出").collect { total ->
                    _uiState.value = _uiState.value.copy(totalExpense = total ?: 0.0)
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * 设置筛选类型
     */
    fun setFilterType(type: String?) {
        _uiState.value = _uiState.value.copy(filterType = type)
        loadRecords()
    }

    /**
     * 添加记录
     */
    fun addRecord(
        amount: Double,
        type: String,
        categoryId: Long,
        note: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                if (amount <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "金额必须大于0"
                    )
                    return@launch
                }
                val record = Record(
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    userId = userId,
                    note = note,
                    date = date
                )
                recordDao.insertRecord(record)
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
     * 更新记录
     */
    fun updateRecord(record: Record) {
        viewModelScope.launch {
            try {
                recordDao.updateRecord(record)
                _uiState.value = _uiState.value.copy(successMessage = "修改成功")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "修改失败: ${e.message}")
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            try {
                recordDao.deleteRecord(record)
                _uiState.value = _uiState.value.copy(successMessage = "删除成功")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "删除失败: ${e.message}")
            }
        }
    }

    /**
     * 获取单条记录（用于编辑）
     */
    suspend fun getRecord(id: Long): Record? {
        return recordDao.getRecordById(id)
    }

    /**
     * 清除消息
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
