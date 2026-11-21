import Link from "next/link";
import type { BarSummary } from "@/lib/types";
import { ActivityBadge } from "./StatusBadge";
import { BarImage } from "./BarImage";
import { PinIcon, StarIcon } from "./Icons";

export function BarCard({ bar, action }: { bar: BarSummary; action?: React.ReactNode }) {
  return (
    <article className="card group relative flex gap-5 p-5 transition hover:-translate-y-1 hover:shadow-lift">
      <BarImage src={bar.imageUrl} alt={bar.name} className="size-28 shrink-0 rounded-lg sm:size-40" />
      <div className="flex min-w-0 flex-1 flex-col justify-between gap-3">
        <div className="flex items-start justify-between gap-3">
          <div className="min-w-0 space-y-1.5">
            <h3 className="truncate text-lg">
              {/* Stretched link: the whole card is clickable, the like button stays separate. */}
              <Link href={`/bars/${bar.id}`} className="after:absolute after:inset-0 hover:text-info">{bar.name}</Link>
            </h3>
            <p className="flex items-start gap-2 text-sm text-gray-700">
              <PinIcon className="mt-0.5 size-4" />
              <span>{bar.address}, {bar.city}</span>
            </p>
            <p className="flex items-center gap-2 text-sm text-gray-700">
              <StarIcon filled className="size-4 text-warning" />
              <span>
                <b className="text-gray-800">{bar.averageRating.toFixed(1)}</b> · {bar.reviewCount} review{bar.reviewCount === 1 ? "" : "s"}
              </span>
            </p>
          </div>
          {action && <div className="relative z-10">{action}</div>}
        </div>
        <div>
          <ActivityBadge status={bar.activityStatus} count={bar.activeLikeCount} />
        </div>
      </div>
    </article>
  );
}
