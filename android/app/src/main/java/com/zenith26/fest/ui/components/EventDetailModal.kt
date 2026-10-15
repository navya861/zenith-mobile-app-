package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationResponse
import com.zenith26.fest.ui.theme.Brand600
import com.zenith26.fest.ui.theme.Brand700
import com.zenith26.fest.ui.theme.Brand900
import com.zenith26.fest.ui.theme.Emerald200
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald50
import com.zenith26.fest.ui.theme.Emerald800
import com.zenith26.fest.ui.theme.Emerald900
import com.zenith26.fest.ui.theme.FestCoral
import com.zenith26.fest.ui.theme.Rose200
import com.zenith26.fest.ui.theme.Rose50
import com.zenith26.fest.ui.theme.Rose600
import com.zenith26.fest.ui.theme.Rose800
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate50
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate600
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900

/**
 * Event Detail & Registration Modal strictly matching the web modal.
 */
@Composable
fun EventDetailModal(
    event: Event,
    studentName: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    rollNumber: String,
    onRollChange: (String) -> Unit,
    rollError: String?,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    phone: String,
    onPhoneChange: (String) -> Unit,
    isRegistering: Boolean,
    registrationSuccess: RegistrationResponse?,
    registrationError: String?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Header Banner with Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Brand700, Brand900)
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Close 'X' Button at top-right
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close modal",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Event Icon, Badges, and Title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 36.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x26FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = event.icon ?: "⚡",
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Category Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0x33FFFFFF))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = event.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                // Fee Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0x3310B981))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = event.feeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald300
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = event.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Modal Body Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // 2x2 Meta Grid Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate50)
                            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Schedule", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = "${event.formattedDate()}, ${event.eventTime}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Venue", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = event.venue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Available Seats", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Medium)
                                    if (event.isFull) {
                                        Text(
                                            text = "SOLD OUT (0 seats left)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Rose600
                                        )
                                    } else {
                                        Text(
                                            text = "${event.availableSeats} of ${event.capacity}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Brand600
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Registration Fee", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = event.feeModalText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About This Event
                    Text(
                        text = "ABOUT THIS EVENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description ?: "No additional details provided for this event.",
                        fontSize = 13.sp,
                        color = Slate600,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Slate200, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Registration Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reserve Your Spot",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "* Required fields",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SUCCESS BANNER
                    if (registrationSuccess != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Emerald50)
                                .border(1.dp, Emerald200, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "✅", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Registered for ${event.title}!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                    Text(
                                        text = "Registration ID #${registrationSuccess.id} confirmed for ${registrationSuccess.studentName}.",
                                        fontSize = 12.sp,
                                        color = Emerald800
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // SERVER ERROR BANNER
                    if (registrationError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Rose50)
                                .border(1.dp, Rose200, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "⚠️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = registrationError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Rose800
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // FORM INPUTS

                    // 1. Student Full Name
                    FormField(
                        label = "Student Full Name *",
                        value = studentName,
                        onValueChange = onNameChange,
                        placeholder = "e.g. Navya Mendiratta",
                        errorMessage = nameError,
                        enabled = !isRegistering && !event.isFull
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Roll / Student ID
                    FormField(
                        label = "Roll / Student ID *",
                        value = rollNumber,
                        onValueChange = onRollChange,
                        placeholder = "e.g. 2026MBAPM023",
                        errorMessage = rollError,
                        enabled = !isRegistering && !event.isFull
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Phone (Optional)
                    FormField(
                        label = "Phone (Optional)",
                        value = phone,
                        onValueChange = onPhoneChange,
                        placeholder = "e.g. +91 9876543210",
                        errorMessage = null,
                        keyboardType = KeyboardType.Phone,
                        enabled = !isRegistering && !event.isFull
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. College / Personal Email
                    FormField(
                        label = "College / Personal Email *",
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = "e.g. student@college.edu",
                        errorMessage = emailError,
                        keyboardType = KeyboardType.Email,
                        enabled = !isRegistering && !event.isFull
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Registration Button
                    val isButtonEnabled = !isRegistering && !event.isFull && registrationSuccess == null

                    Button(
                        onClick = onSubmit,
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brand600,
                            disabledContainerColor = if (event.isFull) Slate300 else Brand600.copy(alpha = 0.7f),
                            contentColor = Color.White,
                            disabledContentColor = if (event.isFull) Slate500 else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isRegistering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Registering...", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else if (registrationSuccess != null) {
                            Text(text = "Registered!", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else if (event.isFull) {
                            Text(text = "Event is Full", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else if (registrationError != null) {
                            Text(text = "Try Again", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text(text = "Confirm Registration", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, fontSize = 13.sp, color = Slate400) },
            singleLine = true,
            enabled = enabled,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand600,
                unfocusedBorderColor = Slate300,
                errorBorderColor = FestCoral
            )
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = errorMessage,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Rose600
            )
        }
    }
}
