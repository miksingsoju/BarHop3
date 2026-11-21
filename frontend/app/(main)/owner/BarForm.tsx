"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { Alert } from "@/components/Feedback";
import { api, ApiError, apiPath, errorText, type FieldError } from "@/lib/api";
import { useSession } from "@/lib/session";
import { METRO_MANILA_CITIES, type BarDetail, type MessageResponse } from "@/lib/types";

/** Create or edit a bar listing. */
export function BarForm({ bar }: { bar?: BarDetail }) {
  const router = useRouter();
  const { owner } = useSession();
  const [form, setForm] = useState({
    name: bar?.name ?? "",
    description: bar?.description ?? "",
    address: bar?.address ?? "",
    city: bar?.city ?? "Quezon City",
    openingTime: bar?.openingTime ?? "18:00",
    closingTime: bar?.closingTime ?? "02:00",
    capacity: String(bar?.capacity ?? 100),
    amenities: bar?.amenities ?? "",
    imageUrl: bar?.imageUrl ?? "",
  });
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldError[]>([]);
  const [busy, setBusy] = useState(false);

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
    setForm({ ...form, [k]: e.target.value });
  const fe = (f: string) => fieldErrors.find((x) => x.field === f)?.message;

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    setFieldErrors([]);
    const body = { ...form, ownerId: owner!.id, capacity: Number(form.capacity) };
    try {
      const saved = bar
        ? await api.post<BarDetail>(`/api/bars/${bar.id}/update`, body)
        : await api.post<BarDetail>("/api/bars", body);
      router.push(`/bars/${saved.id}`);
    } catch (err) {
      if (err instanceof ApiError && err.errors.length) setFieldErrors(err.errors);
      else setError(errorText(err));
      setBusy(false);
    }
  };

  const remove = async () => {
    if (!bar) return;
    if (!confirm(`Delete ${bar.name}? Its tables, events, likes and reviews are deleted, and upcoming bookings are cancelled (guests get a text). This can't be undone.`)) return;
    try {
      const res = await api.post<MessageResponse>(apiPath(`/bars/${bar.id}/delete`, { ownerId: owner!.id }));
      alert(res.message);
      router.push("/owner");
    } catch (err) {
      setError(errorText(err));
    }
  };

  return (
    <form onSubmit={submit} className="card flex flex-col gap-5 p-6 sm:p-8">
      <F error={fe("name")} k="name" label="Bar name"><input id="name" className="input" value={form.name} onChange={set("name")} required maxLength={100} /></F>
      <F error={fe("description")} k="description" label="Description"><textarea id="description" className="input min-h-28" value={form.description} onChange={set("description")} maxLength={1000} /></F>
      <div className="grid gap-5 sm:grid-cols-3">
        <div className="sm:col-span-2">
          <F error={fe("address")} k="address" label="Street address"><input id="address" className="input" value={form.address} onChange={set("address")} required maxLength={200} /></F>
        </div>
        <F error={fe("city")} k="city" label="City" hint="Metro Manila only">
          <select id="city" className="input" value={form.city} onChange={set("city")}>
            {METRO_MANILA_CITIES.map((c) => <option key={c}>{c}</option>)}
          </select>
        </F>
      </div>
      <div className="grid gap-5 sm:grid-cols-3">
        <F error={fe("openingTime")} k="openingTime" label="Opens"><input id="openingTime" type="time" className="input" value={form.openingTime} onChange={set("openingTime")} required /></F>
        <F error={fe("closingTime")} k="closingTime" label="Closes" hint="Can be after midnight"><input id="closingTime" type="time" className="input" value={form.closingTime} onChange={set("closingTime")} required /></F>
        <F error={fe("capacity")} k="capacity" label="Capacity"><input id="capacity" type="number" min={1} max={5000} className="input" value={form.capacity} onChange={set("capacity")} required /></F>
      </div>
      <F error={fe("amenities")} k="amenities" label="Amenities" hint="Comma-separated, e.g. Live band on Fridays, Outdoor seating, Darts">
        <input id="amenities" className="input" value={form.amenities} onChange={set("amenities")} maxLength={500} />
      </F>
      <F error={fe("imageUrl")} k="imageUrl" label="Image URL"><input id="imageUrl" className="input" value={form.imageUrl} onChange={set("imageUrl")} maxLength={300} placeholder="https://..." /></F>
      {error && <Alert>{error}</Alert>}
      <div className="flex flex-wrap items-center justify-between gap-3">
        {bar ? <button type="button" className="btn btn-danger" onClick={remove}>Delete Bar</button> : <span />}
        <div className="flex gap-2">
          <button type="button" className="btn btn-light" onClick={() => router.back()}>Cancel</button>
          <button className="btn btn-info" disabled={busy}>{busy ? "Saving..." : bar ? "Save Changes" : "Create Bar"}</button>
        </div>
      </div>
    </form>
  );
}

function F({ k, label, children, hint, error }: { k: string; label: string; children: React.ReactNode; hint?: string; error?: string }) {
  return (
    <div>
      <label className="label" htmlFor={k}>{label}</label>
      {children}
      {hint && <p className="mt-1 text-xs text-gray-500">{hint}</p>}
      {error && <p className="mt-1 text-xs text-danger">{error}</p>}
    </div>
  );
}
