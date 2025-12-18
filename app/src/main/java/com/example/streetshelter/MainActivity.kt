package com.example.streetshelter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.streetshelter.screens.DashboardScreen
import com.example.streetshelter.screens.ForgotPasswordScreen
import com.example.streetshelter.screens.LoginScreen
import com.example.streetshelter.screens.RegisterScreen
import com.example.streetshelter.screens.SplashScreen
import com.example.streetshelter.ui.theme.StreetShelterTheme

class MainActivity : ComponentActivity() {
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetShelterTheme {
                AppNavigation(authManager)
            }
        }
    }
}

@Composable
fun AppNavigation(authManager: AuthManager) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } })
        }
        composable("login") {
            LoginScreen(
                authManager = authManager,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onLoginSuccess = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("register") {
            RegisterScreen(
                authManager = authManager,
                onLoginClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                authManager = authManager,
                onSubmit = { navController.popBackStack() }
            )
        }
        composable("dashboard") {
            DashboardScreen(onLogout = { navController.navigate("login") { popUpTo("dashboard") { inclusive = true } } })
        }
    }
}
