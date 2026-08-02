# Ghana AirWatch — Backend

A Spring Boot API that tracks air quality across Ghanaian cities and turns it into practical health guidance. It sits between the frontend and OpenWeatherMap, keeps the API key off the browser, logs every reading to a database, and runs the health-risk logic that answers: given this air quality and this health profile, what should someone do about it?

**Live app / frontend repo:**

## Tech stack

- Java 21, Spring Boot 3
- Spring Data JPA + H2 (file-based)
- Spring Security + JWT for auth
- Maven (via wrapper, no separate install needed)

## Requirements

- Java 21
- Nothing else — the project ships with a Maven wrapper (`mvnw.cmd`), so you don't need Maven installed separately.

## Running it locally

Open a terminal in this folder (the one with `pom.xml`).

Set your OpenWeatherMap key as an environment variable so it doesn't end up hardcoded anywhere:

```powershell
$env:OWM_API_KEY="your_real_key_here"
```

You'll need to set this again each time you open a new terminal, unless you make it permanent later.

Then start the server:

```powershell
.\mvnw.cmd spring-boot:run
```

First run takes a few minutes since it's pulling down Maven and all the dependencies. After that it's quick.

Once you see:

```
Started GhanaAirwatchBackendApplication in X seconds
```

check `http://localhost:8080/api/locations` in a browser. If you get back a JSON list of Ghanaian cities, the backend, database, and seed data are all working.

## How it works

On first startup it creates a local H2 database file at `data/airwatch.mv.db` and seeds it with 10 Ghanaian cities.

Every time the frontend requests air quality data, the backend calls OpenWeatherMap, converts the response, saves a copy to the database, and returns it. Those saved copies are what build up real history over time instead of just point-in-time snapshots.

`/api/health-analytics` runs the same health-risk rules the frontend used to run client-side, but now from one trusted place, and returns plain-language advice.

## API reference

### Public endpoints

| Method | URL | Description |
|---|---|---|
| GET | `/api/locations` | List all Ghanaian cities |
| GET | `/api/air-quality?locationId=1` | Current AQI, pollutants (incl. SO₂), and weather — also logs the reading |
| GET | `/api/air-quality/history?locationId=1&days=7` | Stored historical readings |
| GET | `/api/weather?locationId=1` | Temperature, humidity, wind, pressure, rain |
| GET | `/api/predictions?locationId=1` | AQI forecast for +1h, +6h, tomorrow (linear regression) |
| GET | `/api/health-analytics?locationId=1&profile=asthma` | Quick health guidance |
| POST | `/api/health-risk?locationId=1` | Full risk engine. Body: `{"age":32,"asthma":true,"heartDisease":false,"pregnancy":false,"outdoorActivity":"high","smoking":"none"}` → returns Low/Moderate/High/Very High plus advice |
| POST | `/api/health-score?locationId=1` | Daily health score out of 100 (same request body as above) |
| GET | `/api/exposure?locationId=1&hours=6&activity=jogging` | Exposure calculator (`activity`: resting/walking/jogging/sports) |
| GET | `/api/chat?locationId=1&q=Can I jog today?` | Rule-based assistant using live AQI + weather |
| GET | `/api/compare?locationIds=1,2` | Compare AQI, PM2.5, temperature, humidity across cities |
| GET | `/api/alerts/check?locationId=1` | Checks whether AQI has crossed the alert threshold (150) |
| GET | `/api/heatmap` | Interpolated AQI grid across Ghana, for the pollution map |

### Authenticated endpoints

Require a JWT from login/register, sent as `Authorization: Bearer <token>`.

| Method | URL | Description |
|---|---|---|
| POST | `/api/auth/register` | Body: `{"username","email","password"}` → returns a token |
| POST | `/api/auth/login` | Body: `{"username","password"}` → returns a token |
| GET | `/api/users/me/favorites` | List saved locations |
| POST | `/api/users/me/favorites?locationId=1` | Save a location |
| DELETE | `/api/users/me/favorites/{id}` | Remove a saved location |

## Project structure

```
src/main/java/com/ghanaairwatch/
├── controller/   # REST endpoints
├── service/      # business logic (AQI predictions, health risk, chat, etc.)
├── repository/   # Spring Data JPA repositories
├── entity/       # database models
├── dto/          # request/response objects
├── security/     # JWT auth
└── config/       # CORS, data seeding
```

## License

MIT — see [LICENSE](LICENSE).

## Status

Personal/student project.feel free to fork or open an issue if something's broken.
