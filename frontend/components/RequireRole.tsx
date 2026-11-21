"use client";

import Link from "next/link";
import { useSession } from "@/lib/session";
import { Spinner } from "./Feedback";

/** Shows children only to a logged-in hopper or owner; otherwise a prompt to log in. */
export function RequireRole({ role, children }: { role: "hopper" | "owner"; children: React.ReactNode }) {
  const { session, ready } = useSession();
  if (!ready) return <Spinner />;
  if (session?.role === role) return <>{children}</>;
  return (
    <div className="mx-auto mt-16 max-w-md px-6">
      <div className="card flex flex-col items-center gap-4 p-10 text-center">
        <span className="text-4xl" aria-hidden="true">🔒</span>
        <h2 className="font-display text-xl">
          {role === "owner" ? "Bar Owners only" : "Log in as a Bar Hopper"}
        </h2>
        <p className="text-gray-600">
          {session
            ? `You're logged in as a ${session.role === "owner" ? "Bar Owner" : "Bar Hopper"}. This page is for ${role === "owner" ? "Bar Owners" : "Bar Hoppers"}.`
            : "You need to log in to see this page."}
        </p>
        {!session && <Link href="/login" className="btn btn-info">Log in</Link>}
      </div>
    </div>
  );
}
