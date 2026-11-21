// Small fetch wrapper for the BarHop API. Requests go to /api/... on this origin,
// which next.config.ts rewrites to the Spring Boot backend.

export interface FieldError {
  field: string;
  message: string;
}

export class ApiError extends Error {
  status: number;
  errors: FieldError[];

  constructor(status: number, message: string, errors: FieldError[] = []) {
    super(message);
    this.status = status;
    this.errors = errors;
  }

  /** "Validation failed: email must be a well-formed email address; ..." */
  get detail(): string {
    if (this.errors.length === 0) return this.message;
    return this.errors.map((e) => `${e.field} ${e.message}`).join("; ");
  }
}

async function request<T>(method: "GET" | "POST", path: string, body?: unknown): Promise<T> {
  let res: Response;
  try {
    res = await fetch(path, {
      method,
      headers: body !== undefined ? { "Content-Type": "application/json" } : undefined,
      body: body !== undefined ? JSON.stringify(body) : undefined,
      cache: "no-store",
    });
  } catch {
    throw new ApiError(0, "Can't reach the BarHop server. Is the backend running?");
  }

  const text = await res.text();
  let data: unknown = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = null;
    }
  }

  if (!res.ok) {
    const err = (data ?? {}) as { message?: string; errors?: FieldError[] };
    const fallback = res.status >= 500 ? "Can't reach the BarHop server. Is the backend running?" : res.statusText;
    throw new ApiError(res.status, err.message ?? fallback, err.errors ?? []);
  }
  return data as T;
}

/** Builds "/api/x?a=1&b=2", skipping empty values. */
export function apiPath(path: string, query?: Record<string, string | number | boolean | null | undefined>) {
  const params = new URLSearchParams();
  for (const [k, v] of Object.entries(query ?? {})) {
    if (v !== undefined && v !== null && v !== "") params.set(k, String(v));
  }
  const qs = params.toString();
  return `/api${path}${qs ? `?${qs}` : ""}`;
}

export const api = {
  get: <T>(path: string) => request<T>("GET", path),
  post: <T>(path: string, body?: unknown) => request<T>("POST", path, body),
};

export function errorText(e: unknown): string {
  if (e instanceof ApiError) return e.detail;
  return e instanceof Error ? e.message : "Something went wrong.";
}
