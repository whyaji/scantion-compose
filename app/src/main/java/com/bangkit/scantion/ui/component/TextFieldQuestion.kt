package com.bangkit.scantion.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TextFieldQuestion(
    text: String,
    placeholder: String,
    value: String,
    onChangeValue: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    keyboardController?.hide() // Close the keyboard
                }
            }.imePadding(),
            value = value,
            onValueChange = onChangeValue,
            placeholder = { Text(text = placeholder, color = MaterialTheme.colorScheme.secondary) },
        )
    }
}