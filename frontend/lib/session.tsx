"use client";

import { createContext, useCallback, useContext, useMemo, useSyncExternalStore } from "react";
import type { Hopper, Owner } from "./types";

// The proposal: "The Next.js app stores the returned id in its session."
// We keep the logged-in account in localStorage so it survives reloads.

export type Session =
  | { role: "hopper"; user: Hopper }
  | { role: "owner"; user: Owner };

const KEY = "barhop.session";
const listeners = new Set<() => void>();
let cachedRaw: string | null | undefined;
let cachedValue: Session | null = null;

function read(): Session | null {
  const raw = typeof window === "undefined" ? null : window.localStorage.getItem(KEY);
  if (raw !== cachedRaw) {
    cachedRaw = raw;
    try {
      cachedValue = raw ? (JSON.parse(raw) as Session) : null;
    } catch {
      cachedValue = null;
    }
  }
  return cachedValue;
}

function subscribe(cb: () => void) {
  listeners.add(cb);
  const onStorage = (e: StorageEvent) => e.key === KEY && cb();
  window.addEventListener("storage", onStorage);
  return () => {
    listeners.delete(cb);
    window.removeEventListener("storage", onStorage);
  };
}

function write(s: Session | null) {
  if (s) window.localStorage.setItem(KEY, JSON.stringify(s));
  else window.localStorage.removeItem(KEY);
  listeners.forEach((l) => l());
}

interface SessionContextValue {
  session: Session | null;
  /** false during server render / first paint, before localStorage is read */
  ready: boolean;
  login: (s: Session) => void;
  logout: () => void;
}

const SessionContext = createContext<SessionContextValue | null>(null);

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const session = useSyncExternalStore(subscribe, read, () => null);
  const ready = useSyncExternalStore(subscribe, () => true, () => false);
  const login = useCallback((s: Session) => write(s), []);
  const logout = useCallback(() => write(null), []);
  const value = useMemo(() => ({ session, ready, login, logout }), [session, ready, login, logout]);
  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error("useSession must be used inside SessionProvider");
  const { session } = ctx;
  return {
    ...ctx,
    hopper: session?.role === "hopper" ? session.user : null,
    owner: session?.role === "owner" ? session.user : null,
  };
}
