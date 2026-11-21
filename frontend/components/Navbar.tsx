"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import { useSession } from "@/lib/session";
import { initials } from "@/lib/format";
import { CalendarIcon, ChevronDownIcon, HomeIcon, LogoutIcon, StoreIcon, TicketIcon, UserIcon, VerifiedIcon } from "./Icons";

interface NavItem { href: string; label: string; icon: React.ReactNode; match: (p: string) => boolean }

export function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const { session, ready, logout } = useSession();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!menuOpen) return;
    const close = (e: MouseEvent) => {
      if (!menuRef.current?.contains(e.target as Node)) setMenuOpen(false);
    };
    document.addEventListener("mousedown", close);
    return () => document.removeEventListener("mousedown", close);
  }, [menuOpen]);

  const items: NavItem[] = [
    { href: "/", label: "Home", icon: <HomeIcon />, match: (p) => p === "/" || p.startsWith("/bars") },
    { href: "/events", label: "Events", icon: <CalendarIcon />, match: (p) => p.startsWith("/events") },
  ];
  if (session?.role === "hopper") {
    items.push({ href: "/bookings", label: "My Bookings", icon: <TicketIcon />, match: (p) => p.startsWith("/bookings") });
  }
  if (session?.role === "owner") {
    items.push({ href: "/owner", label: "My Bars", icon: <StoreIcon />, match: (p) => p.startsWith("/owner") });
  }
  items.push({ href: session ? "/profile" : "/login", label: "Profile", icon: <UserIcon />, match: (p) => p.startsWith("/profile") });

  const doLogout = () => {
    setMenuOpen(false);
    logout();
    router.push("/");
  };

  const name = session ? `${session.user.firstName} ${session.user.lastName}` : "";

  return (
    <nav className="fixed inset-x-0 bottom-0 z-30 flex h-16 items-center justify-between border-t border-gray-300/70 bg-gray-100 px-4 sm:top-0 sm:bottom-auto sm:h-[75px] sm:border-t-0 sm:border-b sm:px-12">
      <Link href="/" className="hidden font-display text-3xl font-extrabold text-gray-900 hover:text-info sm:block">
        BarHop
      </Link>

      <div className="flex w-full items-center justify-around gap-1 text-sm font-semibold sm:w-auto sm:justify-center">
        {items.map((it) => {
          const active = it.match(pathname);
          const desktopOnlyProfile = it.label === "Profile";
          return (
            <Link key={it.label} href={it.href}
              className={`flex flex-col items-center gap-0.5 rounded-full px-4 py-1 text-xs transition-colors sm:flex-row sm:px-5 sm:py-2.5 sm:text-sm
                ${desktopOnlyProfile ? "sm:hidden" : ""}
                ${active ? "text-info sm:bg-info/15 sm:outline sm:outline-info/40" : "text-gray-700 hover:text-info"}`}>
              <span className="sm:hidden">{it.icon}</span>
              {it.label}
            </Link>
          );
        })}
      </div>

      <div className="hidden sm:block">
        {!ready ? (
          <div className="h-10 w-40" />
        ) : session ? (
          <div className="relative" ref={menuRef}>
            <button onClick={() => setMenuOpen((o) => !o)} className="flex items-center gap-3 text-left" aria-expanded={menuOpen} aria-haspopup="menu">
              <span className="flex size-10 items-center justify-center rounded-full bg-gray-300 text-lg font-semibold text-gray-700">
                {initials(name)}
              </span>
              <span className="flex flex-col leading-tight">
                <span className="text-xs font-semibold text-gray-600">Welcome back,</span>
                <span className="flex items-center gap-1.5 text-sm font-bold whitespace-nowrap text-gray-800">
                  {name}
                  {session.role === "owner" && <VerifiedIcon className="size-4 text-info" />}
                </span>
              </span>
              <ChevronDownIcon className={`size-5 text-gray-600 transition-transform ${menuOpen ? "rotate-180" : ""}`} />
            </button>
            {menuOpen && (
              <div role="menu" className="absolute right-0 mt-5 w-44 rounded-xl border border-gray-300 bg-gray-200 p-2 text-sm font-semibold shadow-lift">
                <Link role="menuitem" href="/profile" onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 rounded-lg px-3 py-2 text-gray-700 hover:bg-info-light hover:text-info">
                  <UserIcon className="size-5" /> Profile
                </Link>
                <button role="menuitem" onClick={doLogout}
                  className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-gray-700 hover:bg-info-light hover:text-info">
                  <LogoutIcon className="size-5" /> Logout
                </button>
              </div>
            )}
          </div>
        ) : (
          <Link href="/login" className="btn btn-sm btn-info rounded-full">Login</Link>
        )}
      </div>
    </nav>
  );
}
