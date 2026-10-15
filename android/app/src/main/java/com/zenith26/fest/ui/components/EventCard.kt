package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.theme.Amber100
import com.zenith26.fest.ui.theme.Amber200
import com.zenith26.fest.ui.theme.Amber800
import com.zenith26.fest.ui.theme.Brand600
import com.zenith26.fest.ui.theme.Emerald600
import com.zenith26.fest.ui.theme.Rose100
import com.zenith26.fest.ui.theme.Rose300
import com.zenith26.fest.ui.theme.Rose800
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate50
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate600
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.getCategoryColorStyle

/**
 * Event card composable strictly reproducing the web grid card layout.
 */
@Composable
fun EventCard(
    event: Event,
    onViewClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryStyle = getCategoryColorStyle(event.category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Category Badge & Capacity Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(categoryStyle.background)
                        .border(1.dp, categoryStyle.border, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = event.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = categoryStyle.text
                    )
                }

                // Capacity / FULL badge
                if (event.isFull) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Rose100)
                            .border(1.dp, Rose300, RoundedCornerShape(999.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "FULL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Rose800
                        )
                    }
                } else {
                    val isLowSeats = event.availableSeats <= 10
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isLowSeats) Amber100 else Slate100)
                            .border(
                                1.dp,
                                if (isLowSeats) Amber200 else Color.Transparent,
                                RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${event.availableSeats} / ${event.capacity} seats left",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isLowSeats) Amber800 else Slate600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon & Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Event Emoji Icon Container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate50)
                        .border(1.dp, Slate100, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.icon ?: "⚡",
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = event.description ?: "Join this exciting fest competition!",
                        fontSize = 12.sp,
                        color = Slate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Slate100, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Details: Date & Venue
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.formattedDate()} • ${event.eventTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate600
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.venue,
                        fontSize = 12.sp,
                        color = Slate600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Slate100, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Fee and Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ENTRY FEE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = event.feeLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (event.fee == 0) Emerald600 else Slate900
                    )
                }

                // Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (event.isFull) Slate100 else Brand600)
                        .clickable { onViewClick(event) }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (event.isFull) "View Info" else "View & Register →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isFull) Slate400 else Color.White
                    )
                }
            }
        }
    }
}
