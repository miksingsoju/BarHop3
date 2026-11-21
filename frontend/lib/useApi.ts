"use client";

import { useCallback, useEffect, useState } from "react";
import { api, errorText } from "./api";

interface State<T> {
  key: string | null;
  data?: T;
  error?: string;
}

/**
 * GETs `path` (null = don't fetch) and re-fetches when it changes or reload() is called.
 * loading is derived from whether the stored result belongs to the current path.
 */
export function useApi<T>(path: string | null) {
  const [version, setVersion] = useState(0);
  const [state, setState] = useState<State<T>>({ key: null });
  const key = path === null ? null : `${path}#${version}`;

  useEffect(() => {
    if (path === null || key === null) return;
    let cancelled = false;
    api
      .get<T>(path)
      .then((data) => !cancelled && setState({ key, data }))
      .catch((e) => !cancelled && setState({ key, error: errorText(e) }));
    return () => {
      cancelled = true;
    };
  }, [path, key]);

  const reload = useCallback(() => setVersion((v) => v + 1), []);
  const current = state.key === key;
  return {
    data: current ? state.data : undefined,
    // Keep showing the previous data while a reload is in flight.
    stale: state.data,
    error: current ? state.error : undefined,
    loading: path !== null && !current,
    reload,
  };
}
