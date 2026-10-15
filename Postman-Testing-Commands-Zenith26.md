# Zenith '26 API Testing Guide — Postman Commands & Test Suite

This guide contains the complete set of 18 test cases for the **Zenith '26 College Fest Management System REST API**, covering every endpoint's success and error scenarios.

---

## 1. Setup Instructions

1. **Start the FastAPI Backend:**
   ```bash
   cd zenith26-fest-system
   .\venv\Scripts\uvicorn backend.main:app --reload --port 8000
   ```
2. **Open Postman Desktop:**
   - Click **Import** in the top-left corner.
   - Choose `zenith26-api.postman_collection.json` located in your `zenith26-fest-system/` root directory.
   - The collection will be imported with `baseUrl` set to `http://localhost:8000`.

---

## 2. Test Cases Overview (18 Requests)

### Folder 1: Events Endpoints

#### Request 1: Get All Events (No Filters)
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events`
- **Expected Status:** `200 OK`
- **Expected Response:** An array of 14 events. Each event contains `id`, `title`, `category`, `capacity`, `fee`, and dynamic `available_seats`.

#### Request 2: Filter Events by Category (Technical)
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events?category=Technical`
- **Expected Status:** `200 OK`
- **Expected Response:** Array of 4 technical events (Code Sprint 2.0, Robo Race, AI Pitch Fest, Web Design Rumble).

#### Request 3: Filter Events by Search Keyword ("Code")
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events?search=Code`
- **Expected Status:** `200 OK`
- **Expected Response:** Array containing matching events (e.g. "Code Sprint 2.0" and "Intro to Vibe Coding").

#### Request 4: Search With No Matches
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events?search=NonExistentKeywordXYZ`
- **Expected Status:** `200 OK`
- **Expected Response:** `[]` (Empty array).

#### Request 5: Get Single Event Details (Valid ID)
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events/1`
- **Expected Status:** `200 OK`
- **Expected Response:**
```json
{
  "id": 1,
  "title": "Code Sprint 2.0",
  "category": "Technical",
  "description": "Fast-paced competitive programming challenge. Solve algorithm problems against the clock.",
  "event_date": "2026-11-14",
  "event_time": "10:00 AM",
  "venue": "Main Auditorium",
  "capacity": 100,
  "fee": 0,
  "icon": "💻",
  "available_seats": 100
}
```

#### Request 6: Get Single Event (Invalid ID — Expected 404)
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events/999`
- **Expected Status:** `404 Not Found`
- **Expected Response:**
```json
{
  "detail": "Event with id 999 not found."
}
```

---

### Folder 2: Student Registrations Endpoints

#### Request 7: Register for Event — Valid Input (Success)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "student_name": "Aarav Sharma",
  "roll_number": "2026BTECH001",
  "email": "aarav.s@college.edu",
  "phone": "+91 9876543210"
}
```
- **Expected Status:** `201 Created`
- **Expected Response:**
```json
{
  "id": 1,
  "event_id": 1,
  "student_name": "Aarav Sharma",
  "registered_at": "2026-10-12T..."
}
```

#### Request 8: Verify Seat Count Decrement After Registration
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/events/1`
- **Expected Status:** `200 OK`
- **Expected Response:** `available_seats` should now be `99` (decremented by 1).

#### Request 9: Validation Failure — Empty Student Name (400 Bad Request)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "student_name": "",
  "roll_number": "2026BTECH002",
  "email": "student@college.edu"
}
```
- **Expected Status:** `400 Bad Request`
- **Expected Response:**
```json
{
  "detail": "Student name, roll number, and email are required and cannot be empty."
}
```

#### Request 10: Validation Failure — Empty Roll Number (400 Bad Request)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "student_name": "Priya Patel",
  "roll_number": "   ",
  "email": "priya@college.edu"
}
```
- **Expected Status:** `400 Bad Request`

#### Request 11: Validation Failure — Invalid Email Format (400 Bad Request)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "student_name": "Rohan Verma",
  "roll_number": "2026BTECH003",
  "email": "rohanwithoutatdotcom"
}
```
- **Expected Status:** `400 Bad Request`
- **Expected Response:**
```json
{
  "detail": "Invalid email format: must contain '@'."
}
```

#### Request 12: Business Rule Failure — Duplicate Registration (409 Conflict)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Body:** *(Same email as Request 7)*
```json
{
  "student_name": "Aarav Sharma",
  "roll_number": "2026BTECH001",
  "email": "aarav.s@college.edu"
}
```
- **Expected Status:** `409 Conflict`
- **Expected Response:**
```json
{
  "detail": "Already registered"
}
```

#### Request 13: Non-Existent Event Registration (404 Not Found)
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events/999/register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "student_name": "Simran Kaur",
  "roll_number": "2026BTECH004",
  "email": "simran@college.edu"
}
```
- **Expected Status:** `404 Not Found`
- **Expected Response:**
```json
{
  "detail": "Event with id 999 not found."
}
```

---

### Folder 3: Admin & Reporting Endpoints

#### Request 14: Get All Registrations
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/registrations`
- **Expected Status:** `200 OK`
- **Expected Response:** List containing all registrations, including joined `event_title`.

#### Request 15: Filter Registrations by Event ID
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/registrations?event_id=1`
- **Expected Status:** `200 OK`
- **Expected Response:** Only registrations for event 1.

#### Request 16: Get Live Admin Stats
- **Method:** `GET`
- **URL:** `http://localhost:8000/api/stats`
- **Expected Status:** `200 OK`
- **Expected Response:**
```json
{
  "total_events": 14,
  "total_registrations": 1,
  "total_capacity": 1417,
  "overall_utilization_pct": 0.1,
  "upcoming_events_count": 14,
  "events_by_category": {
    "Cultural": 5,
    "Technical": 4,
    "Sports": 2,
    "Workshop": 2,
    "Gaming": 1
  }
}
```

---

### Folder 4: Stretch Goals Endpoints

#### Request 17: Admin Add New Event
- **Method:** `POST`
- **URL:** `http://localhost:8000/api/events`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "title": "Quantum Computing 101",
  "category": "Workshop",
  "description": "Introduction to Qubits and Quantum algorithms.",
  "event_date": "2026-11-15",
  "event_time": "03:00 PM",
  "venue": "Physics Lecture Hall",
  "capacity": 30,
  "fee": 0,
  "icon": "⚛️"
}
```
- **Expected Status:** `201 Created`
- **Expected Response:** The created event with `id: 15`.

#### Request 18: Admin Cancel Registration
- **Method:** `DELETE`
- **URL:** `http://localhost:8000/api/registrations/1`
- **Expected Status:** `200 OK`
- **Expected Response:**
```json
{
  "message": "Registration cancelled successfully",
  "registration_id": 1
}
```
*(After this, `available_seats` for Event 1 will increment back up to 100)*
