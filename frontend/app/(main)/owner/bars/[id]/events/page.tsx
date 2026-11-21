"use client";

import { use, useState } from "react";
import { EventCard } from "@/components/EventCard";
import { Alert, Breadcrumbs, PageTitle } from "@/components/Feedback";
import { api, ApiError, apiPath, errorText } from "@/lib/api";
import { useSession } from "@/lib/session";
import type { BarEvent } from "@/lib/types";
import { useApi } from "@/lib/useApi";
import { OwnedBar } from "../../../OwnedBar";

export default function EventsAdminPage({ params }: PageProps<"/owner/bars/[id]/events">) {
  const { id } = use(params);
  return (
    <OwnedBar barId={Number(id)}>
      {(bar) => (
        <>
          <Breadcrumbs items={[{ label: "My Bars", href: "/owner" }, { label: bar.name, href: `/bars/${bar.id}` }, { label: "Events" }]} />
          <div className="mx-auto mt-8 flex max-w-4xl flex-col gap-6 px-6">
            <PageTitle>Events at {bar.name}</PageTitle>
            <Events barId={bar.id} />
          </div>
        </>
      )}
    </OwnedBar>
  );
}

function Events({ barId }: { barId: number }) {
  const { owner } = useSession();
  const events = useApi<BarEvent[]>(apiPath("/events", { barId, upcomingOnly: false }));
  const [form, setForm] = useState({ title: "", description: "", start: "", end: "", cover: "" });
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const create = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await api.post("/api/events", {
        ownerId: owner!.id, barId, title: form.title, description: form.description,
        startDateTime: `${form.start}:00`, endDateTime: `${form.end}:00`,
        coverCharge: form.cover === "" ? null : Number(form.cover),
      });
      setForm({ title: "", description: "", start: "", end: "", cover: "" });
      events.reload();
    } catch (err) {
      setError(err instanceof ApiError ? err.detail : errorText(err));
    } finally {
      setBusy(false);
    }
  };

  const remove = async (ev: BarEvent) => {
    if (!confirm(`Delete "${ev.title}"?`)) return;
    try {
      await api.post(apiPath(`/events/${ev.id}/delete`, { ownerId: owner!.id }));
      events.reload();
    } catch (err) {
      alert(errorText(err));
    }
  };

  return (
    <>
      <form onSubmit={create} className="card flex flex-col gap-4 p-6">
        <h2 className="font-display">Add an event</h2>
        <div>
          <label className="label" htmlFor="title">Title</label>
          <input id="title" className="input" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required maxLength={100} placeholder="Post-Finals Acoustic Night" />
        </div>
        <div>
          <label className="label" htmlFor="desc">Description</label>
          <textarea id="desc" className="input min-h-20" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} maxLength={1000} />
        </div>
        <div className="grid gap-4 sm:grid-cols-3">
          <div>
            <label className="label" htmlFor="start">Starts</label>
            <input id="start" type="datetime-local" className="input" value={form.start} onChange={(e) => setForm({ ...form, start: e.target.value })} required />
          </div>
          <div>
            <label className="label" htmlFor="end">Ends</label>
            <input id="end" type="datetime-local" className="input" value={form.end} onChange={(e) => setForm({ ...form, end: e.target.value })} required />
          </div>
          <div>
            <label className="label" htmlFor="cover">Cover charge (₱)</label>
            <input id="cover" type="number" min={0} className="input" value={form.cover} onChange={(e) => setForm({ ...form, cover: e.target.value })} placeholder="Blank = free entry" />
          </div>
        </div>
        {error && <Alert>{error}</Alert>}
        <button className="btn btn-info w-fit self-end" disabled={busy}>{busy ? "Adding..." : "Add Event"}</button>
      </form>

      {events.data?.length === 0 && <p className="text-gray-500">No events yet.</p>}
      <div className="grid justify-items-center gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {events.data?.map((ev) => (
          <EventCard key={ev.id} event={ev} showBar={false}
            action={<button className="btn btn-sm btn-danger" onClick={() => remove(ev)}>Delete</button>} />
        ))}
      </div>
    </>
  );
}
