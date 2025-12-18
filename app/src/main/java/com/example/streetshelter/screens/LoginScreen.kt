package com.example.streetshelter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streetshelter.AuthManager
import com.example.streetshelter.R
import com.example.streetshelter.ui.theme.StreetShelterTheme

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<Pair<String, Color>?>(null) }

    StreetShelterTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val errorColor = MaterialTheme.colorScheme.error

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.streetshelterlogo),
                    contentDescription = "Street Shelter Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        authManager.login(email, password) { success, errorMessage ->
                            if (success) {
                                onLoginSuccess()
                            } else {
                                message = Pair(errorMessage ?: "Login failed. Please try again.", errorColor)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(16.dp))
                message?.let {
                    Text(
                        text = it.first,
                        color = it.second,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onForgotPasswordClick() }) {
                    Text("Forgot Password?")
                }
                TextButton(onClick = { onRegisterClick() }) {
                    Text("Don't have an account? Register")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    StreetShelterTheme {
        LoginScreen(AuthManager(), onRegisterClick = {}, onForgotPasswordClick = {}, onLoginSuccess = {})
    }
}
