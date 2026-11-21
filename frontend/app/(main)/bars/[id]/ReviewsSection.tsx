"use client";

import Link from "next/link";
import { useState } from "react";
import { Alert } from "@/components/Feedback";
import { ReviewCard } from "@/components/ReviewCard";
import { StarInput } from "@/components/Stars";
import { api, apiPath, errorText } from "@/lib/api";
import { todayIso } from "@/lib/format";
import { useSession } from "@/lib/session";
import type { Review } from "@/lib/types";
import { useApi } from "@/lib/useApi";

export function ReviewsSection({ barId, onRatingChange }: { barId: number; onRatingChange: () => void }) {
  const { hopper, session } = useSession();
  const reviews = useApi<Review[]>(apiPath("/reviews", { barId }));
  const mine = hopper ? reviews.data?.find((r) => r.hopperId === hopper.id) : undefined;
  const [editing, setEditing] = useState(false);

  const changed = () => {
    setEditing(false);
    reviews.reload();
    onRatingChange();
  };

  return (
    <section className="flex flex-col gap-5">
      <header className="flex items-center justify-between">
        <h2 className="font-display text-2xl">Reviews</h2>
        {hopper && !mine && !editing && <button className="btn btn-sm btn-info" onClick={() => setEditing(true)}>Leave a Review</button>}
      </header>

      {!session && <Alert kind="info"><Link href="/login" className="font-semibold underline">Log in</Link> as a Bar Hopper to leave a review.</Alert>}
      {session?.role === "owner" && <Alert kind="info">Only Bar Hoppers can leave reviews.</Alert>}

      {hopper && editing && (
        <ReviewForm barId={barId} hopperId={hopper.id} existing={mine} onDone={changed} onCancel={() => setEditing(false)} />
      )}

      {reviews.error && <Alert>{reviews.error}</Alert>}
      {reviews.data?.length === 0 && <p className="text-gray-500">No reviews yet. Be the first to leave one!</p>}
      <div className="flex flex-col gap-8">
        {reviews.data?.map((r) => (
          <ReviewCard key={r.id} review={r} actions={hopper?.id === r.hopperId && !editing ? (
            <div className="flex gap-2">
              <button className="btn btn-sm btn-light" onClick={() => setEditing(true)}>Edit</button>
              <DeleteReview review={r} hopperId={hopper.id} onDone={changed} />
            </div>
          ) : undefined} />
        ))}
      </div>
    </section>
  );
}

function ReviewForm({ barId, hopperId, existing, onDone, onCancel }: {
  barId: number; hopperId: number; existing?: Review; onDone: () => void; onCancel: () => void;
}) {
  const [rating, setRating] = useState(existing?.rating ?? 5);
  const [comment, setComment] = useState(existing?.comment ?? "");
  const [visitDate, setVisitDate] = useState(existing?.visitDate ?? "");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    const body = { hopperId, barId, rating, comment, visitDate: visitDate || null };
    try {
      if (existing) await api.post(`/api/reviews/${existing.id}/update`, body);
      else await api.post("/api/reviews", body);
      onDone();
    } catch (err) {
      setError(errorText(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} className="card flex flex-col gap-4 p-6">
      <h3 className="font-display">{existing ? "Edit your review" : "Leave a review"}</h3>
      <StarInput value={rating} onChange={setRating} />
      <textarea className="input min-h-24" placeholder="How was it? (optional)" maxLength={1000}
        value={comment} onChange={(e) => setComment(e.target.value)} />
      <div className="max-w-56">
        <label className="label" htmlFor="visit">Date of visit (optional)</label>
        <input id="visit" type="date" className="input" max={todayIso()} value={visitDate} onChange={(e) => setVisitDate(e.target.value)} />
      </div>
      {error && <Alert>{error}</Alert>}
      <div className="flex justify-end gap-2">
        <button type="button" className="btn btn-light" onClick={onCancel}>Cancel</button>
        <button type="submit" className="btn btn-info" disabled={busy}>{busy ? "Saving..." : existing ? "Save" : "Post Review"}</button>
      </div>
    </form>
  );
}

function DeleteReview({ review, hopperId, onDone }: { review: Review; hopperId: number; onDone: () => void }) {
  const [busy, setBusy] = useState(false);
  const del = async () => {
    if (!confirm("Delete your review? This can't be undone.")) return;
    setBusy(true);
    try {
      await api.post(apiPath(`/reviews/${review.id}/delete`, { hopperId }));
      onDone();
    } catch (err) {
      alert(errorText(err));
    } finally {
      setBusy(false);
    }
  };
  return <button className="btn btn-sm btn-danger" onClick={del} disabled={busy}>Delete</button>;
}
