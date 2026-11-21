"use client";

import Link from "next/link";
import { useState } from "react";
import { Alert, Breadcrumbs, Empty, PageTitle, Spinner } from "@/components/Feedback";
import { ChevronDownIcon, UsersIcon } from "@/components/Icons";
import { RequireRole } from "@/components/RequireRole";
import { ReservationBadge } from "@/components/StatusBadge";
import { api, apiPath, errorText } from "@/lib/api";
import { dateLong, ticketId, time12 } from "@/lib/format";
import { useSession } from "@/lib/session";
import type { Reservation } from "@/lib/types";
import { useApi } from "@/lib/useApi";

export default function BookingsPage() {
  return (
    <RequireRole role="hopper">
      <MyBookings />
    </RequireRole>
  );
}

function MyBookings() {
  const { hopper } = useSession();
  const list = useApi<Reservation[]>(apiPath("/reservations", { hopperId: hopper!.id }));
  const [open, setOpen] = useState<number | null>(null);
  const upcoming = list.data?.filter((r) => r.status === "PENDING" || r.status === "CONFIRMED") ?? [];
  const past = list.data?.filter((r) => r.status !== "PENDING" && r.status !== "CONFIRMED") ?? [];

  return (
    <>
      <Breadcrumbs items={[{ label: "My Reservations" }]} />
      <div className="mx-auto mt-8 flex max-w-3xl flex-col gap-6 px-6">
        <PageTitle>My Reservations</PageTitle>
        {list.loading && <Spinner />}
        {list.error && <Alert>{list.error}</Alert>}
        {list.data?.length === 0 && (
          <Link href="/" className="card block hover:shadow-lift">
            <Empty title="You have no reservations.">Book one now!</Empty>
          </Link>
        )}
        {upcoming.length > 0 && <h2 className="text-sm tracking-wide text-gray-500 uppercase">Upcoming</h2>}
        {upcoming.map((r) => (
          <Ticket key={r.id} r={r} open={open === r.id} onToggle={() => setOpen(open === r.id ? null : r.id)} onChanged={list.reload} />
        ))}
        {past.length > 0 && <h2 className="mt-4 text-sm tracking-wide text-gray-500 uppercase">Past and cancelled</h2>}
        {past.map((r) => (
          <Ticket key={r.id} r={r} open={open === r.id} onToggle={() => setOpen(open === r.id ? null : r.id)} onChanged={list.reload} />
        ))}
      </div>
    </>
  );
}

function Ticket({ r, open, onToggle, onChanged }: { r: Reservation; open: boolean; onToggle: () => void; onChanged: () => void }) {
  const { hopper } = useSession();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const cancellable = r.status === "PENDING" || r.status === "CONFIRMED";

  const cancel = async () => {
    if (!confirm(`Cancel your booking at ${r.barName} on ${r.reservationDate}?`)) return;
    setBusy(true);
    setError(null);
    try {
      await api.post(apiPath(`/reservations/${r.id}/cancel`, { hopperId: hopper!.id }));
      onChanged();
    } catch (e) {
      setError(errorText(e));
    } finally {
      setBusy(false);
    }
  };

  return (
    <article className="card overflow-hidden">
      <button onClick={onToggle} aria-expanded={open} className="w-full text-left">
        <div className="flex items-start justify-between bg-gradient-to-b from-gray-800/10 to-transparent px-5 pt-4">
          <span className="text-xs font-semibold text-gray-600">ID {ticketId(r.id)}</span>
          <ReservationBadge status={r.status} />
        </div>
        <div className="flex items-center justify-between gap-4 px-5 pt-2 pb-5">
          <div>
            <h3 className="text-lg">{r.barName}</h3>
            <span className="text-sm text-gray-600">{r.tableLabel}</span>
          </div>
          <div className="flex items-center gap-3 text-right">
            <div>
              <span className="block font-semibold text-gray-800">{dateLong(r.reservationDate)}</span>
              <span className="block text-xs text-gray-600">{time12(r.startTime)} – {time12(r.endTime)}</span>
            </div>
            <ChevronDownIcon className={`size-5 text-gray-500 transition-transform ${open ? "rotate-180" : ""}`} />
          </div>
        </div>
      </button>
      {open && (
        <div className="px-5 pb-5">
          {/* Ticket divider with punched-out bullets */}
          <div className="relative -mx-5 mb-5 border-t-4 border-dashed border-gray-200">
            <span className="absolute -top-3 -left-3 size-5 rounded-full bg-gray-200" />
            <span className="absolute -top-3 -right-3 size-5 rounded-full bg-gray-200" />
          </div>
          <dl className="grid grid-cols-2 gap-4 text-sm sm:grid-cols-4">
            <div><dt className="text-xs text-gray-600">Guests</dt><dd className="flex items-center gap-1 font-semibold text-gray-800"><UsersIcon className="size-4" />{r.guestCount} pax</dd></div>
            <div><dt className="text-xs text-gray-600">Table</dt><dd className="font-semibold text-gray-800">{r.tableLabel}</dd></div>
            <div><dt className="text-xs text-gray-600">Contact</dt><dd className="font-semibold text-gray-800">{r.contactName}</dd></div>
            <div><dt className="text-xs text-gray-600">Mobile</dt><dd className="font-semibold text-gray-800">{r.contactNumber}</dd></div>
          </dl>
          {error && <div className="mt-4"><Alert>{error}</Alert></div>}
          <div className="mt-5 flex flex-wrap justify-end gap-2">
            <Link href={`/bars/${r.barId}`} className="btn btn-sm btn-light">View Bar</Link>
            {cancellable && <button className="btn btn-sm btn-danger" disabled={busy} onClick={cancel}>{busy ? "Cancelling..." : "Cancel Booking"}</button>}
          </div>
        </div>
      )}
    </article>
  );
}
