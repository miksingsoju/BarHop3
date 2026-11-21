"use client";

export type Role = "hopper" | "owner";

export function RoleToggle({ role, onChange }: { role: Role; onChange: (r: Role) => void }) {
  const opt = (r: Role, label: string) => (
    <button type="button" onClick={() => onChange(r)} aria-pressed={role === r}
      className={`flex-1 rounded-lg px-4 py-2 text-sm font-semibold transition ${role === r ? "bg-white text-info shadow-card" : "text-gray-600 hover:text-gray-800"}`}>
      {label}
    </button>
  );
  return (
    <div className="flex w-full gap-1 rounded-xl bg-gray-300 p-1">
      {opt("hopper", "Bar Hopper")}
      {opt("owner", "Bar Owner")}
    </div>
  );
}
