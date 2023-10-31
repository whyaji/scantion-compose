package com.bangkit.scantion.presentation.login

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

class LoginKtTest{

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController

    @Before
    fun setUpNavHost(){
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            Login(navController = navController, loginViewModel = AuthViewModel(
                AuthRepositoryImpl(
                FirebaseAuth.getInstance())
            )
            )
        }
    }

    @Test
    fun verify_login_button_is_enable_if_all_tf_not_empty() {
        // Use composeTestRule.onNode functions to interact with Compose components
        composeTestRule.onNodeWithTag("loginEmailField").performTextInput("whyaji@example.com")
        composeTestRule.onNodeWithTag("loginPasswordField").performTextInput("Password12")

        // Validate that the button is enabled after all field was filled
        composeTestRule.onNodeWithTag("loginButton").assertIsEnabled()
    }

    @Test
    fun verify_login_success() {
        composeTestRule.onNodeWithTag("loginEmailField").performTextInput("whyaji@example.com")
        composeTestRule.onNodeWithTag("loginPasswordField").performTextInput("Password12")
        composeTestRule.onNodeWithTag("loginButton").performClick()
    }

    @Test
    fun verify_login_button_is_disabled_if_all_textfield_empty() {
        composeTestRule.onNodeWithTag("loginButton").assertIsNotEnabled()
    }
}
