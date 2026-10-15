package com.zenith26.fest

import com.google.gson.Gson
import com.zenith26.fest.data.model.ApiError
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.model.RegistrationResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying models, serialization contracts, and client-side validation logic.
 */
class EventModelAndValidationTest {

    private val gson = Gson()

    @Test
    fun testEventDeserialization() {
        val json = """
            {
                "id": 1,
                "title": "Code Sprint 2.0",
                "category": "Technical",
                "description": "A 3-hour competitive programming showdown.",
                "event_date": "2026-11-14",
                "event_time": "10:00 AM",
                "venue": "Main Auditorium",
                "capacity": 100,
                "fee": 0,
                "icon": "💻",
                "available_seats": 95
            }
        """.trimIndent()

        val event = gson.fromJson(json, Event::class.java)

        assertNotNull(event)
        assertEquals(1, event.id)
        assertEquals("Code Sprint 2.0", event.title)
        assertEquals("Technical", event.category)
        assertEquals("2026-11-14", event.eventDate)
        assertEquals("10:00 AM", event.eventTime)
        assertEquals("Main Auditorium", event.venue)
        assertEquals(100, event.capacity)
        assertEquals(0, event.fee)
        assertEquals("💻", event.icon)
        assertEquals(95, event.availableSeats)
        assertFalse(event.isFull)
        assertEquals("Free", event.feeLabel)
        assertEquals("Free Entry", event.feeModalText)
        assertEquals("14 Nov", event.formattedDate())
    }

    @Test
    fun testEventFullStatus() {
        val fullEvent = Event(
            id = 2,
            title = "Robo Race",
            category = "Technical",
            description = null,
            eventDate = "2026-11-14",
            eventTime = "02:00 PM",
            venue = "Engineering Lab 2",
            capacity = 40,
            fee = 50,
            icon = "🤖",
            availableSeats = 0
        )

        assertTrue(fullEvent.isFull)
        assertEquals("₹50", fullEvent.feeLabel)
        assertEquals("₹50 per participant", fullEvent.feeModalText)
    }

    @Test
    fun testRegistrationRequestSerialization() {
        val request = RegistrationRequest(
            studentName = "Navya Mendiratta",
            rollNumber = "2026MBAPM023",
            email = "student@college.edu",
            phone = "+91 9876543210"
        )

        val json = gson.toJson(request)

        assertTrue(json.contains("\"student_name\":\"Navya Mendiratta\""))
        assertTrue(json.contains("\"roll_number\":\"2026MBAPM023\""))
        assertTrue(json.contains("\"email\":\"student@college.edu\""))
        assertTrue(json.contains("\"phone\":\"+91 9876543210\""))
    }

    @Test
    fun testRegistrationResponseDeserialization() {
        val json = """
            {
                "id": 42,
                "event_id": 1,
                "student_name": "Navya Mendiratta",
                "registered_at": "2026-10-13T15:00:00.000000"
            }
        """.trimIndent()

        val response = gson.fromJson(json, RegistrationResponse::class.java)

        assertNotNull(response)
        assertEquals(42, response.id)
        assertEquals(1, response.eventId)
        assertEquals("Navya Mendiratta", response.studentName)
        assertEquals("2026-10-13T15:00:00.000000", response.registeredAt)
    }

    @Test
    fun testFastApiErrorDetailParsing() {
        val errorJson = """{"detail": "Already registered"}"""
        val error = gson.fromJson(errorJson, ApiError::class.java)

        assertEquals("Already registered", error.detail)
    }

    @Test
    fun testClientValidationRules() {
        // Validation logic replication from EventViewModel
        fun validate(name: String, roll: String, email: String): Triple<String?, String?, String?> {
            val nameErr = if (name.trim().isEmpty()) "Please enter your name." else null
            val rollErr = if (roll.trim().isEmpty()) "Roll number is required." else null
            val emailTrim = email.trim()
            val emailErr = if (emailTrim.isEmpty() || !emailTrim.contains("@")) "Valid email address containing '@' is required." else null
            return Triple(nameErr, rollErr, emailErr)
        }

        // Test missing name
        val (err1, _, _) = validate("", "2026BTECH001", "test@college.edu")
        assertEquals("Please enter your name.", err1)

        // Test missing roll
        val (_, err2, _) = validate("Student Name", "", "test@college.edu")
        assertEquals("Roll number is required.", err2)

        // Test missing @ in email
        val (_, _, err3) = validate("Student Name", "2026BTECH001", "invalidemail")
        assertEquals("Valid email address containing '@' is required.", err3)

        // Test valid inputs
        val (v1, v2, v3) = validate("Student Name", "2026BTECH001", "valid@college.edu")
        assertNull(v1)
        assertNull(v2)
        assertNull(v3)
    }
}
