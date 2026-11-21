import { StarIcon } from "./Icons";

export function Stars({ rating, className }: { rating: number; className?: string }) {
  return (
    <span className={`inline-flex text-warning ${className ?? ""}`} aria-label={`${rating} out of 5 stars`}>
      {[1, 2, 3, 4, 5].map((i) => (
        <StarIcon key={i} filled={i <= Math.round(rating)} className="size-4" />
      ))}
    </span>
  );
}

export function StarInput({ value, onChange }: { value: number; onChange: (v: number) => void }) {
  return (
    <div className="flex items-center gap-1" role="radiogroup" aria-label="Rating">
      {[1, 2, 3, 4, 5].map((i) => (
        <button key={i} type="button" role="radio" aria-checked={value === i} aria-label={`${i} star${i > 1 ? "s" : ""}`}
          onClick={() => onChange(value === i ? 0 : i)} className="text-warning transition hover:scale-110">
          <StarIcon filled={i <= value} className="size-7" />
        </button>
      ))}
      <span className="ml-2 text-sm text-gray-600">{value} / 5</span>
    </div>
  );
}
