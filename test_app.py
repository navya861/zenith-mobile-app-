"""
Comprehensive End-to-End Test Suite for Zenith '26 College Fest Management System
Tests all API contracts, validation edge cases, status codes, and database updates.
"""
from fastapi.testclient import TestClient
from backend.main import app

def run_e2e_tests():
    client = TestClient(app)
    passed = 0
    total = 0

    def assert_test(condition, test_name):
        nonlocal passed, total
        total += 1
        if condition:
            passed += 1
            print(f"  [PASS] {test_name}")
        else:
            print(f"  [FAIL] {test_name}")
            raise AssertionError(f"Test failed: {test_name}")

    print("\n--- 1. Testing Event Listing & Filters ---")
    # All events
    res = client.get("/api/events")
    assert_test(res.status_code == 200, "GET /api/events returns 200 OK")
    events = res.json()
    assert_test(len(events) >= 14, f"Seeded events present: {len(events)} (expected >= 14)")
    assert_test("available_seats" in events[0], "available_seats is dynamically computed")

    # Category filter
    res_tech = client.get("/api/events?category=Technical")
    assert_test(res_tech.status_code == 200, "GET /api/events?category=Technical returns 200 OK")
    tech_events = res_tech.json()
    assert_test(all(e["category"] == "Technical" for e in tech_events), "All returned events are Technical")
    assert_test(len(tech_events) == 4, f"Category filter count: {len(tech_events)} (expected 4)")

    # Search filter
    res_search = client.get("/api/events?search=Code")
    assert_test(res_search.status_code == 200, "GET /api/events?search=Code returns 200 OK")
    assert_test(any("Code" in e["title"] for e in res_search.json()), "Search matches title 'Code'")

    # Single event detail
    res_single = client.get("/api/events/1")
    assert_test(res_single.status_code == 200, "GET /api/events/1 returns 200 OK")
    ev1 = res_single.json()
    assert_test(ev1["title"] == "Code Sprint 2.0", f"Event 1 title matches: {ev1['title']}")

    # 404 for invalid event
    res_404 = client.get("/api/events/9999")
    assert_test(res_404.status_code == 404, "GET /api/events/9999 returns 404 Not Found")

    print("\n--- 2. Testing Registration & Validation Edge Cases ---")
    # Initial seats for event 2
    initial_ev2 = client.get("/api/events/2").json()
    initial_seats = initial_ev2["available_seats"]

    # Missing field test (400)
    res_missing = client.post("/api/events/2/register", json={
        "student_name": "",
        "roll_number": "2026BTECH001",
        "email": "test@college.edu"
    })
    assert_test(res_missing.status_code == 400, "Missing name returns 400 Bad Request")

    # Invalid email test (400)
    res_invalid_email = client.post("/api/events/2/register", json={
        "student_name": "Test Student",
        "roll_number": "2026BTECH001",
        "email": "invalidemailformat"
    })
    assert_test(res_invalid_email.status_code == 400, "Email missing '@' returns 400 Bad Request")

    # Valid registration (201)
    unique_email = "test.e2e@college.edu"
    res_success = client.post("/api/events/2/register", json={
        "student_name": "Test Student",
        "roll_number": "2026BTECH001",
        "email": unique_email,
        "phone": "+91 9999999999"
    })
    assert_test(res_success.status_code == 201, "Valid registration returns 201 Created")
    reg_data = res_success.json()
    reg_id = reg_data["id"]
    assert_test(reg_data["event_id"] == 2, "Registration linked to event 2")

    # Duplicate registration (409)
    res_duplicate = client.post("/api/events/2/register", json={
        "student_name": "Test Student",
        "roll_number": "2026BTECH001",
        "email": unique_email
    })
    assert_test(res_duplicate.status_code == 409, "Duplicate registration email returns 409 Conflict")

    # Check available seats decremented by 1
    ev2_updated = client.get("/api/events/2").json()
    assert_test(ev2_updated["available_seats"] == initial_seats - 1, "Available seats decremented by exactly 1")

    print("\n--- 3. Testing Registrations List & Admin Stats ---")
    res_regs = client.get("/api/registrations")
    assert_test(res_regs.status_code == 200, "GET /api/registrations returns 200 OK")
    regs = res_regs.json()
    assert_test(len(regs) >= 1, "Registrations list has at least 1 record")
    assert_test("event_title" in regs[0], "Registration record contains joined event_title")

    # Filter registrations by event
    res_regs_filter = client.get("/api/registrations?event_id=2")
    assert_test(res_regs_filter.status_code == 200, "GET /api/registrations?event_id=2 returns 200 OK")

    # Admin Stats
    res_stats = client.get("/api/stats")
    assert_test(res_stats.status_code == 200, "GET /api/stats returns 200 OK")
    stats = res_stats.json()
    assert_test(stats["total_events"] >= 14, f"Total events in stats: {stats['total_events']}")
    assert_test(stats["total_registrations"] >= 1, f"Total registrations in stats: {stats['total_registrations']}")
    assert_test(stats["overall_utilization_pct"] >= 0.0, f"Utilization pct: {stats['overall_utilization_pct']}%")
    assert_test(len(stats["events_by_category"]) >= 5, "All categories present in stats")

    print("\n--- 4. Testing Stretch Goals (Create Event & Delete Registration) ---")
    # Admin Create Event
    res_create = client.post("/api/events", json={
        "title": "Quantum Robotics Workshop",
        "category": "Workshop",
        "description": "Hands on quantum computing and robotics.",
        "event_date": "2026-11-15",
        "event_time": "02:00 PM",
        "venue": "Physics Lab",
        "capacity": 25,
        "fee": 0,
        "icon": "⚛️"
    })
    assert_test(res_create.status_code == 201, "POST /api/events creates new event with 201 Created")
    new_event_id = res_create.json()["id"]

    # Admin Delete Registration
    res_delete = client.delete(f"/api/registrations/{reg_id}")
    assert_test(res_delete.status_code == 200, f"DELETE /api/registrations/{reg_id} returns 200 OK")

    # Verify seat count restored after cancellation
    ev2_restored = client.get("/api/events/2").json()
    assert_test(ev2_restored["available_seats"] == initial_seats, "Available seats restored after registration cancellation")

    print("\n--- 5. Testing Static File Routes ---")
    res_home = client.get("/")
    assert_test(res_home.status_code == 200, "GET / returns index.html")
    assert_test("Zenith '26" in res_home.text, "Index page has Zenith branding")

    res_admin = client.get("/admin.html")
    assert_test(res_admin.status_code == 200, "GET /admin.html returns admin.html")

    res_js_app = client.get("/js/app.js")
    assert_test(res_js_app.status_code == 200, "GET /js/app.js served successfully")

    res_js_admin = client.get("/js/admin.js")
    assert_test(res_js_admin.status_code == 200, "GET /js/admin.js served successfully")

    print(f"\n==========================================")
    print(f" ALL {passed}/{total} TESTS PASSED SUCCESSFULLY! ")
    print(f"==========================================\n")

if __name__ == "__main__":
    run_e2e_tests()
