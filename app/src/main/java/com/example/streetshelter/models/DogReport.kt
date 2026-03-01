package com.example.streetshelter.models

data class DogReport(
    val id: String = "",
    val reporterId: String = "",
    val reporterEmail: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val dogType: String = "",
    val category: String = "STRAY",
    val priority: String = "MEDIUM",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING"
)

