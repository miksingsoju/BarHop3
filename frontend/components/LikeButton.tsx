"use client";

import { useState } from "react";
import { api, apiPath, errorText } from "@/lib/api";
import type { ActivityStatus, Like, MessageResponse } from "@/lib/types";
import { HeartIcon } from "./Icons";

interface Props {
  barId: number;
  hopperId: number;
  like: Like | undefined;
  onChange: (update: { like?: Like; barId: number; count: number; status: ActivityStatus }) => void;
  size?: "sm" | "lg";
}

/** Timed like toggle: a like counts toward the bar's activity for 90 minutes. */
export function LikeButton({ barId, hopperId, like, onChange, size = "sm" }: Props) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggle = async (e: React.MouseEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      if (like) {
        const res = await api.post<MessageResponse & { barActiveLikeCount: number; barActivityStatus: ActivityStatus }>(
          apiPath(`/likes/${like.id}/delete`, { hopperId }));
        onChange({ like: undefined, barId, count: res.barActiveLikeCount, status: res.barActivityStatus });
      } else {
        const res = await api.post<Like>("/api/likes", { hopperId, barId });
        onChange({ like: res, barId, count: res.barActiveLikeCount, status: res.barActivityStatus });
      }
    } catch (err) {
      setError(errorText(err));
    } finally {
      setBusy(false);
    }
  };

  const expires = like ? new Date(like.expiresAt).toLocaleTimeString("en-PH", { hour: "numeric", minute: "2-digit" }) : null;
  const title = like ? `You're here! Your like expires at ${expires}. Click to take it back.` : "I'm here and it's lively (counts for 90 minutes)";

  return (
    <div className="flex flex-col items-end gap-1">
      <button type="button" onClick={toggle} disabled={busy} title={title} aria-pressed={!!like}
        aria-label={like ? "Remove your like" : "Like this bar"}
        className={`flex items-center justify-center rounded-full transition disabled:opacity-60
          ${size === "lg" ? "size-14" : "size-10"}
          ${like ? "bg-danger text-white hover:bg-danger/80" : "bg-gray-300 text-gray-700 hover:bg-danger-light hover:text-danger"}`}>
        <HeartIcon filled={!!like} className={size === "lg" ? "size-7" : "size-5"} />
      </button>
      {error && <span className="max-w-48 text-right text-xs text-danger">{error}</span>}
    </div>
  );
}
