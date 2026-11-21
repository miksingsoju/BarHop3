// Display helpers. API times are "HH:mm", dates "yyyy-MM-dd", date-times "yyyy-MM-ddTHH:mm:ss".

export function time12(hhmm: string): string {
  const [h, m] = hhmm.split(":").map(Number);
  const suffix = h >= 12 ? "PM" : "AM";
  const hour = h % 12 === 0 ? 12 : h % 12;
  return `${hour}:${String(m).padStart(2, "0")} ${suffix}`;
}

export function dateLong(iso: string): string {
  const d = new Date(`${iso.slice(0, 10)}T00:00:00`);
  return d.toLocaleDateString("en-PH", { weekday: "short", month: "short", day: "numeric", year: "numeric" });
}

export function dateTime(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleString("en-PH", { month: "short", day: "numeric", hour: "numeric", minute: "2-digit" });
}

export function peso(n: number | null | undefined): string {
  if (n === null || n === undefined) return "Free";
  if (n === 0) return "No minimum";
  return `₱${n.toLocaleString("en-PH")}`;
}

export function ticketId(id: number): string {
  return String(id).padStart(6, "0");
}

/** Today in Manila as yyyy-MM-dd. */
export function todayIso(): string {
  return new Date().toLocaleDateString("en-CA", { timeZone: "Asia/Manila" });
}

export function initials(name: string): string {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join("");
}
