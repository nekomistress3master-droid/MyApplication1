package com.example.myapplication.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.entity.Category
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    recordViewModel: RecordViewModel,
    categoryViewModel: CategoryViewModel,
    userId: Long,
    onNavigateBack: () -> Unit,
    editRecordId: Long? = null
) {
    val recordState by recordViewModel.uiState.collectAsState()
    val categoryState by categoryViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 表单状态
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("支出") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // 如果是编辑模式，加载记录
    LaunchedEffect(editRecordId) {
        if (editRecordId != null && editRecordId > 0) {
            val record = recordViewModel.getRecord(editRecordId)
            if (record != null) {
                amount = record.amount.toBigDecimal().stripTrailingZeros().toPlainString()
                selectedType = record.type
                note = record.note
                val cal = Calendar.getInstance()
                cal.timeInMillis = record.date
                selectedDate = cal
                // 加载类别
                val cat = categoryState.categories.find { it.id == record.categoryId }
                selectedCategory = cat
            }
        }
    }

    // 加载分类
    LaunchedEffect(userId) {
        if (userId > 0) {
            categoryViewModel.setUserId(userId)
        }
    }

    val filteredCategories = categoryState.categories.filter { it.type == selectedType }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // 日期选择器
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
    }

    // 时间选择器
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedDate.apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
            },
            selectedDate.get(Calendar.HOUR_OF_DAY),
            selectedDate.get(Calendar.MINUTE),
            true
        )
    }

    // 显示成功消息后返回
    LaunchedEffect(recordState.successMessage) {
        recordState.successMessage?.let {
            kotlinx.coroutines.delay(500)
            recordViewModel.clearMessages()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (editRecordId != null) "编辑记录" else "添加记录",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 收支类型切换
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "类型",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 支出按钮
                        FilterChip(
                            selected = selectedType == "支出",
                            onClick = {
                                selectedType = "支出"
                                selectedCategory = null
                            },
                            label = { Text("支出") },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFCE4EC),
                                selectedLabelColor = Color(0xFFC62828)
                            )
                        )
                        // 收入按钮
                        FilterChip(
                            selected = selectedType == "收入",
                            onClick = {
                                selectedType = "收入"
                                selectedCategory = null
                            },
                            label = { Text("收入") },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE8F5E9),
                                selectedLabelColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                }
            }

            // 金额输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "金额",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入金额") },
                        leadingIcon = {
                            Text(
                                text = "¥",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            // 分类选择
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "分类",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "请选择分类",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Category, contentDescription = null)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            if (filteredCategories.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("暂无分类，请先添加") },
                                    onClick = { categoryDropdownExpanded = false }
                                )
                            } else {
                                filteredCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (category.type == "收入")
                                                        Icons.AutoMirrored.Filled.TrendingUp
                                                    else
                                                        Icons.AutoMirrored.Filled.TrendingDown,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = if (category.type == "收入")
                                                        Color(0xFF2E7D32)
                                                    else
                                                        Color(0xFFC62828)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(category.name)
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 日期时间选择
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "日期时间",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(selectedDate.time),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        OutlinedButton(
                            onClick = { timePickerDialog.show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(selectedDate.time),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // 备注输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "备注",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("添加备注说明（可选）") },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 保存按钮
            Button(
                onClick = {
                    val amountVal = amount.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        // 会通过 LiveData 显示错误
                        recordViewModel.addRecord(0.0, selectedType, -1, "")
                        return@Button
                    }
                    if (selectedCategory == null) {
                        recordViewModel.addRecord(0.0, selectedType, -1, "")
                        return@Button
                    }
                    if (editRecordId != null && editRecordId > 0) {
                        // 编辑模式
                        val updatedRecord = com.example.myapplication.data.entity.Record(
                            id = editRecordId,
                            amount = amountVal,
                            type = selectedType,
                            categoryId = selectedCategory!!.id,
                            userId = userId,
                            note = note,
                            date = selectedDate.timeInMillis
                        )
                        recordViewModel.updateRecord(updatedRecord)
                    } else {
                        // 添加模式
                        recordViewModel.addRecord(
                            amount = amountVal,
                            type = selectedType,
                            categoryId = selectedCategory!!.id,
                            note = note,
                            date = selectedDate.timeInMillis
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !recordState.isLoading
            ) {
                if (recordState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (editRecordId != null) "保存修改" else "保存记录",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 错误Snackbar
        Box(modifier = Modifier.fillMaxSize()) {
            recordState.errorMessage?.let { msg ->
                Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                    Text(msg)
                }
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(3000)
                    recordViewModel.clearMessages()
                }
            }
        }
    }
}
