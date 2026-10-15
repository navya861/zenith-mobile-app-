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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700

/**
 * Footer composable reproducing the web footer.
 */
@Composable
fun Footer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = Slate200)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Zenith '26",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• College Fest Management System",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Built with FastAPI, SQLite, and Tailwind CSS",
                fontSize = 11.sp,
                color = Slate500,
                textAlign = TextAlign.Center
            )
        }
    }
}
