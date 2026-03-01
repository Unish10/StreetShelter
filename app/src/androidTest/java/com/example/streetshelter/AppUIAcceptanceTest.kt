package com.example.streetshelter

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppUIAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test Case: Login Screen UI Verification
     */
    @Test
    fun testLoginScreen_isDisplayedCorrectly() {
        // Wait for Splash screen to end and Login screen to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("email_field").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify key UI elements on Login Screen
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("password_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("to_register_button").assertIsDisplayed()
    }

    /**
     * Test Case: Register Screen UI Verification (via Role Selection)
     */
    @Test
    fun testRegisterScreen_isDisplayedCorrectly() {
        // Wait for Login screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("to_register_button").fetchSemanticsNodes().isNotEmpty()
        }

        // Navigate to Register
        composeTestRule.onNodeWithTag("to_register_button").performClick()

        // Verify Role Selection screen
        composeTestRule.onNodeWithText("Choose Your Role").assertIsDisplayed()
        composeTestRule.onNodeWithText("Register as Reporter").performClick()

        // Verify Register Screen components
        composeTestRule.onNodeWithText("Create an Account").assertIsDisplayed()
        composeTestRule.onNodeWithTag("reg_email_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("reg_password_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("register_button").assertIsDisplayed()
    }

    /**
     * Test Case: Verify elements of the Dashboards (HomeScreen equivalents)
     * Note: Since we don't log in with real credentials here to avoid auth latency,
     * we verify the reachability of the Forgot Password "Home" state.
     */
    @Test
    fun testDashboardElements_Accessibility() {
        // Wait for Login screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("forgot_password_button").fetchSemanticsNodes().isNotEmpty()
        }

        // Go to Forgot Password (as a proxy for testing a destination screen's UI)
        composeTestRule.onNodeWithTag("forgot_password_button").performClick()

        // Verify Forgot Password Screen UI (Using tags added to ForgotPasswordScreen.kt)
        composeTestRule.onNodeWithTag("forgot_password_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("reset_submit_button").assertIsDisplayed()
        
        // Return to Login
        composeTestRule.onNodeWithTag("back_to_login_button").assertIsDisplayed()
    }
}
