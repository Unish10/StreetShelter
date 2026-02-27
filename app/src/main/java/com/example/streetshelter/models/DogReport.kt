package com.example.streetshelter.models

data class DogReport(
    val id: String = "",
    val reporterId: String = "",
    val reporterEmail: String = "",
    val location: String = "",
    val dogType: String = "", // Small Dog 🐕, Medium Dog 🐶, Big Dog 🐩, Injured/Sick, Stray
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING, RESCUED, CANCELLED
)

