package com.zenith26.fest.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Brand600
import com.zenith26.fest.ui.theme.Emerald100
import com.zenith26.fest.ui.theme.Emerald200
import com.zenith26.fest.ui.theme.Emerald600
import com.zenith26.fest.ui.theme.Emerald800
import com.zenith26.fest.ui.theme.FestCoral
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate900

/**
 * Top App Bar strictly matching the web header.
 * Displays logo badge, ZENITH '26 branding, pulsing "Registrations Live" badge,
 * and an optional settings icon to configure backend URL.
 */
@Composable
fun TopHeader(
    onConfigClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp)
            .background(Color.White)
            .border(width = 1.dp, color = Slate200)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand / Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Gradient Icon Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Brand600, FestCoral)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ZENITH ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate900,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "'26",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Brand600,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Text(
                        text = "ANNUAL COLLEGE FEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate400,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Right side: "Registrations Live" pill & settings icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Emerald100)
                        .border(1.dp, Emerald200, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulsing dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Emerald600)
                                .alpha(pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Registrations Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald800
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onConfigClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Backend URL Settings",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
