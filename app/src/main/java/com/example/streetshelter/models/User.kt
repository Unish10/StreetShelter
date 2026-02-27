package com.example.streetshelter.models

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = UserRole.REPORTER.name
)

