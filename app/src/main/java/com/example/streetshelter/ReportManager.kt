package com.example.streetshelter

import android.util.Log
import com.example.streetshelter.models.DogReport
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class ReportManager {
    private val database = FirebaseDatabase.getInstance()
    private val reportsRef = database.reference.child("reports")

    fun submitReport(
        reporterId: String,
        reporterEmail: String,
        location: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        dogType: String,
        category: String,
        priority: String,
        description: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val reportId = UUID.randomUUID().toString()

        Log.d("ReportManager", "Creating report: $reportId")
        Log.d("ReportManager", "Location: $location, Type: $dogType, Category: $category, Priority: $priority")

        val report = DogReport(
            id = reportId,
            reporterId = reporterId,
            reporterEmail = reporterEmail,
            location = location,
            latitude = latitude,
            longitude = longitude,
            dogType = dogType,
            category = category,
            priority = priority,
            description = description,
            timestamp = System.currentTimeMillis(),
            status = "PENDING"
        )

        reportsRef.child(reportId).setValue(report)
            .addOnSuccessListener {
                Log.d("ReportManager", "Report saved successfully")
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("ReportManager", "Failed to save report: ${exception.message}")
                onComplete(false, "Failed to save report: ${exception.message}")
            }
    }

    fun getAllReports(onComplete: (List<DogReport>?, String?) -> Unit) {
        Log.d("ReportManager", "Fetching all reports")

        reportsRef.get()
            .addOnSuccessListener { snapshot ->
                val reports = mutableListOf<DogReport>()
                Log.d("ReportManager", "Query successful, found ${snapshot.childrenCount} total reports")

                for (child in snapshot.children) {
                    try {
                        val report = child.getValue(DogReport::class.java)
                        if (report != null) {
                            reports.add(report)
                            Log.d("ReportManager", "Loaded report: ${report.id} by ${report.reporterEmail}")
                        }
                    } catch (e: Exception) {
                        Log.e("ReportManager", "Error parsing report: ${e.message}")
                    }
                }

                Log.d("ReportManager", "Successfully loaded ${reports.size} reports")
                onComplete(reports.sortedByDescending { it.timestamp }, null)
            }
            .addOnFailureListener { exception ->
                Log.e("ReportManager", "Failed to fetch all reports: ${exception.message}", exception)
                onComplete(null, exception.message)
            }
    }

    fun updateReportStatus(
        reportId: String,
        newStatus: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        reportsRef.child(reportId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    fun getMyReports(reporterId: String, onComplete: (List<DogReport>?, String?) -> Unit) {
        Log.d("ReportManager", "Fetching reports for user: $reporterId")

        reportsRef.orderByChild("reporterId").equalTo(reporterId).get()
            .addOnSuccessListener { snapshot ->
                val reports = mutableListOf<DogReport>()
                Log.d("ReportManager", "Query successful, found ${snapshot.childrenCount} reports")

                for (child in snapshot.children) {
                    try {
                        val report = child.getValue(DogReport::class.java)
                        if (report != null) {
                            reports.add(report)
                            Log.d("ReportManager", "Loaded report: ${report.id}")
                        }
                    } catch (e: Exception) {
                        Log.e("ReportManager", "Error parsing report: ${e.message}")
                    }
                }

                Log.d("ReportManager", "Successfully loaded ${reports.size} reports")
                onComplete(reports.sortedByDescending { it.timestamp }, null)
            }
            .addOnFailureListener { exception ->
                Log.e("ReportManager", "Failed to fetch reports: ${exception.message}", exception)
                onComplete(null, exception.message)
            }
    }
}

