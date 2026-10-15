# ⚡ Zenith '26 — College Fest Management System

A full-stack, three-tier web application built as part of **Product Management II: Technology & Agile Execution (PM2-01 & PM2-02)**. Zenith '26 demonstrates core software engineering principles for Product Managers — client-server architecture, API contracts, database persistence, and server-side validation ("never trust the client").

---

## 🌟 Features

### 🎓 1. Student Event Portal (`/` or `index.html`)
- **Dynamic Event Cards:** Displays all 14 college fest events loaded directly from SQLite via REST API.
- **Live Search & Category Filtering:** Instant search by event title and category tabs (Technical, Cultural, Sports, Gaming, Workshop) with real query parameter requests to `GET /api/events`.
- **Real-Time Capacity Badges:** Automatically marks events as `FULL` when capacity reaches 0.
- **Event Detail & Registration Modal:** Rich event information and a student booking form.
- **Double Validation ("Never Trust the Client"):** Form fields are validated in JavaScript for instant UI feedback *and* re-validated strictly on the FastAPI server.
- **Instant Reactive Updates:** Seat counts decrement on the fly immediately upon successful registration without needing a full page reload.

### 📊 2. Admin Dashboard (`/admin.html`)
- **Four Real-Time Metric Cards:** Total Events, Total Registrations, Overall Capacity Utilization %, and Upcoming Events.
- **Live Category Breakdown:** Visual pill badges displaying events distribution across genres.
- **Utilization & Demand Table:** Sorted with highest-utilization events first; rows exceeding 90% capacity are highlighted with high-visibility warning badges.
- **Registrations Log & Management:** View student booking records, filter by event, and cancel registrations with modal confirmation.
- **Add Event Tool (Stretch Goal):** Create new events dynamically through the admin interface.

---

### 📱 3. Native Android Mobile App (`android/`)
- **Native Android Development with Kotlin:** Fully reproduces the student portal UI using native Android components and Jetpack Compose.
- **Identical Visual Design:** Matches the exact color palette (Brand purple, Fest accents, Slate neutrals), typography hierarchy, hero section with gradient badges, event cards, category filters, and footer.
- **Real-Time Integration:** Connects to the same FastAPI backend (`/api/events`, `/api/events/{id}`, `/api/events/{id}/register`) without modifying any API contracts or database logic.
- **Registration Modal:** Complete dialog with schedule/venue/seats metadata, client-side input validation, server error banner handling, and automatic seat count updates.
- **Configurable Backend Connection:** Easily toggle between Android Emulator host (`http://10.0.2.2:8000/`) and physical device LAN IP (`http://192.168.x.x:8000/`) via in-app settings.

---

## 🛠️ Tech Stack

| Layer | Technology | Role & Purpose |
| :--- | :--- | :--- |
| **Android Client** | Kotlin, Jetpack Compose, Retrofit 2, OkHttp 3, Gson, Coroutines | Native Android mobile application, HTTP networking, reactive UI state |
| **Web Frontend** | HTML5, Tailwind CSS (Play CDN), JavaScript (Vanilla ES6+) | Responsive cards, modals, filters, dashboards, animations, zero build tools required |
| **Backend** | Python 3.12+, FastAPI, Uvicorn, Pydantic v2 | REST API, business validation logic, static file serving, CORS middleware |
| **Data Layer** | SQLite (`fest.db`), `openpyxl` | Embedded relational database with foreign keys and unique constraints; automated Excel seeding |
| **API Testing** | Postman v2.1.0 Collection, Pytest / TestClient | Automated API contract verification & HTTP status testing |

---

## 🚀 Getting Started

### Prerequisites
- Python 3.10+ installed on your computer.
- Android Studio Hedgehog / Iguana / Jellyfish (or JDK 17+ & Android SDK Platform 34) for running the Android mobile application.

### Step 1: Navigate to the Project Folder
```bash
cd zenith26-fest-system
```

### Step 2: Set Up Python Virtual Environment
On Windows (PowerShell):
```powershell
python -m venv venv
.\venv\Scripts\activate
```
On macOS / Linux:
```bash
bash
python3 -m venv venv
source venv/bin/activate
```

### Step 3: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 4: Run the Backend Application
```bash
uvicorn backend.main:app --reload --port 8000
```

