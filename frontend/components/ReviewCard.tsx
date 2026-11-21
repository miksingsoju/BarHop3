import Link from "next/link";
import type { Review } from "@/lib/types";
import { dateLong, initials } from "@/lib/format";
import { Stars } from "./Stars";

export function ReviewCard({ review, showBar = false, actions }: { review: Review; showBar?: boolean; actions?: React.ReactNode }) {
  const edited = review.updatedAt !== review.createdAt;
  return (
    <article className="flex gap-4">
      <span className="flex size-11 shrink-0 items-center justify-center rounded-full bg-info-light font-semibold text-info">
        {initials(review.hopperName)}
      </span>
      <div className="flex flex-1 flex-col gap-1.5">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <span className="font-semibold text-gray-800">{showBar ? <Link href={`/bars/${review.barId}`} className="hover:text-info">{review.barName}</Link> : review.hopperName}</span>
            <span className="ml-2 text-xs text-gray-500">
              {review.visitDate ? `Visited ${dateLong(review.visitDate)}` : dateLong(review.createdAt)}{edited ? " · edited" : ""}
            </span>
          </div>
          {actions}
        </div>
        <Stars rating={review.rating} />
        {review.comment && <p className="text-gray-700">{review.comment}</p>}
      </div>
    </article>
  );
}
