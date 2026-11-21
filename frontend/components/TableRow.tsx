import type { BarTable } from "@/lib/types";
import { peso } from "@/lib/format";

const EMOJI = { SOLO: "🪑", GROUP: "🍻", VIP: "🥂" } as const;

export function TableRow({ table }: { table: BarTable }) {
  return (
    <div className="flex items-center gap-4">
      <div className="flex size-14 shrink-0 items-center justify-center rounded-lg bg-gray-300 text-2xl" aria-hidden="true">
        {EMOJI[table.tableType]}
      </div>
      <div>
        <span className="block font-semibold text-gray-800">
          {table.label} <span className="ml-1 text-xs font-medium text-gray-500">{table.tableType}</span>
        </span>
        <span className="block text-sm font-medium text-gray-500">
          Seats {table.capacity > 1 ? "up to " : ""}{table.capacity} pax · {peso(table.rate)}
        </span>
      </div>
    </div>
  );
}