Once running:
- **Student Event Portal:** [http://localhost:8000/](http://localhost:8000/)
- **Admin Dashboard:** [http://localhost:8000/admin.html](http://localhost:8000/admin.html)
- **Interactive Swagger API Docs:** [http://localhost:8000/docs](http://localhost:8000/docs)
- **ReDoc Documentation:** [http://localhost:8000/redoc](http://localhost:8000/redoc)

---

## 📱 Running the Android Mobile App

The Android native application lives inside the `android/` directory.

### Opening in Android Studio
1. Launch **Android Studio**.
2. Select **File > Open...** and choose the `android` folder located at `zenith26-fest-system/android`.
3. Wait for Gradle to sync dependencies automatically.

### Configuring the Backend Base URL
Because mobile devices and emulators have isolated network namespaces:
- **Android Emulator:** The host computer's `localhost` is accessible at `http://10.0.2.2:8000/`. This is pre-configured as the default.
- **Physical Android Device:** Ensure both the Android device and development computer are on the same Wi-Fi network. Find your computer's local IP (e.g., `192.168.1.50` via `ipconfig`) and configure `http://192.168.1.50:8000/`.
- **In-App Dynamic Config:** You can also tap the ⚙️ Settings icon in the top header inside the Android app at any time to update the backend URL without rebuilding the app.

### Running on Emulator or Physical Device
1. Start the FastAPI backend first (`uvicorn backend.main:app --reload --port 8000`).
2. In Android Studio, select your target device (e.g., Pixel 7 emulator or plugged-in USB device with Developer Options enabled).
3. Click the **Run** (green triangle) button or press `Shift + F10`.

### Building an APK from Terminal
With JDK 17 configured:
```bash
cd android
./gradlew assembleDebug
```
The resulting debug APK will be located at:
`android/app/build/outputs/apk/debug/app-debug.apk`

### Implementation Notes & Limitations
- The Android application focuses strictly on the student-facing portal, matching the assignment scope. The Admin dashboard remains accessible via the web interface at `http://localhost:8000/admin.html`.
- Cleartext HTTP traffic is explicitly permitted in `network_security_config.xml` to support local development over `http://` without requiring TLS certificates.

---

## 📡 API Contract Reference

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/events` | List events with live computed `available_seats`. Optional `?category=` and `?search=`. | `200 OK` |
| `GET` | `/api/events/{event_id}` | Retrieve full details for a single event. | `200 OK`, `404 Not Found` |
| `POST` | `/api/events/{event_id}/register` | Register a student for an event with strict server-side validation. | `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `GET` | `/api/registrations` | List all registrations with event titles. Optional `?event_id=`. | `200 OK` |
| `GET` | `/api/stats` | Live fest metrics (events, registrations, capacity, utilization %). | `200 OK` |
| `POST` | `/api/events` | *(Stretch)* Admin adds a new event to the fest calendar. | `201 Created`, `400 Bad Request` |
| `DELETE`| `/api/registrations/{id}`| *(Stretch)* Admin cancels a student registration, restoring the seat. | `200 OK`, `404 Not Found` |

---

## 🧪 Postman API Testing Suite

A complete test suite is provided in `zenith26-api.postman_collection.json`.
Detailed steps and expected payloads for all 18 test requests are documented in [`Postman-Testing-Commands-Zenith26.md`](Postman-Testing-Commands-Zenith26.md).

You can also run the automated Python end-to-end test suite:
```bash
python test_app.py
```

---

## 📂 Project Architecture

```
zenith26-fest-system/
├── android/                        # Native Android Application (Kotlin)
│   ├── build.gradle.kts            # Root Gradle build script
│   ├── settings.gradle.kts         # Gradle project settings
│   ├── gradle.properties           # JVM & AndroidX options
│   ├── gradlew / gradlew.bat       # Gradle wrapper executable scripts
│   ├── gradle/wrapper/             # Wrapper properties
│   └── app/
│       ├── build.gradle.kts        # App dependencies (Compose, Retrofit, Coroutines)
│       ├── proguard-rules.pro      # ProGuard model preservation rules
│       └── src/
│           ├── main/
│           │   ├── AndroidManifest.xml
│           │   ├── java/com/zenith26/fest/
│           │   │   ├── MainActivity.kt        # Main single-activity entry point
│           │   │   ├── ZenithApp.kt           # Application class
│           │   │   ├── data/model/            # Event, RegistrationRequest, ApiError
│           │   │   ├── data/repository/       # EventRepository & NetworkResult
│           │   │   ├── network/               # Retrofit ZenithApiService & NetworkClient
│           │   │   ├── viewmodel/             # EventViewModel & reactive UiState
│           │   │   └── ui/
│           │   │       ├── EventScreen.kt     # Full portal screen combining components
│           │   │       ├── components/        # TopHeader, Hero, FilterBar, Card, Modal, Footer
│           │   │       └── theme/             # Color, Type, Theme (Tailwind hex mapping)
│           │   └── res/
│           │       ├── values/                # strings.xml, colors.xml
│           │       ├── xml/                   # network_security_config.xml
│           │       └── drawable/              # ic_launcher_foreground.xml
│           └── test/
│               └── java/com/zenith26/fest/    # Unit tests for models & validations
├── backend/
│   ├── database.py         # SQLite connection & schema creation
│   ├── main.py             # FastAPI REST endpoints & static file router
│   ├── models.py           # Pydantic schemas for requests and responses
│   └── seed_data.py        # Excel reader importing events_seed.xlsx
├── data/
│   └── events_seed.xlsx    # Companion spreadsheet containing 14 fest events
├── frontend/
│   ├── admin.html          # Coordinator dashboard with charts & logs
│   ├── index.html          # Student fest portal & registration modal
│   └── js/
│       ├── admin.js        # Admin metrics, tables, and registration controls
│       └── app.js          # Student portal cards, filters, and forms
├── fest.db                 # SQLite database (auto-generated on startup)
├── requirements.txt        # Python package dependencies
├── test_app.py             # Automated end-to-end Python test suite
├── zenith26-api.postman_collection.json  # Postman test collection
├── Postman-Testing-Commands-Zenith26.md   # Testing checklist & commands
└── README.md               # Project documentation
```

---

## 💡 Key Product Management Takeaways (PM2-01 & PM2-02)

1. **Client vs. Server as Source of Truth:** Available seat counts are never stored as a separate mutable column in the database; they are calculated live (`capacity - COUNT(registrations)`). This prevents race conditions and data synchronization drift across both web and mobile clients.
2. **Never Trust the Client:** Even if browser or Android app forms enforce required fields or email checks, malicious or altered requests can bypass the UI. FastAPI re-validates all inputs and enforces database-level uniqueness (`UNIQUE(event_id, email)`).
3. **HTTP Status Code Precision:** The backend uses standard semantic codes: `201` for created resources, `400` for bad input, `404` for missing entities, and `409` for business rule conflicts (e.g. event full or already registered). Both the web and Kotlin Android clients handle these codes identically.

