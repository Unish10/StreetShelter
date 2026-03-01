package com.example.streetshelter

import com.example.streetshelter.models.User
import com.example.streetshelter.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun register(email: String, password: String, role: UserRole, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = User(
                            uid = userId,
                            email = email,
                            role = role.name
                        )
                        database.reference.child("users").child(userId).setValue(user)
                            .addOnSuccessListener {
                                onComplete(true, null)
                            }
                            .addOnFailureListener { exception ->
                                onComplete(false, exception.message)
                            }
                    } else {
                        onComplete(false, "Failed to get user ID")
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun getUserRole(onComplete: (UserRole?, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.reference.child("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val roleStr = snapshot.child("role").getValue(String::class.java)
                    val role = roleStr?.let { 
                        try {
                            UserRole.valueOf(it.uppercase())
                        } catch (e: Exception) {
                            UserRole.REPORTER
                        }
                    }
                    onComplete(role, null)
                }
                .addOnFailureListener { exception ->
                    onComplete(null, exception.message)
                }
        } else {
            onComplete(null, "User not logged in")
        }
    }

    fun getAllUsers(onComplete: (List<User>?, String?) -> Unit) {
        database.reference.child("users").get()
            .addOnSuccessListener { snapshot ->
                val users = mutableListOf<User>()
                snapshot.children.forEach { child ->
                    val uid = child.key ?: ""
                    val email = child.child("email").getValue(String::class.java) ?: "No Email"
                    val role = child.child("role").getValue(String::class.java) ?: UserRole.REPORTER.name
                    
                    val user = User(
                        uid = uid,
                        email = email,
                        role = role
                    )
                    users.add(user)
                }
                onComplete(users, null)
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
    }

    fun deleteUser(userId: String, onComplete: (Boolean, String?) -> Unit) {
        database.reference.child("users").child(userId).removeValue()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun logout() {
        auth.signOut()
    }

    fun forgotPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
}
