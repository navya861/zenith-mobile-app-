from pathlib import Path
import openpyxl
from backend.database import get_db_connection, init_db

DATA_FILE = Path(__file__).resolve().parent.parent / "data" / "events_seed.xlsx"

def seed_events():
    """Seeds the events table from data/events_seed.xlsx if not already seeded."""
    init_db()

    if not DATA_FILE.exists():
        raise FileNotFoundError(
            f"Seed data file not found at '{DATA_FILE}'. "
            "Please copy the provided 'events_seed.xlsx' file into the 'data/' folder before running this script."
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    # Skip seeding if events table already has rows
    cursor.execute("SELECT COUNT(*) FROM events;")
    count = cursor.fetchone()[0]
    if count > 0:
        print(f"Events table already has {count} rows. Skipping seed.")
        conn.close()
        return

    # Load workbook and worksheet
    wb = openpyxl.load_workbook(DATA_FILE)
    if "Events" not in wb.sheetnames:
        raise ValueError(f"'Events' sheet not found in '{DATA_FILE}'. Sheets found: {wb.sheetnames}")

    sheet = wb["Events"]

    # Verify header row
    header = [cell.value for cell in sheet[1]]
    expected_header = ['id', 'title', 'category', 'description', 'event_date', 'event_time', 'venue', 'capacity', 'fee', 'icon']
    
    # Read rows after header
    inserted_count = 0
    for row in sheet.iter_rows(min_row=2, values_only=True):
        if not row or not any(row):
            continue
        event_id, title, category, description, event_date, event_time, venue, capacity, fee, icon = row[:10]
        cursor.execute("""
            INSERT INTO events (id, title, category, description, event_date, event_time, venue, capacity, fee, icon)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """, (event_id, title, category, description, str(event_date), str(event_time), venue, int(capacity), int(fee or 0), icon))
        inserted_count += 1

    conn.commit()
    conn.close()
    print(f"Successfully seeded {inserted_count} events from {DATA_FILE.name}.")

if __name__ == "__main__":
    seed_events()
    conn = get_db_connection()
    rows = conn.execute("SELECT id, title, category, event_date, venue, capacity, fee FROM events;").fetchall()
    print("\nCurrent events in database:")
    for r in rows:
        print(f"[{r['id']}] {r['title']} ({r['category']}) - {r['event_date']} | Venue: {r['venue']} | Cap: {r['capacity']} | Fee: Rs.{r['fee']}")
    conn.close()
