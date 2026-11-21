"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Alert } from "@/components/Feedback";
import { api, ApiError, errorText, type FieldError } from "@/lib/api";
import { useSession } from "@/lib/session";
import type { Hopper, Owner } from "@/lib/types";
import { RoleToggle, type Role } from "../RoleToggle";

const empty = { firstName: "", lastName: "", email: "", password: "", confirm: "", phoneNumber: "+639", businessName: "" };

export default function RegisterPage() {
  const router = useRouter();
  const { login } = useSession();
  const [role, setRole] = useState<Role>("hopper");
  const [form, setForm] = useState(empty);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldError[]>([]);
  const [busy, setBusy] = useState(false);

  const set = (k: keyof typeof empty) => (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value });
  const fieldError = (f: string) => fieldErrors.find((e) => e.field === f)?.message;

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors([]);
    if (form.password !== form.confirm) {
      setFieldErrors([{ field: "confirm", message: "Passwords don't match." }]);
      return;
    }
    setBusy(true);
    const body = {
      firstName: form.firstName, lastName: form.lastName, email: form.email,
      password: form.password, phoneNumber: form.phoneNumber,
      ...(role === "owner" ? { businessName: form.businessName } : {}),
    };
    try {
      if (role === "hopper") {
        await api.post<Hopper>("/api/hoppers", body);
        const user = await api.post<Hopper>("/api/hoppers/login", { email: form.email, password: form.password });
        login({ role: "hopper", user });
        router.push("/");
      } else {
        await api.post<Owner>("/api/owners", body);
        const user = await api.post<Owner>("/api/owners/login", { email: form.email, password: form.password });
        login({ role: "owner", user });
        router.push("/owner");
      }
    } catch (err) {
      if (err instanceof ApiError && err.errors.length) setFieldErrors(err.errors);
      else setError(errorText(err));
    } finally {
      setBusy(false);
    }
  };

  const field = (k: keyof typeof empty, label: string, props: React.InputHTMLAttributes<HTMLInputElement> = {}) => (
    <div>
      <label className="label" htmlFor={k}>{label}</label>
      <input id={k} className="input" value={form[k]} onChange={set(k)} {...props} />
      {fieldError(k) && <p className="mt-1 text-xs text-danger">{fieldError(k)}</p>}
    </div>
  );

  return (
    <div className="flex w-full flex-col items-center px-8 py-12">
      <form onSubmit={submit} className="flex w-full max-w-md flex-col gap-4">
        <h1 className="text-center font-display text-4xl font-extrabold">Join BarHop</h1>
        <p className="mb-2 text-center text-gray-600">Find out where it&apos;s lively tonight.</p>
        <RoleToggle role={role} onChange={setRole} />
        <div className="grid grid-cols-2 gap-4">
          {field("firstName", "First name", { required: true, maxLength: 50 })}
          {field("lastName", "Last name", { required: true, maxLength: 50 })}
        </div>
        {field("email", "Email", { type: "email", required: true, autoComplete: "email" })}
        {field("phoneNumber", "Mobile number", { required: true, pattern: "^\\+639\\d{9}$", title: "+639XXXXXXXXX", placeholder: "+639171234567" })}
        {role === "owner" && field("businessName", "Business name (optional)", { maxLength: 100 })}
        <div className="grid grid-cols-2 gap-4">
          {field("password", "Password", { type: "password", required: true, minLength: 8, maxLength: 72, autoComplete: "new-password" })}
          {field("confirm", "Confirm password", { type: "password", required: true, autoComplete: "new-password" })}
        </div>
        <p className="text-xs text-gray-500">We text booking updates to this number. Passwords need at least 8 characters.</p>
        {error && <Alert>{error}</Alert>}
        <button className="btn btn-info mt-2 w-full" type="submit" disabled={busy}>
          {busy ? "Creating account..." : "Create account"}
        </button>
        <p className="text-center text-gray-600">
          Already have an account? <Link className="link" href="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
}
