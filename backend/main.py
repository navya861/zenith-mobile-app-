from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import Optional, List, Dict
import sqlite3

from fastapi import FastAPI, HTTPException, Query, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from backend.database import get_db_connection
from backend.seed_data import seed_events
from backend.models import (
    EventOut,
    EventDetail,
    EventCreate,
    RegistrationIn,
    RegistrationOut,
    RegistrationDetailOut,
    StatsOut,
)

BASE_DIR = Path(__file__).resolve().parent.parent
FRONTEND_DIR = BASE_DIR / "frontend"

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Seed database on startup if needed
    seed_events()
    yield

app = FastAPI(
    title="Zenith '26 College Fest Management System API",
    description="REST API for events, registrations, and admin management for Zenith '26 Fest.",
    version="1.0.0",
    lifespan=lifespan,
)

# Enable CORS for local development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -------------------------------------------------------------
# API Endpoints
# -------------------------------------------------------------

@app.get("/api/events", response_model=List[EventOut], tags=["Events"])
def get_events(
    category: Optional[str] = Query(None, description="Filter by event category"),
    search: Optional[str] = Query(None, description="Search by event title (case-insensitive)"),
):
    """Returns a list of events with live-computed available_seats."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT e.id, e.title, e.category, e.description, e.event_date, e.event_time, 
               e.venue, e.capacity, e.fee, e.icon,
               (e.capacity - (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.id)) AS available_seats
        FROM events e
        WHERE 1=1
    """
    params = []

    if category and category.strip().lower() != "all":
        query += " AND LOWER(e.category) = LOWER(?)"
        params.append(category.strip())

    if search and search.strip():
        query += " AND LOWER(e.title) LIKE LOWER(?)"
        params.append(f"%{search.strip()}%")

    query += " ORDER BY e.id ASC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    events = [dict(row) for row in rows]
    return events


@app.get("/api/events/{event_id}", response_model=EventDetail, tags=["Events"])
def get_event(event_id: int):
    """Returns one event with full details, or 404 if not found."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("""
        SELECT e.id, e.title, e.category, e.description, e.event_date, e.event_time, 
               e.venue, e.capacity, e.fee, e.icon,
               (e.capacity - (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.id)) AS available_seats
        FROM events e
        WHERE e.id = ?
    """, (event_id,))
    row = cursor.fetchone()
    conn.close()

    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found."
        )

    return dict(row)


@app.post("/api/events", response_model=EventOut, status_code=status.HTTP_201_CREATED, tags=["Events"])
def create_event(payload: EventCreate):
    """Stretch Goal: Admin adds a new event with server-side validation."""
    if not payload.title.strip() or not payload.category.strip() or not payload.venue.strip():
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Title, category, and venue are required.")
    if payload.capacity <= 0:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Capacity must be greater than 0.")

    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO events (title, category, description, event_date, event_time, venue, capacity, fee, icon)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (
        payload.title.strip(),
        payload.category.strip(),
        payload.description.strip() if payload.description else "",
        payload.event_date.strip(),
        payload.event_time.strip(),
        payload.venue.strip(),
        payload.capacity,
        payload.fee,
        payload.icon or "🎉",
    ))
    new_id = cursor.lastrowid
    conn.commit()

    cursor.execute("""
        SELECT e.id, e.title, e.category, e.description, e.event_date, e.event_time, 
               e.venue, e.capacity, e.fee, e.icon,
               e.capacity AS available_seats
        FROM events e WHERE e.id = ?
    """, (new_id,))
    row = cursor.fetchone()
    conn.close()
    return dict(row)


@app.post("/api/events/{event_id}/register", response_model=RegistrationOut, status_code=status.HTTP_201_CREATED, tags=["Registrations"])
def register_for_event(event_id: int, payload: RegistrationIn):
    """
    Registers a student for an event. Performs strict server-side validation:
    - event_id must exist (404)
    - student_name, roll_number, email must not be empty (400)
    - email must contain '@' (400)
    - event must not be at capacity (409)
    - student email must not already be registered for this event (409)
    """
    student_name = payload.student_name.strip()
    roll_number = payload.roll_number.strip()
    email = payload.email.strip()
    phone = payload.phone.strip() if payload.phone else None

    # 1. Non-empty fields
    if not student_name or not roll_number or not email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Student name, roll number, and email are required and cannot be empty."
        )

    # 2. Email format validation
    if "@" not in email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid email format: must contain '@'."
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    # 3. Check event existence & available capacity
    cursor.execute("""
        SELECT e.capacity, 
               (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.id) AS reg_count
        FROM events e
        WHERE e.id = ?
    """, (event_id,))
    event_row = cursor.fetchone()

    if not event_row:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found."
        )

    capacity = event_row["capacity"]
    reg_count = event_row["reg_count"]

    if reg_count >= capacity:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Event is full"
        )

    # 4. Check for duplicate registration for this event
    cursor.execute("""
        SELECT id FROM registrations 
        WHERE event_id = ? AND LOWER(email) = LOWER(?)
    """, (event_id, email))
    existing_reg = cursor.fetchone()

    if existing_reg:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered"
        )

    # 5. Insert registration
    registered_at = datetime.now().isoformat()
    try:
        cursor.execute("""
            INSERT INTO registrations (event_id, student_name, roll_number, email, phone, registered_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (event_id, student_name, roll_number, email, phone, registered_at))
        reg_id = cursor.lastrowid
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered"
        )

    conn.close()
    return RegistrationOut(
        id=reg_id,
        event_id=event_id,
        student_name=student_name,
        registered_at=registered_at,
    )


