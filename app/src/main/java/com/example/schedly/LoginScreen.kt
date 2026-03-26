package com.example.schedly

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schedly.ui.theme.SchedlyTheme

@Composable
fun LoginScreen(
    onCreateAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    errorMessage: String?,
    onErrorDismiss: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    val displayedError = localError ?: errorMessage

    Scaffold(
        topBar = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(top = 32.dp, start = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Log in to Schedly!",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Please enter your details to continue",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    localError = null
                    if (errorMessage != null) onErrorDismiss()
                },
                label = { Text("Username") },
                leadingIcon = { Icon(painterResource(id = R.drawable.icon_profilecircle), contentDescription = null, modifier = Modifier.size(28.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = null
                    if (errorMessage != null) onErrorDismiss()
                },
                label = { Text("Password") },
                leadingIcon = { Icon(painterResource(id = R.drawable.icon_lockcircle), contentDescription = null, modifier = Modifier.size(28.dp)) },
                trailingIcon = {
                    val visibilityIcon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            displayedError?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            TextButton(onClick = onForgotPasswordClick) {
                Text("Forgot your password?")
            }

            Button(
                onClick = {
                    val trimmedUsername = username.trim()
                    val trimmedPassword = password.trim()
                    if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
                        localError = "Please enter username and password"
                    } else {
                        localError = null
                        onErrorDismiss()
                        onLoginClick(trimmedUsername, trimmedPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In")
            }

            TextButton(onClick = onCreateAccountClick) {
                Text("Create a new account")
            }

            Text(
                text = "Or",
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /* Handle Google login */ }) {
                    Image(painter = painterResource(id = R.drawable.icon_google), contentDescription = "Google Login", modifier = Modifier.size(64.dp))
                }
                IconButton(onClick = { /* Handle Twitter login */ }) {
                    Image(painter = painterResource(id = R.drawable.icon_twitter), contentDescription = "Twitter Login", modifier = Modifier.size(64.dp))
                }
                IconButton(onClick = { /* Handle Apple login */ }) {
                    Image(painter = painterResource(id = R.drawable.icon_apple), contentDescription = "Apple Login", modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    SchedlyTheme {
        LoginScreen({}, {}, {}, { _, _ -> }, errorMessage = null, onErrorDismiss = {})
    }
}
