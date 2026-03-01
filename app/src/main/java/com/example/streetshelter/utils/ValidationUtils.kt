package com.example.streetshelter.utils

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun areCredentialsValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}
