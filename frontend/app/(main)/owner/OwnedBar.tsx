"use client";

import { Alert, Spinner } from "@/components/Feedback";
import { useSession } from "@/lib/session";
import type { BarDetail } from "@/lib/types";
import { useApi } from "@/lib/useApi";

/** Loads a bar and only renders children if the logged-in owner owns it. */
export function OwnedBar({ barId, children }: { barId: number; children: (bar: BarDetail, reload: () => void) => React.ReactNode }) {
  const { owner } = useSession();
  const bar = useApi<BarDetail>(`/api/bars/${barId}`);
  if (bar.error) return <div className="mx-auto mt-12 max-w-xl px-6"><Alert>{bar.error}</Alert></div>;
  const b = bar.data ?? bar.stale;
  if (!b) return <Spinner />;
  if (b.ownerId !== owner?.id) {
    return <div className="mx-auto mt-12 max-w-xl px-6"><Alert>You don&apos;t own {b.name}.</Alert></div>;
  }
  return <>{children(b, bar.reload)}</>;
}
