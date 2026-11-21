"use client";

import Link from "next/link";
import { use, useState } from "react";
import { BarImage } from "@/components/BarImage";
import { Alert, Breadcrumbs, Spinner } from "@/components/Feedback";
import { ClockIcon, PencilIcon, PinIcon, StarIcon, UsersIcon } from "@/components/Icons";
import { LikeButton } from "@/components/LikeButton";
import { ActivityBadge } from "@/components/StatusBadge";
import { EventCard } from "@/components/EventCard";
import { apiPath } from "@/lib/api";
import { initials, time12 } from "@/lib/format";
import { useSession } from "@/lib/session";
import type { ActivityStatus, BarDetail, BarEvent, Like } from "@/lib/types";
import { useApi } from "@/lib/useApi";
import { BookingPanel } from "./BookingPanel";
import { OwnerPanel } from "./OwnerPanel";
import { ReviewsSection } from "./ReviewsSection";

export default function BarDetailsPage({ params }: PageProps<"/bars/[id]">) {
  const { id } = use(params);
  const barId = Number(id);
  const { hopper, owner } = useSession();
  const bar = useApi<BarDetail>(`/api/bars/${barId}`);
  const events = useApi<BarEvent[]>(apiPath("/events", { barId }));
  const myLikes = useApi<Like[]>(hopper ? apiPath("/likes", { hopperId: hopper.id }) : null);
  const [live, setLive] = useState<{ like?: Like; count: number; status: ActivityStatus } | null>(null);

  if (bar.error) {
    return <div className="mx-auto mt-12 max-w-xl px-6"><Alert>{bar.error}</Alert></div>;
  }
  const b = bar.data ?? bar.stale;
  if (!b) return <Spinner />;

  const isOwner = owner?.id === b.ownerId;
  const like = live ? live.like : myLikes.data?.find((l) => l.barId === barId);
  const count = live?.count ?? b.activeLikeCount;
  const status = live?.status ?? b.activityStatus;
  const amenities = (b.amenities ?? "").split(",").map((a) => a.trim()).filter(Boolean);

  return (
    <>
      <Breadcrumbs items={[{ label: "Bar List", href: "/" }, { label: b.name }]} />
      <div className="mx-auto my-10 flex max-w-6xl flex-col justify-center gap-10 px-6 md:flex-row md:px-12">
        <section className="flex w-full max-w-3xl flex-col gap-7">
          <BarImage src={b.imageUrl} alt={b.name} className="h-72 w-full rounded-xl" />

          <header className="flex items-start justify-between gap-4">
            <div className="space-y-1.5">
              <h1 className="font-display text-3xl">{b.name}</h1>
              <p className="flex items-center gap-2 text-gray-700"><PinIcon className="size-4" />{b.address}, {b.city}</p>
              <p className="flex items-center gap-2 text-gray-700"><ClockIcon className="size-4" />{time12(b.openingTime)} – {time12(b.closingTime)}</p>
              <p className="flex items-center gap-2 text-gray-700"><UsersIcon className="size-4" />Capacity {b.capacity}</p>
              <div className="pt-1"><ActivityBadge status={status} /></div>
            </div>
            {isOwner && (
              <Link href={`/owner/bars/${b.id}/edit`} className="btn btn-sm btn-info"><PencilIcon className="size-4" />Edit Bar Details</Link>
            )}
          </header>

          {/* Likes and rating strip */}
          <article className="flex border-y border-gray-300 py-5">
            <div className="flex w-full items-center justify-center gap-4 text-danger">
              {hopper ? (
                <LikeButton size="lg" barId={b.id} hopperId={hopper.id} like={like}
                  onChange={(u) => setLive({ like: u.like, count: u.count, status: u.status })} />
              ) : (
                <span className="text-4xl" aria-hidden="true">❤️</span>
              )}
              <div className="flex flex-col items-center">
                <span className="text-2xl leading-none font-bold">{count}</span>
                <span className="text-xs">active like{count === 1 ? "" : "s"}</span>
              </div>
            </div>
            <div className="flex w-full items-center justify-center gap-4 text-warning">
              <StarIcon filled className="size-12" />
              <div className="flex flex-col items-center">
                <span className="text-2xl leading-none font-bold">{b.averageRating.toFixed(1)}</span>
                <span className="text-xs">{b.reviewCount} review{b.reviewCount === 1 ? "" : "s"}</span>
              </div>
            </div>
          </article>
          {hopper && !like && <p className="-mt-4 text-center text-xs text-gray-500">At the bar? Tap the heart. Your like counts toward its activity for 90 minutes.</p>}

          <article className="flex items-center gap-4 rounded-xl border border-gray-300/60 bg-gray-100/60 px-6 py-5">
            <span className="flex size-12 items-center justify-center rounded-full bg-info-light text-lg font-semibold text-info">{initials(b.ownerName)}</span>
            <span className="text-gray-700">Owned by <b className="text-gray-800">{b.ownerName}</b></span>
          </article>

          <article>
            {b.description && <p className="mb-6 leading-relaxed">{b.description}</p>}
            {amenities.length > 0 && (
              <>
                <h2 className="font-display text-lg">Amenities</h2>
                <div className="mt-3 flex flex-wrap gap-2">
                  {amenities.map((a) => <span key={a} className="badge bg-success-light px-3 py-1.5 text-sm text-green-700">{a}</span>)}
                </div>
              </>
            )}
          </article>

          <hr className="border-gray-300" />
          <section className="flex flex-col gap-4">
            <header className="flex items-center justify-between">
              <h2 className="font-display text-2xl">Events</h2>
              {isOwner && <Link href={`/owner/bars/${b.id}/events`} className="btn btn-sm btn-info">Add Event</Link>}
            </header>
            {events.data && events.data.length === 0 && <span className="text-gray-500">No upcoming events.</span>}
            <div className="flex gap-6 overflow-x-auto pb-2">
              {events.data?.map((e) => <EventCard key={e.id} event={e} showBar={false} />)}
            </div>
          </section>

          <hr className="border-gray-300" />
          <ReviewsSection barId={b.id} onRatingChange={bar.reload} />
        </section>

        <aside className="card h-fit w-full shrink-0 md:sticky md:top-24 md:w-[350px]">
          {isOwner ? (
            <OwnerPanel barId={b.id} />
          ) : hopper ? (
            <BookingPanel bar={b} hopper={hopper} />
          ) : (
            <div className="flex flex-col gap-3 p-8">
              <h2 className="font-display text-lg">Book a Table</h2>
              <p className="text-gray-600">{owner ? "Only Bar Hoppers can book tables." : "Log in as a Bar Hopper to reserve a table here."}</p>
              {!owner && <Link href="/login" className="btn btn-info">Log in</Link>}
            </div>
          )}
        </aside>
      </div>
    </>
  );
}
