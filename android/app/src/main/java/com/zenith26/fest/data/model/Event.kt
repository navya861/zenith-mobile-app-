package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

/**
 * Event model matching the FastAPI EventOut and EventDetail schemas.
 */
data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("event_date")
    val eventDate: String,

    @SerializedName("event_time")
    val eventTime: String,

    @SerializedName("venue")
    val venue: String,

    @SerializedName("capacity")
    val capacity: Int,

    @SerializedName("fee")
    val fee: Int = 0,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("available_seats")
    val availableSeats: Int
) {
    val isFull: Boolean
        get() = availableSeats <= 0

    val feeLabel: String
        get() = if (fee == 0) "Free" else "₹$fee"

    val feeModalText: String
        get() = if (fee == 0) "Free Entry" else "₹$fee per participant"

    /**
     * Formats ISO date 'YYYY-MM-DD' into 'DD Mon' (e.g. '14 Nov')
     * exactly matching web frontend formatDate().
     */
    fun formattedDate(): String {
        return try {
            val parts = eventDate.split("-")
            if (parts.size == 3) {
                val day = parts[2]
                val monthIndex = parts[1].toIntOrNull()?.minus(1) ?: 0
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val month = if (monthIndex in monthNames.indices) monthNames[monthIndex] else parts[1]
                "$day $month"
            } else {
                eventDate
            }
        } catch (_: Exception) {
            eventDate
        }
    }
}
