package com.example.streetshelter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.streetshelter.models.UserRole
import com.example.streetshelter.screens.ForgotPasswordScreen
import com.example.streetshelter.screens.LoginScreen
import com.example.streetshelter.screens.OwnerDashboardScreen
import com.example.streetshelter.screens.RegisterScreen
import com.example.streetshelter.screens.ReporterDashboardScreen
import com.example.streetshelter.screens.RoleSelectionScreen
import com.example.streetshelter.screens.SplashScreen
import com.example.streetshelter.ui.theme.StreetShelterTheme

class MainActivity : ComponentActivity() {
    private val authManager = AuthManager()
    private val reportManager = ReportManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreetShelterTheme {
                AppNavigation(authManager, reportManager)
            }
        }
    }
}

@Composable
fun AppNavigation(authManager: AuthManager, reportManager: ReportManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } })
        }
        composable("login") {
            LoginScreen(
                authManager = authManager,
                onRegisterClick = { navController.navigate("role_selection") },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onLoginSuccess = {
                    authManager.getUserRole { role, error ->
                        when (role) {
                            UserRole.REPORTER -> navController.navigate("reporter_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                            UserRole.OWNER -> navController.navigate("owner_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                            null -> {
                                // Default fallback if no role is found
                                navController.navigate("reporter_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                }
            )
        }
        composable("role_selection") {
            RoleSelectionScreen(
                onRoleSelected = { selectedRole ->
                    navController.navigate("register/$selectedRole")
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("register/{role}") { backStackEntry ->
            val roleString = backStackEntry.arguments?.getString("role") ?: "REPORTER"
            val selectedRole = try {
                UserRole.valueOf(roleString)
            } catch (e: Exception) {
                UserRole.REPORTER
            }
            RegisterScreen(
                authManager = authManager,
                selectedRole = selectedRole,
                onLoginClick = { navController.navigate("login") { popUpTo("role_selection") { inclusive = true } } },
                onRegisterSuccess = { navController.navigate("login") { popUpTo("role_selection") { inclusive = true } } }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                authManager = authManager,
                onSubmit = { navController.popBackStack() }
            )
        }
        composable("reporter_dashboard") {
            ReporterDashboardScreen(
                authManager = authManager,
                reportManager = reportManager,
                onLogout = { navController.navigate("login") { popUpTo("reporter_dashboard") { inclusive = true } } }
            )
        }
        composable("owner_dashboard") {
            OwnerDashboardScreen(
                authManager = authManager,
                reportManager = reportManager,
                onLogout = { navController.navigate("login") { popUpTo("owner_dashboard") { inclusive = true } } }
            )
        }
    }
}
