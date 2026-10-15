package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Brand600
import com.zenith26.fest.ui.theme.Rose100
import com.zenith26.fest.ui.theme.Rose200
import com.zenith26.fest.ui.theme.Rose50
import com.zenith26.fest.ui.theme.Rose600
import com.zenith26.fest.ui.theme.Rose700
import com.zenith26.fest.ui.theme.Rose900
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate800

/**
 * Loading state spinner with helper text.
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = Brand600,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Fetching live events schedule...",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Slate500
        )
    }
}

/**
 * Error state display matching web failure state.
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Rose50)
            .border(1.dp, Rose200, RoundedCornerShape(16.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Error Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Rose100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚠️", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Failed to connect to backend service",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Rose900,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (message.isNotBlank()) message else "Please check that FastAPI is running on port 8000.",
                fontSize = 13.sp,
                color = Rose700,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rose600,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🔄 Retry Connection",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Empty search / filter results display with Reset button.
 */
@Composable
fun EmptyView(
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Slate300, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔍", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "No events match your criteria",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Try clearing your search keyword or switching category tabs to see other activities.",
                fontSize = 13.sp,
                color = Slate500,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onResetFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brand600,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Reset Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
