import Link from "next/link";
import type { BarEvent } from "@/lib/types";
import { dateTime, peso } from "@/lib/format";
import { CalendarIcon } from "./Icons";

export function EventCard({ event, showBar = true, action }: { event: BarEvent; showBar?: boolean; action?: React.ReactNode }) {
  const start = new Date(event.startDateTime);
  return (
    <article className="card flex w-72 shrink-0 flex-col overflow-hidden">
      <div className="flex items-center gap-4 bg-info px-5 py-4 text-white">
        <div className="flex flex-col items-center leading-none">
          <span className="text-xs font-semibold uppercase">{start.toLocaleDateString("en-PH", { month: "short" })}</span>
          <span className="text-2xl font-bold">{start.getDate()}</span>
        </div>
        <h3 className="line-clamp-2 font-display text-white">{event.title}</h3>
      </div>
      <div className="flex flex-1 flex-col gap-2 p-5 text-sm">
        {showBar && <Link href={`/bars/${event.barId}`} className="font-semibold text-info hover:underline">{event.barName}</Link>}
        <p className="flex items-center gap-2 text-gray-600"><CalendarIcon className="size-4" />{dateTime(event.startDateTime)} – {dateTime(event.endDateTime)}</p>
        {event.description && <p className="text-gray-700">{event.description}</p>}
        <div className="mt-auto flex items-center justify-between pt-2">
          <span className="badge bg-gray-200 text-gray-700">Cover: {event.coverCharge ? peso(event.coverCharge) : "Free entry"}</span>
          {action}
        </div>
      </div>
    </article>
  );
}
