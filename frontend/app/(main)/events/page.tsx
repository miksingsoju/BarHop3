"use client";

import { Alert, Breadcrumbs, Empty, PageTitle, Spinner } from "@/components/Feedback";
import { EventCard } from "@/components/EventCard";
import type { BarEvent } from "@/lib/types";
import { useApi } from "@/lib/useApi";

export default function EventsPage() {
  const events = useApi<BarEvent[]>("/api/events");
  return (
    <>
      <Breadcrumbs items={[{ label: "Events" }]} />
      <div className="mx-auto mt-8 max-w-5xl px-6 md:px-12">
        <PageTitle>Upcoming Events</PageTitle>
        <p className="mt-1 text-gray-600">Live bands, DJ nights and promos at bars around Metro Manila.</p>
        <div className="mt-8">
          {events.loading && <Spinner />}
          {events.error && <Alert>{events.error}</Alert>}
          {events.data?.length === 0 && <div className="card"><Empty title="No upcoming events yet." /></div>}
          <div className="grid justify-items-center gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {events.data?.map((e) => <EventCard key={e.id} event={e} />)}
          </div>
        </div>
      </div>
    </>
  );
}
