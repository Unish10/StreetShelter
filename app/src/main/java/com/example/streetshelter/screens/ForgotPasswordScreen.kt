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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streetshelter.AuthManager
import com.example.streetshelter.R
import com.example.streetshelter.ui.theme.StreetShelterTheme

@Composable
fun ForgotPasswordScreen(authManager: AuthManager, onSubmit: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<Pair<String, Color>?>(null) }

    StreetShelterTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val primaryColor = MaterialTheme.colorScheme.primary
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
                    text = "Forgot Your Password?",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("forgot_password_title")
                )
                Text(
                    text = "Enter your email to reset your password",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth().testTag("reset_email_field")
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        authManager.forgotPassword(email) { success, errorMessage ->
                            message = if (success) {
                                Pair("Password reset email sent successfully!", primaryColor)
                            } else {
                                Pair(errorMessage ?: "Failed to send reset email.", errorColor)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("reset_submit_button")
                ) {
                    Text("Submit")
                }
                Spacer(modifier = Modifier.height(16.dp))
                message?.let {
                    Text(
                        text = it.first,
                        color = it.second,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().testTag("reset_message")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onSubmit() }, modifier = Modifier.testTag("back_to_login_button")) {
                    Text("Back to Login")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    StreetShelterTheme {
        ForgotPasswordScreen(AuthManager(), onSubmit = {})
    }
}
