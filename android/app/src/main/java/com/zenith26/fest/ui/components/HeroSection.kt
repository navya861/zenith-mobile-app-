package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Brand100
import com.zenith26.fest.ui.theme.Brand200
import com.zenith26.fest.ui.theme.Brand800
import com.zenith26.fest.ui.theme.Brand900
import com.zenith26.fest.ui.theme.FestAmber
import com.zenith26.fest.ui.theme.FestCoral
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate900

/**
 * Hero Banner Section reproducing the web hero section:
 * Gradient background, date pill, headline, description, and event count highlight pills.
 */
@Composable
fun HeroSection(
    modifier: Modifier = Modifier
) {
    val heroGradient = Brush.verticalGradient(
        colors = listOf(
            Brand900,
            Brand800,
            Slate900
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(heroGradient)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Date & Venue Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0x26FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(999.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 Nov 14 – 15, 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Brand100
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color(0x66FFFFFF)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📍 Campus Central Arena",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Brand100
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Headline
            val headline = buildAnnotatedString {
                append("Ignite The Spark at\n")
                withStyle(
                    style = SpanStyle(
                        brush = Brush.horizontalGradient(
                            listOf(FestAmber, FestCoral, Brand100)
                        ),
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Zenith '26")
                }
            }

            Text(
                text = headline,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle Description
            Text(
                text = "Two electrifying days of coding hackathons, rock band showdowns, esports arenas, and hands-on masterclasses. Register now to reserve your seats!",
                fontSize = 13.sp,
                color = Slate300,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Highlight Pills (Horizontally scrollable for mobile)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HighlightPill(icon = "💻", text = "4 Tech Battles")
                Spacer(modifier = Modifier.width(8.dp))
                HighlightPill(icon = "🎸", text = "5 Cultural Acts")
                Spacer(modifier = Modifier.width(8.dp))
                HighlightPill(icon = "⚽", text = "2 Sports Arenas")
                Spacer(modifier = Modifier.width(8.dp))
                HighlightPill(icon = "🎮", text = "Esports & Workshops")
            }
        }
    }
}

@Composable
private fun HighlightPill(icon: String, text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x1AFFFFFF))
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Brand200
            )
        }
    }
}
