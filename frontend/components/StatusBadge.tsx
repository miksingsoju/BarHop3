import type { ActivityStatus, ReservationStatus } from "@/lib/types";

const ACTIVITY: Record<ActivityStatus, { emoji: string; label: string; cls: string }> = {
  QUIET: { emoji: "🧊", label: "Quiet", cls: "bg-primary-light text-primary" },
  MODERATE: { emoji: "🌡️", label: "Moderate", cls: "bg-warning-light text-amber-600" },
  LIVELY: { emoji: "🔥", label: "Lively", cls: "bg-orange-50 text-orange-600" },
  PACKED: { emoji: "🔥", label: "Packed", cls: "bg-danger-light text-danger" },
};

export function ActivityBadge({ status, count }: { status: ActivityStatus; count?: number }) {
  const s = ACTIVITY[status];
  return (
    <span className={`badge ${s.cls}`} title={count !== undefined ? `${count} active likes` : undefined}>
      <span aria-hidden="true">{s.emoji}</span>
      {s.label}
      {count !== undefined && <span className="font-medium opacity-70">· {count}</span>}
    </span>
  );
}

const RESERVATION: Record<ReservationStatus, string> = {
  PENDING: "bg-warning-light text-amber-600 border-warning/30",
  CONFIRMED: "bg-success-light text-success border-success/30",
  COMPLETED: "bg-primary-light text-primary border-primary/30",
  CANCELLED: "bg-danger-light text-danger border-danger/30",
  NO_SHOW: "bg-gray-200 text-gray-600 border-gray-400/40",
};

export function ReservationBadge({ status }: { status: ReservationStatus }) {
  return <span className={`badge border ${RESERVATION[status]}`}>{status.replace("_", "-")}</span>;
}
