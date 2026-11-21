"use client";

import { use, useState } from "react";
import { Alert, Breadcrumbs, Empty, PageTitle, Spinner } from "@/components/Feedback";
import { ReservationBadge } from "@/components/StatusBadge";
import { api, apiPath, errorText } from "@/lib/api";
import { dateLong, ticketId, time12, todayIso } from "@/lib/format";
import { useSession } from "@/lib/session";
import type { Reservation, ReservationStatus } from "@/lib/types";
import { useApi } from "@/lib/useApi";
import { OwnedBar } from "../../../OwnedBar";

export default function BarBookingsPage({ params }: PageProps<"/owner/bars/[id]/bookings">) {
  const { id } = use(params);
  return (
    <OwnedBar barId={Number(id)}>
      {(bar) => (
        <>
          <Breadcrumbs items={[{ label: "My Bars", href: "/owner" }, { label: bar.name, href: `/bars/${bar.id}` }, { label: "Bookings" }]} />
          <div className="mx-auto mt-8 flex max-w-5xl flex-col gap-6 px-6">
            <PageTitle>Bookings at {bar.name}</PageTitle>
            <Bookings barId={bar.id} />
          </div>
        </>
      )}
    </OwnedBar>
  );
}

// Allowed owner actions per status (mirrors ReservationComponent.updateStatus).
const ACTIONS: Partial<Record<ReservationStatus, { to: ReservationStatus; label: string; cls: string }[]>> = {
  PENDING: [
    { to: "CONFIRMED", label: "Confirm", cls: "btn-success" },
    { to: "CANCELLED", label: "Decline", cls: "btn-danger" },
  ],
  CONFIRMED: [
    { to: "COMPLETED", label: "Completed", cls: "btn-light" },
    { to: "NO_SHOW", label: "No-show", cls: "btn-light" },
    { to: "CANCELLED", label: "Cancel", cls: "btn-danger" },
  ],
};

function Bookings({ barId }: { barId: number }) {
  const { owner } = useSession();
  const [date, setDate] = useState("");
  const list = useApi<Reservation[]>(apiPath("/reservations", { barId, date }));
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const run = async (id: number, fn: () => Promise<unknown>) => {
    setBusyId(id);
    setError(null);
    try {
      await fn();
      list.reload();
    } catch (e) {
      setError(errorText(e));
    } finally {
      setBusyId(null);
    }
  };
  const setStatus = (r: Reservation, status: ReservationStatus) =>
    run(r.id, () => api.post(`/api/reservations/${r.id}/status`, { ownerId: owner!.id, status }));
  const remove = (r: Reservation) => {
    if (!confirm(`Remove booking #${ticketId(r.id)} from this list?`)) return;
    run(r.id, () => api.post(apiPath(`/reservations/${r.id}/delete`, { ownerId: owner!.id })));
  };

  const rows = list.data ?? list.stale ?? [];
  const pending = rows.filter((r) => r.status === "PENDING").length;

  return (
    <>
      <div className="flex flex-wrap items-end gap-3">
        <div>
          <label className="label" htmlFor="date">Show</label>
          <div className="flex gap-2">
            <input id="date" type="date" className="input w-auto" value={date} onChange={(e) => setDate(e.target.value)} />
            <button className={`btn btn-sm ${date === "" ? "btn-info" : "btn-light"}`} onClick={() => setDate("")}>All upcoming</button>
            <button className={`btn btn-sm ${date === todayIso() ? "btn-info" : "btn-light"}`} onClick={() => setDate(todayIso())}>Today</button>
          </div>
        </div>
        {pending > 0 && <span className="badge mb-2 ml-auto bg-warning-light text-amber-700">{pending} waiting for confirmation</span>}
      </div>

      {error && <Alert>{error}</Alert>}
      {list.loading && !list.stale && <Spinner />}
      {list.error && <Alert>{list.error}</Alert>}
      {!list.loading && rows.length === 0 && <div className="card"><Empty title={date ? `No bookings on ${dateLong(date)}.` : "No upcoming bookings."} /></div>}

      {rows.length > 0 && (
        <div className="card overflow-x-auto">
          <table className="w-full min-w-[760px] text-left text-sm">
            <thead className="border-b border-gray-200 text-xs text-gray-500 uppercase">
              <tr>
                <th className="px-5 py-3">Booking</th>
                <th className="px-5 py-3">When</th>
                <th className="px-5 py-3">Table</th>
                <th className="px-5 py-3">Guest</th>
                <th className="px-5 py-3">Status</th>
                <th className="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {rows.map((r) => (
                <tr key={r.id} className={busyId === r.id ? "opacity-50" : ""}>
                  <td className="px-5 py-4 font-semibold text-gray-800">#{ticketId(r.id)}</td>
                  <td className="px-5 py-4">
                    <span className="block font-semibold text-gray-800">{dateLong(r.reservationDate)}</span>
                    <span className="text-gray-600">{time12(r.startTime)} – {time12(r.endTime)}</span>
                  </td>
                  <td className="px-5 py-4">{r.tableLabel}<span className="block text-gray-500">{r.guestCount} pax</span></td>
                  <td className="px-5 py-4">{r.contactName}<span className="block text-gray-500">{r.contactNumber}</span></td>
                  <td className="px-5 py-4"><ReservationBadge status={r.status} /></td>
                  <td className="px-5 py-4">
                    <div className="flex justify-end gap-2">
                      {ACTIONS[r.status]?.map((a) => (
                        <button key={a.to} className={`btn btn-sm ${a.cls}`} disabled={busyId !== null} onClick={() => setStatus(r, a.to)}>{a.label}</button>
                      ))}
                      {!ACTIONS[r.status] && (
                        <button className="btn btn-sm btn-light" disabled={busyId !== null} onClick={() => remove(r)}>Remove</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <p className="text-xs text-gray-500">Guests get a text when you confirm or cancel. Past bookings are closed out every morning at 6 AM.</p>
    </>
  );
}
