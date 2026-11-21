# BarHop

ESP final project: find lively bars around Katipunan, Maginhawa, Poblacion and BGC, and reserve a table.

- `backend/`: Spring Boot REST service (Jersey, Spring Data JPA, Javax Validation, Retrofit, Scheduling), an Eclipse project built on the course `ProjectTemplate2024` / `SpringBootDependencies` setup.
- `frontend/`: Next.js + Tailwind CSS UI, ported from the Django templates in github.com/miksingsoju/barhop. See `frontend/README.md`.
- `docs/proposal.txt`: the project proposal (API spec), as text.

## Running the backend in Eclipse

1. **Import:** File → Import → General → *Existing Projects into Workspace* → select `~/ESP/barhop/backend` (leave "Copy projects into workspace" unchecked). The project is named **BarHop**. It needs the `SpringBootDependencies` project in the same workspace, the same as `ProjectTemplate2024`.
2. **Database:** MySQL on `localhost:3306`. Open `config/application.properties` and set `spring.datasource.password=` to your MySQL root password. The `barhop_db` database and its tables are created automatically on first run.
3. **SMS (optional):** run the course **FakeTwilio** project (port 9997). BarHop sends every text there and FakeTwilio prints it to the console. If FakeTwilio isn't running, the send failure is only logged. Set `twilio.enabled=false` to just log texts.
4. **Run:** right-click `BarHop.launch` → Run As → BarHop (or run `app.SpringBootApp` as a Java Application). The API is at `http://localhost:8080/api/...`.

On first start with an empty database, demo data is loaded (`barhop.seed=true`):

| Role | Email | Password |
|---|---|---|
| Owner | carla@katitaproom.ph | taproom2026 |
| Owner | miguel@poblacionnights.ph | nights2026 |
| Hopper | paolo.reyes@student.ateneo.edu | katipunan123 |
| Hopper | bea.lim@student.ateneo.edu | maginhawa123 |
| Hopper | jules.cruz@student.ateneo.edu | bgcnights123 |

There are 4 bars, each in a different activity status (Poblacion Rooftop PACKED, The Kati Taproom LIVELY, Maginhawa Social Club MODERATE, BGC Highline QUIET), 13 tables, 2 upcoming events, reviews, and two bookings for Paolo: one CONFIRMED that starts within 2 hours (so the reminder job texts him) and one PENDING. 32 extra `crowdNN@demo.barhop.ph` hoppers (password `crowdpass123`) supply the timed likes, because one hopper can only hold one active like per bar.

To reseed: `DROP DATABASE barhop_db;` and restart.

## Running the frontend

With the backend running:

```bash
cd frontend
npm install
npm run dev
```

Then open http://localhost:3000 and log in with one of the demo accounts above.

## Code layout (`backend/src/app`)

| Package | Contents |
|---|---|
| `rest.controllers` | Jersey resources: Hopper, Owner, Bar, Table, Event, Reservation, Review, Like (`/api/...`) |
| `rest.errorhandlers` | `ApiException` + exception mappers that return the standard error JSON |
| `components` | Business logic (one component per controller), `NotificationComponent` + `TwilioApi` (Retrofit), `ScheduledTasks`, `DataInitializer` |
| `db.entity` / `db.repositories` | The 8 JPA entities and their Spring Data repositories |
| `dto` | Request bodies (validated with `javax.validation`) and response bodies |
| `config` | `JerseyConfig`, `RequestFilter` (CORS, from the template), `RetrofitConfig` |

## Choices not spelled out in the proposal

- **Password hashing** uses PBKDF2-HMAC-SHA256 from the JDK, because `SpringBootDependencies` has no BCrypt jar. To use BCrypt, add `spring-security-crypto` to the build path and swap the body of `PasswordHasher`.
- **Twilio** is called with Retrofit 2.4 (from `SpringBootDependencies`), which has no Jackson converter. The response is read as `ResponseBody` and parsed with Jackson. The request URL is configurable, so the same client works with FakeTwilio and with the real `api.twilio.com`.
- **Overnight slots:** `reservationDate` is the calendar date the slot starts on. A booking at 00:30 on Dec 12 is the late part of the night of Dec 11. Overlap checks compare real date-times, so slots that cross midnight are handled.
- Booking a slot that has already started today returns 400, and so does booking an inactive table.
- Deleting a bar also deletes its booking history (through its tables). Deleting a hopper also deletes their past reservations.
