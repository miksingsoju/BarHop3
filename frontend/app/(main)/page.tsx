"use client";

import { useMemo, useState } from "react";
import { BarCard } from "@/components/BarCard";
import { Alert, Empty, Spinner } from "@/components/Feedback";
import { FilterIcon, PinIcon, SearchIcon } from "@/components/Icons";
import { LikeButton } from "@/components/LikeButton";
import { apiPath } from "@/lib/api";
import { useSession } from "@/lib/session";
import { METRO_MANILA_CITIES, type ActivityStatus, type BarSummary, type Like } from "@/lib/types";
import { useApi } from "@/lib/useApi";

const STATUSES: ActivityStatus[] = ["QUIET", "MODERATE", "LIVELY", "PACKED"];

export default function HomePage() {
  const { hopper, owner } = useSession();
  const [searchBy, setSearchBy] = useState<"bar" | "city">("bar");
  const [text, setText] = useState("");
  const [submitted, setSubmitted] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [status, setStatus] = useState<ActivityStatus | "">("");
  const [city, setCity] = useState("");
  const [sort, setSort] = useState<"likes" | "rating">("likes");

  const cityQuery = searchBy === "city" ? submitted : city;
  const path = apiPath("/bars", { search: searchBy === "bar" ? submitted : "", city: cityQuery, status, sort });
  const bars = useApi<BarSummary[]>(path);
  const likes = useApi<Like[]>(hopper ? apiPath("/likes", { hopperId: hopper.id }) : null);

  // Live updates after liking, without refetching the whole feed.
  const [overrides, setOverrides] = useState<Record<number, { like?: Like; count: number; status: ActivityStatus }>>({});
  const likeByBar = useMemo(() => {
    const m = new Map<number, Like>();
    likes.data?.forEach((l) => m.set(l.barId, l));
    return m;
  }, [likes.data]);

  const list = (bars.data ?? bars.stale ?? []).map((b) => {
    const o = overrides[b.id];
    return o ? { ...b, activeLikeCount: o.count, activityStatus: o.status } : b;
  });

  return (
    <>
      {/* Hero with the curved backdrop from the old design */}
      <div className="relative">
        <svg className="absolute inset-x-0 top-0 -z-10 h-52 w-full text-gray-400 sm:h-72" preserveAspectRatio="none" viewBox="0 0 1280 306" fill="none" aria-hidden="true">
          <path d="M1280 -138H0V256.715C501.579 322.413 781.858 322.444 1280 256.715V-138Z" fill="currentColor" />
        </svg>
        <div className="flex h-72 flex-col justify-center px-6">
          <h1 className="text-center font-display text-3xl font-bold sm:text-4xl">Find active bars near you.</h1>
          <p className="mt-2 text-center font-display text-gray-700">See where it&apos;s lively right now, then book a table.</p>
          <form className="mx-auto mt-8 flex w-full max-w-xl gap-3"
            onSubmit={(e) => { e.preventDefault(); setSubmitted(text.trim()); }}>
            <div className="flex flex-1 items-center rounded-lg bg-gray-300">
              <button type="button" title={searchBy === "bar" ? "Searching by bar name. Click to search by city." : "Searching by city. Click to search by bar name."}
                onClick={() => { setSearchBy(searchBy === "bar" ? "city" : "bar"); setSubmitted(""); setText(""); }}
                className="px-3 text-gray-600 hover:text-info">
                {searchBy === "bar" ? <SearchIcon /> : <PinIcon />}
              </button>
              <input value={text} onChange={(e) => setText(e.target.value)} aria-label="Search"
                placeholder={searchBy === "bar" ? "Search by Bar" : "Search by City (e.g. Makati)"}
                className="w-full bg-transparent py-2.5 pr-4 text-sm outline-none placeholder:text-gray-500" />
            </div>
            <button type="button" onClick={() => setShowFilters(!showFilters)} aria-label="Filters" aria-pressed={showFilters}
              className={`flex size-11 items-center justify-center rounded-lg transition ${showFilters ? "bg-info text-white" : "bg-gray-300 text-gray-700 hover:bg-info hover:text-white"}`}>
              <FilterIcon />
            </button>
          </form>
        </div>
      </div>

      <div className="mx-auto flex max-w-5xl items-center justify-between px-6 md:px-12">
        <h2 className="font-display text-xl">{submitted ? `Results for “${submitted}”` : "All Bars"}</h2>
        {submitted && <button className="link text-sm" onClick={() => { setSubmitted(""); setText(""); }}>Clear search</button>}
      </div>

      <div className="mx-auto mt-6 flex max-w-5xl flex-col-reverse gap-8 px-6 md:flex-row md:px-12">
        <div className="flex flex-1 flex-col gap-6">
          {bars.error && <Alert>{bars.error}</Alert>}
          {bars.loading && !bars.stale && <Spinner label="Finding bars..." />}
          {!bars.loading && !bars.error && list.length === 0 && (
            <div className="card"><Empty title="No bars available">Try a different search or filter.</Empty></div>
          )}
          {list.map((b) => (
            <BarCard key={b.id} bar={b} action={hopper && (
              <LikeButton barId={b.id} hopperId={hopper.id}
                like={b.id in overrides ? overrides[b.id].like : likeByBar.get(b.id)}
                onChange={(u) => setOverrides((o) => ({ ...o, [u.barId]: { like: u.like, count: u.count, status: u.status } }))} />
            )} />
          ))}
          {!hopper && !owner && list.length > 0 && (
            <p className="text-center text-sm text-gray-500">Log in as a Bar Hopper to like bars and book tables.</p>
          )}
        </div>

        {showFilters && (
          <aside className="card h-fit w-full shrink-0 p-6 md:w-64">
            <h3 className="mb-4 font-display">Filters</h3>
            <fieldset className="mb-5">
              <legend className="label">Activity</legend>
              <div className="flex flex-wrap gap-2">
                {(["", ...STATUSES] as const).map((s) => (
                  <button key={s || "all"} type="button" onClick={() => setStatus(s)}
                    className={`badge cursor-pointer px-3 py-1.5 ${status === s ? "bg-info text-white" : "bg-gray-200 text-gray-700 hover:bg-info-light hover:text-info"}`}>
                    {s ? s[0] + s.slice(1).toLowerCase() : "Any"}
                  </button>
                ))}
              </div>
            </fieldset>
            {searchBy === "bar" && (
              <div className="mb-5">
                <label className="label" htmlFor="city">City</label>
                <select id="city" className="input" value={city} onChange={(e) => setCity(e.target.value)}>
                  <option value="">All of Metro Manila</option>
                  {METRO_MANILA_CITIES.map((c) => <option key={c}>{c}</option>)}
                </select>
              </div>
            )}
            <div>
              <label className="label" htmlFor="sort">Sort by</label>
              <select id="sort" className="input" value={sort} onChange={(e) => setSort(e.target.value as "likes" | "rating")}>
                <option value="likes">Liveliest first</option>
                <option value="rating">Highest rated</option>
              </select>
            </div>
          </aside>
        )}
      </div>
    </>
  );
}
