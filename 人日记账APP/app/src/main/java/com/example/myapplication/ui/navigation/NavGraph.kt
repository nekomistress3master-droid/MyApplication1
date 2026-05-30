package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screen.*
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.RecordViewModel

/**
 * 导航路由常量
 */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val ADD_RECORD = "add_record"
    const val EDIT_RECORD = "edit_record/{recordId}"
    const val CATEGORY_MANAGE = "category_manage"
    const val RECORD_LIST = "record_list"

    fun editRecord(recordId: Long) = "edit_record/$recordId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    startDestination: String
) {
    // 共享的ViewModels
    val recordViewModel: RecordViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 登录页面
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 注册页面
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = loginViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 主页（仪表盘）
        composable(Routes.HOME) {
            val loginState = loginViewModel.uiState

            HomeScreen(
                loginViewModel = loginViewModel,
                recordViewModel = recordViewModel,
                onAddRecord = {
                    navController.navigate(Routes.ADD_RECORD)
                },
                onManageCategories = {
                    navController.navigate(Routes.CATEGORY_MANAGE)
                },
                onViewAllRecords = {
                    navController.navigate(Routes.RECORD_LIST)
                },
                onRecordClick = { recordId ->
                    navController.navigate(Routes.editRecord(recordId))
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 添加记录
        composable(Routes.ADD_RECORD) {
            val loginState = loginViewModel.uiState
            val userId = loginState.value.currentUserId

            AddRecordScreen(
                recordViewModel = recordViewModel,
                categoryViewModel = categoryViewModel,
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 编辑记录
        composable(
            route = Routes.EDIT_RECORD,
            arguments = listOf(
                navArgument("recordId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            val loginState = loginViewModel.uiState
            val userId = loginState.value.currentUserId

            AddRecordScreen(
                recordViewModel = recordViewModel,
                categoryViewModel = categoryViewModel,
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                editRecordId = recordId
            )
        }

        // 分类管理
        composable(Routes.CATEGORY_MANAGE) {
            val loginState = loginViewModel.uiState
            val userId = loginState.value.currentUserId

            CategoryManageScreen(
                viewModel = categoryViewModel,
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 全部记录
        composable(Routes.RECORD_LIST) {
            val loginState = loginViewModel.uiState
            val userId = loginState.value.currentUserId

            RecordListScreen(
                viewModel = recordViewModel,
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRecordClick = { recordId ->
                    navController.navigate(Routes.editRecord(recordId))
                }
            )
        }
    }
}
