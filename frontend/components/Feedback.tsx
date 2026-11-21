import Link from "next/link";
import { ChevronRightIcon, HomeIcon } from "./Icons";

export function Alert({ kind = "danger", children }: { kind?: "danger" | "success" | "info" | "warning"; children: React.ReactNode }) {
  const cls = {
    danger: "bg-danger-light text-danger border-danger/20",
    success: "bg-success-light text-green-700 border-success/20",
    info: "bg-info-light text-info border-info/20",
    warning: "bg-warning-light text-amber-700 border-warning/30",
  }[kind];
  return <div role={kind === "danger" ? "alert" : "status"} className={`rounded-lg border px-4 py-3 text-sm ${cls}`}>{children}</div>;
}

export function Spinner({ label = "Loading..." }: { label?: string }) {
  return (
    <div className="flex items-center justify-center gap-3 py-16 text-gray-500" role="status">
      <span className="size-5 animate-spin rounded-full border-2 border-gray-400 border-t-info" />
      {label}
    </div>
  );
}

export function Empty({ title, children }: { title: string; children?: React.ReactNode }) {
  return (
    <div className="flex flex-col items-center gap-2 px-6 py-14 text-center">
      <span className="text-5xl" aria-hidden="true">🍹</span>
      <h3 className="text-lg text-gray-600">{title}</h3>
      {children && <div className="text-gray-500">{children}</div>}
    </div>
  );
}

export function Breadcrumbs({ items }: { items: { label: string; href?: string }[] }) {
  return (
    <nav aria-label="Breadcrumb" className="mt-6 px-6 text-sm font-semibold text-gray-500 sm:px-12">
      <ol className="flex flex-wrap items-center gap-2">
        <li><Link href="/" aria-label="Home" className="hover:text-info"><HomeIcon className="size-4" /></Link></li>
        {items.map((it) => (
          <li key={it.label} className="flex items-center gap-2">
            <ChevronRightIcon className="size-3.5 text-gray-400" />
            {it.href ? <Link href={it.href} className="hover:text-info">{it.label}</Link> : <span className="text-gray-600">{it.label}</span>}
          </li>
        ))}
      </ol>
    </nav>
  );
}

export function PageTitle({ children, action }: { children: React.ReactNode; action?: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <h1 className="font-display text-2xl">{children}</h1>
      {action}
    </div>
  );
}
