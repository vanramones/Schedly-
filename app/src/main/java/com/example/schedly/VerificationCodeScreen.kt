package com.example.schedly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schedly.ui.theme.SchedlyTheme

@Composable
fun VerificationCodeScreen(
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
                text = "Verification code",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Please enter the verification code sent to your email address",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                for (i in 1..4) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .width(60.dp)
                            .padding(horizontal = 4.dp),
                        shape = MaterialTheme.shapes.medium,
                    )
                }
            }

            TextButton(onClick = { /* Resend code */ }) {
                Text("Resend Code")
            }

            // This is a simplified number pad. A real implementation would be more complex.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val numberPad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("*", "0", "<-")
                )
                numberPad.forEach { row ->
                    Row {
                        row.forEach { number ->
                            TextButton(onClick = { /* Handle number pad click */ }, modifier = Modifier.padding(8.dp)) {
                                Text(number, style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerificationCodeScreen() {
    SchedlyTheme {
        VerificationCodeScreen({}, {})
    }
}
