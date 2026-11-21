"use client";

import Link from "next/link";
import { apiPath } from "@/lib/api";
import type { BarTable } from "@/lib/types";
import { useApi } from "@/lib/useApi";
import { TableRow } from "@/components/TableRow";

export function OwnerPanel({ barId }: { barId: number }) {
  const tables = useApi<BarTable[]>(apiPath("/tables", { barId }));
  return (
    <div className="flex flex-col gap-4 p-8">
      <h2 className="font-display text-lg">Table Types</h2>
      {tables.data?.length === 0 && <span className="text-gray-600">No tables to show.</span>}
      {tables.data?.map((t) => <TableRow key={t.id} table={t} />)}
      <Link href={`/owner/bars/${barId}/tables`} className="btn btn-sm btn-info mt-2">
        {tables.data?.length ? "Manage Tables" : "Add Tables"}
      </Link>
      <hr className="border-gray-300" />
      <Link href={`/owner/bars/${barId}/bookings`} className="btn btn-sm btn-info">Check Reservations</Link>
    </div>
  );
}
