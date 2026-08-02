# Ghana AirWatch — Backend

The Spring Boot server behind the AirWatch app. It talks to OpenWeatherMap on
your behalf (so the API key never sits in the browser), saves every reading
to a local database, and answers the health-analytics question: "given this
air quality and this health profile, what should I do?"

## What you need installed

- **Java 21** — you already have this ✅
- **Maven** — you don't need to install this. This project includes a
  "wrapper" (`mvnw.cmd`) that downloads the right Maven version automatically
  the first time you run it.

## How to run it

**1. Open a terminal in this folder** (the one with `pom.xml` in it).

**2. Set your OpenWeatherMap API key** as an environment variable, so it's
never hardcoded into a file. In PowerShell:
```
$env:OWM_API_KEY="your_real_key_here"h
```
(You'll need to do this each time you open a new terminal window — later we
can make it permanent if you want.)

**3. Start the server:**
```
.\mvnw.cmd spring-boot:run
```

The first run will take a few minutes — it's downloading Maven itself plus
all the project's dependencies. That's normal, just let it finish.

**4. Confirm it's running.** Once you see a line like:
```
Started GhanaAirwatchBackendApplication in X seconds
```
open your browser to:
```
http://localhost:8080/api/locations
```
You should see a JSON list of Ghanaian cities. That confirms the backend,
the database, and the seed data all worked.

## What's actually happening under the hood

- **On first startup**, it creates a local database file (`data/airwatch.mv.db`)
  and fills it with the 10 Ghana cities.
- **Every time the frontend asks for air quality**, this backend calls
  OpenWeatherMap itself, converts the result, saves a copy to the database,
  and sends it back. That saved copy is what builds up real history over time.
- **The `/api/health-analytics` endpoint** applies the health-risk rules
  (same ones as the frontend, but now living in one trusted place) and
  returns plain-language advice.

## API endpoints

**Public — no login needed:**

| Method | URL | What it does |
|---|---|---|
| GET | `/api/locations` | List all Ghanaian cities |
| GET | `/api/air-quality?locationId=1` | Current AQI + pollutants (incl. SO₂) + weather (also logs it) |
| GET | `/api/air-quality/history?locationId=1&days=7` | Real stored history |
| GET | `/api/weather?locationId=1` | Temperature, humidity, wind, pressure, rain |
| GET | `/api/predictions?locationId=1` | AQI forecast: +1h, +6h, tomorrow (linear regression) |
| GET | `/api/health-analytics?locationId=1&profile=asthma` | Quick health guidance |
| POST | `/api/health-risk?locationId=1` | Full risk engine — body: `{"age":32,"asthma":true,"heartDisease":false,"pregnancy":false,"outdoorActivity":"high","smoking":"none"}` → Low/Moderate/High/Very High + advice |
| POST | `/api/health-score?locationId=1` | Daily health score /100 (same body as health-risk) |
| GET | `/api/exposure?locationId=1&hours=6&activity=jogging` | Exposure calculator (activity: resting/walking/jogging/sports) |
| GET | `/api/chat?locationId=1&q=Can I jog today?` | Rule-based AI assistant (uses live AQI + weather) |
| GET | `/api/compare?locationIds=1,2` | Compare AQI, PM2.5, temperature, humidity across cities |
| GET | `/api/alerts/check?locationId=1` | Notification trigger check (alert when AQI > 150) |
| GET | `/api/heatmap` | Interpolated AQI grid across Ghana for the pollution map |

**Registered users (need a login token):**

| Method | URL | What it does |
|---|---|---|
| POST | `/api/auth/register` | Body: `{"username","email","password"}` → returns JWT token |
| POST | `/api/auth/login` | Body: `{"username","password"}` → returns JWT token |
| GET | `/api/users/me/favorites` | List my saved locations (send `Authorization: Bearer <token>`) |
| POST | `/api/users/me/favorites?locationId=1` | Save a location |
| DELETE | `/api/users/me/favorites/{id}` | Remove a saved location |

## Viewing the database directly (optional, just for curiosity)

1. With the app running, go to `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:file:./data/airwatch`
3. Username: `sa`, password: (leave blank)
4. Click Connect — you can browse the `LOCATIONS` and `AIR_QUALITY_READINGS`
   tables directly.

## Next step

Once this is running, we switch the React frontend to call this backend
instead of OpenWeatherMap directly — that's a one-line change in
`airQualityService.js`.
