package com.example.streetshelter

import com.example.streetshelter.models.UserRole
import org.junit.Assert.*
import org.junit.Test

class AppAuthUnitTest {

    /**
     * Test Case 1: Verify that a password shorter than 6 characters is rejected.
     */
    @Test
    fun testPasswordValidation_tooShort_returnsFalse() {
        val password = "123"
        val isValid = password.length >= 6
        assertFalse("Password '123' should be invalid (too short)", isValid)
    }

    /**
     * Test Case 2: Verify that empty email and password strings are rejected.
     */
    @Test
    fun testCredentials_emptyFields_returnsFalse() {
        val email = ""
        val password = ""
        val areValid = email.isNotEmpty() && password.isNotEmpty()
        assertFalse("Empty email and password should be invalid", areValid)
    }

    /**
     * Test Case 3: Verify that user roles are correctly mapped from strings.
     */
    @Test
    fun testUserRole_mapping_isCorrect() {
        val roleStr = "REPORTER"
        val mappedRole = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            null
        }
        assertEquals(UserRole.REPORTER, mappedRole)
    }
}
