package com.bangkit.scantion.presentation.register

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.bangkit.scantion.data.firebase.AuthRepositoryImpl
import com.bangkit.scantion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
class RegisterKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController

    @Before
    fun setUpNavHost(){
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            Register(navController = navController, viewModel = AuthViewModel(AuthRepositoryImpl(
                FirebaseAuth.getInstance())))
        }
    }

    @Test
    fun verify_registration_button_is_enable_if_all_textfield_not_empty() {
        // Use composeTestRule.onNode functions to interact with Compose components
        composeTestRule.onNodeWithTag("registerNameField").performTextInput("wahyu aji")
        composeTestRule.onNodeWithTag("registerEmailField").performTextInput("whyaji@example.com")
        composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("Password12")
        composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("Password12")

        // Validate that the button is enabled after all field was filled
        composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
    }

    @Test
    fun verify_registration_success() {
        // Use composeTestRule.onNode functions to interact with Compose components
        composeTestRule.onNodeWithTag("registerNameField").performTextInput("wahyu aji")
        composeTestRule.onNodeWithTag("registerEmailField").performTextInput("whyaji@example.com")
        composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("Password12")
        composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("Password12")

        composeTestRule.onNodeWithTag("registerButton").performClick()
    }

    @Test
    fun verify_registration_button_is_disabled_if_all_textfield_empty() {
        composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
    }
}