"use client";

import Link from "next/link";
import { BarCard } from "@/components/BarCard";
import { Alert, Breadcrumbs, Empty, PageTitle, Spinner } from "@/components/Feedback";
import { CalendarIcon, PlusIcon, TicketIcon } from "@/components/Icons";
import { useSession } from "@/lib/session";
import type { BarSummary } from "@/lib/types";
import { useApi } from "@/lib/useApi";

export default function OwnerDashboard() {
  const { owner } = useSession();
  const bars = useApi<BarSummary[]>(`/api/bars/owner/${owner!.id}`);

  return (
    <>
      <Breadcrumbs items={[{ label: "My Bars" }]} />
      <div className="mx-auto mt-8 flex max-w-4xl flex-col gap-6 px-6">
        <PageTitle action={<Link href="/owner/bars/new" className="btn btn-sm btn-info"><PlusIcon className="size-4" />Create Bar</Link>}>
          My Bars
        </PageTitle>
        {owner!.businessName && <p className="-mt-4 text-gray-600">{owner!.businessName}</p>}
        {bars.loading && <Spinner />}
        {bars.error && <Alert>{bars.error}</Alert>}
        {bars.data?.length === 0 && (
          <div className="card"><Empty title="You haven't listed a bar yet.">
            <Link href="/owner/bars/new" className="btn btn-info mt-4">List your first bar</Link>
          </Empty></div>
        )}
        {bars.data?.map((b) => (
          <div key={b.id} className="flex flex-col gap-2">
            <BarCard bar={b} />
            <div className="flex flex-wrap justify-end gap-2">
              <Link href={`/owner/bars/${b.id}/bookings`} className="btn btn-sm btn-info"><TicketIcon className="size-4" />Bookings</Link>
              <Link href={`/owner/bars/${b.id}/tables`} className="btn btn-sm btn-light">Tables</Link>
              <Link href={`/owner/bars/${b.id}/events`} className="btn btn-sm btn-light"><CalendarIcon className="size-4" />Events</Link>
              <Link href={`/owner/bars/${b.id}/edit`} className="btn btn-sm btn-light">Edit</Link>
            </div>
          </div>
        ))}
      </div>
    </>
  );
}
