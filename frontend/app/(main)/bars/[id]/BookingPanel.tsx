"use client";

import Link from "next/link";
import { useState } from "react";
import { Alert, Spinner } from "@/components/Feedback";
import { MinusIcon, PlusIcon } from "@/components/Icons";
import { TableRow } from "@/components/TableRow";
import { api, apiPath, errorText } from "@/lib/api";
import { time12, todayIso } from "@/lib/format";
import type { BarDetail, BarTable, Hopper, Reservation } from "@/lib/types";
import { useApi } from "@/lib/useApi";

/** Hopper booking flow: pick date, time window and party size; pick a free table; confirm. */
export function BookingPanel({ bar, hopper }: { bar: BarDetail; hopper: Hopper }) {
  const [date, setDate] = useState(todayIso());
  const [start, setStart] = useState(bar.openingTime);
  const [end, setEnd] = useState(addHours(bar.openingTime, 2));
  const [guests, setGuests] = useState(2);
  const [tableId, setTableId] = useState<number | null>(null);
  const [contactName, setContactName] = useState(`${hopper.firstName} ${hopper.lastName}`);
  const [contactNumber, setContactNumber] = useState(hopper.phoneNumber);
  const [agreed, setAgreed] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [booked, setBooked] = useState<Reservation | null>(null);

  const ready = date && start && end && guests >= 1;
  const available = useApi<BarTable[]>(ready && !booked
    ? apiPath("/tables/available", { barId: bar.id, date, startTime: start, endTime: end, guests })
    : null);
  const selected = available.data?.find((t) => t.id === tableId) ?? null;

  const book = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selected) return;
    setBusy(true);
    setError(null);
    try {
      const r = await api.post<Reservation>("/api/reservations", {
        hopperId: hopper.id, tableId: selected.id, reservationDate: date, startTime: start, endTime: end,
        guestCount: guests, contactName, contactNumber, agreedToPolicy: agreed,
      });
      setBooked(r);
    } catch (err) {
      setError(errorText(err));
      available.reload();
    } finally {
      setBusy(false);
    }
  };

  if (booked) {
    return (
      <div className="flex flex-col items-center gap-3 p-8 text-center">
        <span className="text-5xl" aria-hidden="true">🎉</span>
        <h2 className="font-display text-xl">Table requested!</h2>
        <p className="text-gray-600">
          <b>{booked.tableLabel}</b> on {booked.reservationDate}, {time12(booked.startTime)}–{time12(booked.endTime)} for {booked.guestCount}.
          It&apos;s <b>PENDING</b> until the bar confirms. We&apos;ll text {booked.contactNumber}.
        </p>
        <Link href="/bookings" className="btn btn-info mt-2 w-full">View My Bookings</Link>
        <button className="link text-sm" onClick={() => { setBooked(null); setTableId(null); setAgreed(false); }}>Book another</button>
      </div>
    );
  }

  return (
    <form onSubmit={book} className="flex flex-col gap-5 p-8">
      <h2 className="font-display text-lg">Book a Table</h2>
      <div>
        <label className="label" htmlFor="date">Date</label>
        <input id="date" type="date" className="input" min={todayIso()} value={date} required
          onChange={(e) => { setDate(e.target.value); setTableId(null); }} />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="label" htmlFor="start">Start Time</label>
          <input id="start" type="time" step={1800} className="input" value={start} required
            onChange={(e) => { setStart(e.target.value); setTableId(null); }} />
        </div>
        <div>
          <label className="label" htmlFor="end">End Time</label>
          <input id="end" type="time" step={1800} className="input" value={end} required
            onChange={(e) => { setEnd(e.target.value); setTableId(null); }} />
        </div>
      </div>
      <p className="-mt-3 text-xs text-gray-500">Open {time12(bar.openingTime)} – {time12(bar.closingTime)}</p>
      <div>
        <span className="label">No. of Guests</span>
        <div className="flex items-center rounded-lg bg-gray-300">
          <button type="button" aria-label="Fewer guests" className="p-3 text-gray-700 hover:text-info"
            onClick={() => { setGuests(Math.max(1, guests - 1)); setTableId(null); }}><MinusIcon /></button>
          <input aria-label="Guests" className="w-full bg-transparent text-center font-semibold outline-none" inputMode="numeric"
            value={guests} onChange={(e) => { setGuests(Math.max(1, Number(e.target.value.replace(/\D/g, "")) || 1)); setTableId(null); }} />
          <button type="button" aria-label="More guests" className="p-3 text-gray-700 hover:text-info"
            onClick={() => { setGuests(Math.min(30, guests + 1)); setTableId(null); }}><PlusIcon /></button>
        </div>
      </div>

      <hr className="border-gray-300" />
      <div className="flex flex-col gap-3">
        <h3 className="text-sm">Available Tables</h3>
        {available.loading && <Spinner label="Checking..." />}
        {available.error && <Alert kind="warning">{available.error}</Alert>}
        {available.data?.length === 0 && <p className="text-sm text-gray-600">No free tables for that slot and party size. Try another time.</p>}
        {available.data?.map((t) => (
          <label key={t.id} className={`flex cursor-pointer items-center justify-between gap-3 rounded-lg border p-2 transition ${tableId === t.id ? "border-info bg-info-light" : "border-transparent hover:bg-gray-100"}`}>
            <TableRow table={t} />
            <input type="radio" name="table" className="size-5 accent-info" checked={tableId === t.id} onChange={() => setTableId(t.id)} />
          </label>
        ))}
      </div>

      {selected && (
        <>
          <hr className="border-gray-300" />
          <div className="flex flex-col gap-3">
            <div>
              <label className="label" htmlFor="cname">Contact name</label>
              <input id="cname" className="input" value={contactName} onChange={(e) => setContactName(e.target.value)} required maxLength={100} />
            </div>
            <div>
              <label className="label" htmlFor="cnum">Contact number</label>
              <input id="cnum" className="input" value={contactNumber} onChange={(e) => setContactNumber(e.target.value)} required
                pattern="^\+639\d{9}$" title="+639XXXXXXXXX" />
            </div>
            <label className="flex items-start gap-3 text-sm text-gray-700">
              <input type="checkbox" className="mt-0.5 size-4 accent-info" checked={agreed} onChange={(e) => setAgreed(e.target.checked)} />
              I agree to the bar&apos;s house policies (valid ID, arrive within 15 minutes, minimum spend applies).
            </label>
          </div>
          {error && <Alert>{error}</Alert>}
          <button type="submit" className="btn btn-info w-full" disabled={busy || !agreed}>
            {busy ? "Booking..." : `Book ${selected.label}`}
          </button>
        </>
      )}
    </form>
  );
}

function addHours(hhmm: string, h: number) {
  const [hh, mm] = hhmm.split(":").map(Number);
  return `${String((hh + h) % 24).padStart(2, "0")}:${String(mm).padStart(2, "0")}`;
}
