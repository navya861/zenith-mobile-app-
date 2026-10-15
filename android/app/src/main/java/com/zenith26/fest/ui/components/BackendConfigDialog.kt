package com.zenith26.fest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Brand600
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate900

/**
 * Dialog enabling developer / evaluator to change backend base URL
 * (e.g., http://10.0.2.2:8000/ for emulator or http://192.168.x.x:8000/ for physical device).
 */
@Composable
fun BackendConfigDialog(
    currentUrl: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Backend Server Configuration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
        },
        text = {
            Column {
                Text(
                    text = "Configure the FastAPI base URL to connect the Android app to your server.",
                    fontSize = 12.sp,
                    color = Slate500,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Standard Emulator: http://10.0.2.2:8000/\n• Physical Device: http://<YOUR_PC_IP>:8000/",
                    fontSize = 11.sp,
                    color = Slate700,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    singleLine = true,
                    label = { Text("Base URL", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlText.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = Brand600)
            ) {
                Text("Save & Reconnect", fontSize = 12.sp, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp, color = Slate500)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
