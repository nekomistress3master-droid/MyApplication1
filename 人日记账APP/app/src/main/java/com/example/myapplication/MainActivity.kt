package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.navigation.AppNavGraph
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val loginViewModel: LoginViewModel = viewModel()
                    val loginState by loginViewModel.uiState.collectAsState()
                    val navController = rememberNavController()

                    // 根据登录状态决定起始页
                    val startDestination = if (loginState.isLoggedIn) {
                        Routes.HOME
                    } else {
                        Routes.LOGIN
                    }

                    AppNavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