@app.get("/api/registrations", response_model=List[RegistrationDetailOut], tags=["Registrations"])
def get_registrations(event_id: Optional[int] = Query(None, description="Filter registrations by event_id")):
    """Returns a list of registrations, including event title."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT r.id, r.event_id, r.student_name, r.roll_number, r.email, r.phone, r.registered_at,
               e.title AS event_title
        FROM registrations r
        JOIN events e ON r.event_id = e.id
    """
    params = []
    if event_id is not None:
        query += " WHERE r.event_id = ?"
        params.append(event_id)

    query += " ORDER BY r.id DESC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    return [dict(row) for row in rows]


@app.delete("/api/registrations/{registration_id}", tags=["Registrations"])
def delete_registration(registration_id: int):
    """Stretch Goal: Admin cancels a registration."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT id FROM registrations WHERE id = ?", (registration_id,))
    row = cursor.fetchone()
    if not row:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Registration with id {registration_id} not found."
        )

    cursor.execute("DELETE FROM registrations WHERE id = ?", (registration_id,))
    conn.commit()
    conn.close()
    return {"message": "Registration cancelled successfully", "registration_id": registration_id}


@app.get("/api/stats", response_model=StatsOut, tags=["Stats"])
def get_stats():
    """Computes live stats from SQLite without caching."""
    conn = get_db_connection()
    cursor = conn.cursor()

    # Total events
    cursor.execute("SELECT COUNT(*) FROM events;")
    total_events = cursor.fetchone()[0]

    # Total registrations
    cursor.execute("SELECT COUNT(*) FROM registrations;")
    total_registrations = cursor.fetchone()[0]

    # Total capacity
    cursor.execute("SELECT COALESCE(SUM(capacity), 0) FROM events;")
    total_capacity = cursor.fetchone()[0]

    # Overall utilization percentage
    overall_utilization_pct = 0.0
    if total_capacity > 0:
        overall_utilization_pct = round((total_registrations / total_capacity) * 100.0, 1)

    # Upcoming events count (event_date today or later)
    today_str = datetime.now().strftime("%Y-%m-%d")
    cursor.execute("SELECT COUNT(*) FROM events WHERE event_date >= ?;", (today_str,))
    upcoming_events_count = cursor.fetchone()[0]

    # Events by category
    cursor.execute("""
        SELECT category, COUNT(*) as cat_count
        FROM events
        GROUP BY category
        ORDER BY cat_count DESC
    """)
    category_rows = cursor.fetchall()
    events_by_category = {row["category"]: row["cat_count"] for row in category_rows}

    conn.close()

    return StatsOut(
        total_events=total_events,
        total_registrations=total_registrations,
        total_capacity=total_capacity,
        overall_utilization_pct=overall_utilization_pct,
        upcoming_events_count=upcoming_events_count,
        events_by_category=events_by_category,
    )


# -------------------------------------------------------------
# Static Frontend Serving (Step 4)
# -------------------------------------------------------------

# Mount JavaScript folder at /js
if (FRONTEND_DIR / "js").exists():
    app.mount("/js", StaticFiles(directory=FRONTEND_DIR / "js"), name="js")

@app.get("/", response_class=FileResponse, include_in_schema=False)
def serve_index():
    return FileResponse(FRONTEND_DIR / "index.html")

@app.get("/admin.html", response_class=FileResponse, include_in_schema=False)
def serve_admin():
    return FileResponse(FRONTEND_DIR / "admin.html")
