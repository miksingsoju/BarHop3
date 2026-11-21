# BarHop frontend

Next.js (App Router) + Tailwind CSS v4. The look is ported from the Django/Metronic templates in
github.com/miksingsoju/barhop: same gray palette, purple accent, Poppins/Inter fonts, card layout, pill navbar
(bottom tab bar on phones), and a split login screen.

## Run

```bash
npm install
npm run dev          # http://localhost:3000
```

The backend (the Eclipse project in `../backend`) must be running on `http://localhost:8080`.
The browser calls `/api/...` on the Next.js server, which forwards it to the backend (see `next.config.ts`),
so no CORS setup is needed. To point at another backend: `BACKEND_URL=http://host:port npm run dev`.

## Pages

| Route | Who | What |
|---|---|---|
| `/` | everyone | Bar feed with search (by bar or city), activity/city filters, sort; hoppers can like bars |
| `/bars/[id]` | everyone | Details, live activity, like, events, reviews (write/edit/delete), table booking |
| `/events` | everyone | Upcoming events at all bars |
| `/login`, `/register` | guests | Bar Hopper or Bar Owner accounts |
| `/bookings` | hoppers | My Reservations (tickets, cancel) |
| `/profile` | logged in | Edit details, my reviews, delete account, logout |
| `/owner` | owners | My Bars dashboard |
| `/owner/bars/new`, `/owner/bars/[id]/edit` | owners | Create / edit / delete a bar |
| `/owner/bars/[id]/tables` | owners | Add, edit, deactivate, delete tables |
| `/owner/bars/[id]/events` | owners | Add and delete events |
| `/owner/bars/[id]/bookings` | owners | Confirm, decline, cancel, mark completed / no-show, remove |

## Code

- `lib/api.ts`: fetch wrapper; turns the backend's error JSON into `ApiError` (with field errors).
- `lib/useApi.ts`: small data-loading hook with `reload()`.
- `lib/session.tsx`: the logged-in hopper/owner, kept in `localStorage` (the proposal's "session").
- `lib/types.ts`: TypeScript shapes of every API response.
- `components/`: navbar, bar card, badges, like button, event/review cards, icons.
- `app/globals.css`: Tailwind theme (colours, fonts) and the shared `.card`, `.btn`, `.input` styles.

Bar photos: the seed data points at `/images/bars/*.jpg`. Put images with those names in
`public/images/bars/` (or use any image URL when editing a bar). Missing images show a gray placeholder.
