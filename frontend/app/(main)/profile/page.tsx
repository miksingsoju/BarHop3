"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Alert, Breadcrumbs, PageTitle, Spinner } from "@/components/Feedback";
import { LogoutIcon, VerifiedIcon } from "@/components/Icons";
import { ReviewCard } from "@/components/ReviewCard";
import { api, ApiError, apiPath, errorText, type FieldError } from "@/lib/api";
import { initials } from "@/lib/format";
import { useSession } from "@/lib/session";
import type { Hopper, MessageResponse, Owner, Review } from "@/lib/types";
import { useApi } from "@/lib/useApi";

export default function ProfilePage() {
  const { session, ready } = useSession();
  if (!ready) return <Spinner />;
  if (!session) {
    return (
      <div className="mx-auto mt-16 max-w-md px-6">
        <div className="card flex flex-col items-center gap-4 p-10 text-center">
          <h2 className="font-display text-xl">You&apos;re browsing as a guest</h2>
          <Link href="/login" className="btn btn-info">Log in</Link>
          <Link href="/register" className="link">Create an account</Link>
        </div>
      </div>
    );
  }
  return <Profile key={`${session.role}-${session.user.id}`} />;
}

function Profile() {
  const router = useRouter();
  const { session, login, logout } = useSession();
  const s = session!;
  const isOwner = s.role === "owner";
  const base = isOwner ? "/api/owners" : "/api/hoppers";

  const [form, setForm] = useState({
    firstName: s.user.firstName,
    lastName: s.user.lastName,
    phoneNumber: s.user.phoneNumber,
    businessName: isOwner ? (s.user.businessName ?? "") : "",
  });
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldError[]>([]);
  const [busy, setBusy] = useState(false);
  const reviews = useApi<Review[]>(!isOwner ? apiPath("/reviews", { hopperId: s.user.id }) : null);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setSaved(false);
    setError(null);
    setFieldErrors([]);
    try {
      if (isOwner) {
        const user = await api.post<Owner>(`${base}/${s.user.id}/update`, form);
        login({ role: "owner", user });
      } else {
        const body = { firstName: form.firstName, lastName: form.lastName, phoneNumber: form.phoneNumber };
        const user = await api.post<Hopper>(`${base}/${s.user.id}/update`, body);
        login({ role: "hopper", user });
      }
      setSaved(true);
    } catch (err) {
      if (err instanceof ApiError && err.errors.length) setFieldErrors(err.errors);
      else setError(errorText(err));
    } finally {
      setBusy(false);
    }
  };

  const deleteAccount = async () => {
    const warn = isOwner
      ? "Delete your owner account? You must delete your bars first. This can't be undone."
      : "Delete your account? Your likes and reviews are removed and upcoming bookings cancelled. This can't be undone.";
    if (!confirm(warn)) return;
    try {
      const res = await api.post<MessageResponse>(`${base}/${s.user.id}/delete`);
      alert(res.message);
      logout();
      router.push("/");
    } catch (err) {
      setError(errorText(err));
    }
  };

  const fe = (f: string) => fieldErrors.find((x) => x.field === f)?.message;
  const name = `${s.user.firstName} ${s.user.lastName}`;

  return (
    <>
      <Breadcrumbs items={[{ label: "Profile" }]} />
      <div className="mx-auto mt-8 flex max-w-3xl flex-col gap-6 px-6">
        <PageTitle action={
          <button className="btn btn-sm btn-light" onClick={() => { logout(); router.push("/"); }}><LogoutIcon className="size-4" />Logout</button>
        }>Profile</PageTitle>

        <div className="card flex items-center gap-5 p-6">
          <span className="flex size-16 items-center justify-center rounded-full bg-info-light text-2xl font-semibold text-info">{initials(name)}</span>
          <div>
            <h2 className="flex items-center gap-2 text-xl">{name}{isOwner && <VerifiedIcon className="size-5 text-info" />}</h2>
            <p className="text-gray-600">{s.user.email}</p>
            <span className="badge mt-1 bg-info-light text-info">{isOwner ? "Bar Owner" : "Bar Hopper"}</span>
          </div>
        </div>

        <form onSubmit={save} className="card flex flex-col gap-4 p-6">
          <h3 className="font-display">Edit details</h3>
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="label" htmlFor="fn">First name</label>
              <input id="fn" className="input" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required maxLength={50} />
              {fe("firstName") && <p className="mt-1 text-xs text-danger">{fe("firstName")}</p>}
            </div>
            <div>
              <label className="label" htmlFor="ln">Last name</label>
              <input id="ln" className="input" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required maxLength={50} />
              {fe("lastName") && <p className="mt-1 text-xs text-danger">{fe("lastName")}</p>}
            </div>
            <div>
              <label className="label" htmlFor="ph">Mobile number</label>
              <input id="ph" className="input" value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} required pattern="^\+639\d{9}$" title="+639XXXXXXXXX" />
              {fe("phoneNumber") && <p className="mt-1 text-xs text-danger">{fe("phoneNumber")}</p>}
            </div>
            {isOwner && (
              <div>
                <label className="label" htmlFor="bn">Business name</label>
                <input id="bn" className="input" value={form.businessName} onChange={(e) => setForm({ ...form, businessName: e.target.value })} maxLength={100} />
              </div>
            )}
          </div>
          {saved && <Alert kind="success">Profile updated.</Alert>}
          {error && <Alert>{error}</Alert>}
          <div className="flex justify-end">
            <button className="btn btn-info" disabled={busy}>{busy ? "Saving..." : "Save changes"}</button>
          </div>
        </form>

        {!isOwner && (
          <section className="card flex flex-col gap-6 p-6">
            <h3 className="font-display">My Reviews</h3>
            {reviews.data?.length === 0 && <p className="text-gray-500">You haven&apos;t reviewed any bars yet.</p>}
            {reviews.data?.map((r) => <ReviewCard key={r.id} review={r} showBar />)}
          </section>
        )}

        <section className="card flex flex-col gap-3 border-danger/30 p-6">
          <h3 className="font-display text-danger">Delete account</h3>
          <p className="text-sm text-gray-600">
            {isOwner ? "Only possible once you have no bar listings left." : "Removes your likes and reviews and cancels upcoming bookings."}
          </p>
          <button className="btn btn-danger w-fit" onClick={deleteAccount}>Delete my account</button>
        </section>
      </div>
    </>
  );
}
