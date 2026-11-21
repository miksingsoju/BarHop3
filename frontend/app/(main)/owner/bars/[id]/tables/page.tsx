"use client";

import { use, useState } from "react";
import { Alert, Breadcrumbs, PageTitle, Spinner } from "@/components/Feedback";
import { TableRow } from "@/components/TableRow";
import { api, ApiError, apiPath, errorText } from "@/lib/api";
import { useSession } from "@/lib/session";
import type { BarTable, TableType } from "@/lib/types";
import { useApi } from "@/lib/useApi";
import { OwnedBar } from "../../../OwnedBar";

export default function TablesPage({ params }: PageProps<"/owner/bars/[id]/tables">) {
  const { id } = use(params);
  return (
    <OwnedBar barId={Number(id)}>
      {(bar) => (
        <>
          <Breadcrumbs items={[{ label: "My Bars", href: "/owner" }, { label: bar.name, href: `/bars/${bar.id}` }, { label: "Tables" }]} />
          <div className="mx-auto mt-8 flex max-w-3xl flex-col gap-6 px-6">
            <PageTitle>Tables at {bar.name}</PageTitle>
            <Tables barId={bar.id} />
          </div>
        </>
      )}
    </OwnedBar>
  );
}

function Tables({ barId }: { barId: number }) {
  const tables = useApi<BarTable[]>(apiPath("/tables", { barId }));
  const [editing, setEditing] = useState<number | "new" | null>(null);

  return (
    <>
      {tables.loading && <Spinner />}
      {tables.error && <Alert>{tables.error}</Alert>}
      <div className="card divide-y divide-gray-200">
        {tables.data?.length === 0 && editing !== "new" && <p className="p-6 text-gray-600">No tables yet. Add the tables guests can book.</p>}
        {tables.data?.map((t) =>
          editing === t.id ? (
            <TableForm key={t.id} barId={barId} table={t} onDone={() => { setEditing(null); tables.reload(); }} onCancel={() => setEditing(null)} />
          ) : (
            <div key={t.id} className={`flex flex-wrap items-center justify-between gap-3 p-5 ${t.active ? "" : "opacity-60"}`}>
              <TableRow table={t} />
              <div className="flex items-center gap-2">
                {!t.active && <span className="badge bg-gray-200 text-gray-600">Inactive</span>}
                <button className="btn btn-sm btn-light" onClick={() => setEditing(t.id)}>Edit</button>
                <DeleteTable table={t} onDone={tables.reload} />
              </div>
            </div>
          ),
        )}
        {editing === "new" && <TableForm barId={barId} onDone={() => { setEditing(null); tables.reload(); }} onCancel={() => setEditing(null)} />}
      </div>
      {editing === null && <button className="btn btn-info w-fit self-end" onClick={() => setEditing("new")}>Add Table</button>}
    </>
  );
}

function TableForm({ barId, table, onDone, onCancel }: { barId: number; table?: BarTable; onDone: () => void; onCancel: () => void }) {
  const { owner } = useSession();
  const [form, setForm] = useState({
    label: table?.label ?? "",
    tableType: table?.tableType ?? ("GROUP" as TableType),
    capacity: String(table?.capacity ?? 4),
    rate: String(table?.rate ?? 0),
    active: table?.active ?? true,
  });
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    const body = { ownerId: owner!.id, barId, label: form.label, tableType: form.tableType, capacity: Number(form.capacity), rate: Number(form.rate), active: form.active };
    try {
      if (table) await api.post(`/api/tables/${table.id}/update`, body);
      else await api.post("/api/tables", body);
      onDone();
    } catch (err) {
      setError(err instanceof ApiError ? err.detail : errorText(err));
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} className="flex flex-col gap-4 bg-gray-100 p-5">
      <div className="grid gap-4 sm:grid-cols-4">
        <div className="sm:col-span-2">
          <label className="label" htmlFor="label">Label</label>
          <input id="label" className="input" value={form.label} onChange={(e) => setForm({ ...form, label: e.target.value })} required maxLength={50} placeholder="VIP Booth 1" />
        </div>
        <div>
          <label className="label" htmlFor="type">Type</label>
          <select id="type" className="input" value={form.tableType} onChange={(e) => setForm({ ...form, tableType: e.target.value as TableType })}>
            <option>SOLO</option><option>GROUP</option><option>VIP</option>
          </select>
        </div>
        <div>
          <label className="label" htmlFor="cap">Seats</label>
          <input id="cap" type="number" min={1} max={30} className="input" value={form.capacity} onChange={(e) => setForm({ ...form, capacity: e.target.value })} required />
        </div>
        <div className="sm:col-span-2">
          <label className="label" htmlFor="rate">Minimum spend (₱)</label>
          <input id="rate" type="number" min={0} step="0.01" className="input" value={form.rate} onChange={(e) => setForm({ ...form, rate: e.target.value })} required />
        </div>
        {table && (
          <label className="flex items-center gap-2 self-end pb-3 text-sm font-medium text-gray-800 sm:col-span-2">
            <input type="checkbox" className="size-4 accent-info" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} />
            Bookable (active)
          </label>
        )}
      </div>
      {error && <Alert>{error}</Alert>}
      <div className="flex justify-end gap-2">
        <button type="button" className="btn btn-sm btn-light" onClick={onCancel}>Cancel</button>
        <button className="btn btn-sm btn-info" disabled={busy}>{busy ? "Saving..." : table ? "Save" : "Add Table"}</button>
      </div>
    </form>
  );
}

function DeleteTable({ table, onDone }: { table: BarTable; onDone: () => void }) {
  const { owner } = useSession();
  const del = async () => {
    if (!confirm(`Delete ${table.label}?`)) return;
    try {
      await api.post(apiPath(`/tables/${table.id}/delete`, { ownerId: owner!.id }));
      onDone();
    } catch (err) {
      alert(errorText(err));
    }
  };
  return <button className="btn btn-sm btn-danger" onClick={del}>Delete</button>;
}
