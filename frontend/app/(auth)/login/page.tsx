"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Alert } from "@/components/Feedback";
import { api, errorText } from "@/lib/api";
import { useSession } from "@/lib/session";
import type { Hopper, Owner } from "@/lib/types";
import { RoleToggle, type Role } from "../RoleToggle";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useSession();
  const [role, setRole] = useState<Role>("hopper");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      if (role === "hopper") {
        const user = await api.post<Hopper>("/api/hoppers/login", { email, password });
        login({ role: "hopper", user });
        router.push("/");
      } else {
        const user = await api.post<Owner>("/api/owners/login", { email, password });
        login({ role: "owner", user });
        router.push("/owner");
      }
    } catch (err) {
      setError(errorText(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="flex w-full flex-col items-center justify-start px-8 pt-12 sm:justify-center sm:pt-0">
      <form onSubmit={submit} className="flex w-full max-w-sm flex-col gap-5">
        <h1 className="mb-4 text-center font-display text-5xl font-extrabold">BarHop</h1>
        <RoleToggle role={role} onChange={setRole} />
        <input className="input" type="email" placeholder="Email" autoComplete="email" required
          value={email} onChange={(e) => setEmail(e.target.value)} />
        <input className="input" type="password" placeholder="Password" autoComplete="current-password" required
          value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <Alert>{error}</Alert>}
        <button className="btn btn-info w-full" type="submit" disabled={busy}>
          {busy ? "Logging in..." : `Login as ${role === "hopper" ? "Bar Hopper" : "Bar Owner"}`}
        </button>
        <p className="text-center text-gray-600">
          Don&apos;t have an account? <Link className="link" href="/register">Sign up</Link>
        </p>
        <p className="mt-4 text-center text-gray-600">
          or <Link className="link" href="/">Continue as Guest</Link>
        </p>
      </form>
    </div>
  );
}
