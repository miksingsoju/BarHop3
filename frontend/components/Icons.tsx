// Inline SVG icons (stand-ins for the Keenicons used by the Django templates).
type P = { className?: string };
const base = "shrink-0";

function Svg({ className, children, fill = "none" }: P & { children: React.ReactNode; fill?: string }) {
  return (
    <svg viewBox="0 0 24 24" fill={fill} stroke="currentColor" strokeWidth={1.8} strokeLinecap="round"
      strokeLinejoin="round" className={`${base} ${className ?? "size-5"}`} aria-hidden="true">
      {children}
    </svg>
  );
}

export const HomeIcon = (p: P) => <Svg {...p}><path d="M3 10.5 12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z" /></Svg>;
export const SearchIcon = (p: P) => <Svg {...p}><circle cx="11" cy="11" r="7" /><path d="m20 20-3.5-3.5" /></Svg>;
export const PinIcon = (p: P) => <Svg {...p}><path d="M12 21s-7-6.2-7-11.5a7 7 0 0 1 14 0C19 14.8 12 21 12 21z" /><circle cx="12" cy="9.5" r="2.5" /></Svg>;
export const ClockIcon = (p: P) => <Svg {...p}><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></Svg>;
export const StarIcon = ({ filled, ...p }: P & { filled?: boolean }) => (
  <Svg {...p} fill={filled ? "currentColor" : "none"}><path d="m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2L12 17.3 6.4 20.2l1.1-6.2L3 9.6l6.2-.9z" /></Svg>
);
export const HeartIcon = ({ filled, ...p }: P & { filled?: boolean }) => (
  <Svg {...p} fill={filled ? "currentColor" : "none"}><path d="M12 20s-7.5-4.6-9.2-9.3C1.6 7.4 3.8 4 7.3 4c2 0 3.5 1.1 4.7 2.7C13.2 5.1 14.7 4 16.7 4c3.5 0 5.7 3.4 4.5 6.7C19.5 15.4 12 20 12 20z" /></Svg>
);
export const FilterIcon = (p: P) => <Svg {...p}><path d="M4 5h16l-6 7.5V19l-4 1.5v-8z" /></Svg>;
export const UserIcon = (p: P) => <Svg {...p}><circle cx="12" cy="8" r="4" /><path d="M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6" /></Svg>;
export const UsersIcon = (p: P) => <Svg {...p}><circle cx="9" cy="8" r="3.5" /><path d="M2.5 20c1-3.5 3.5-5.5 6.5-5.5s5.5 2 6.5 5.5" /><path d="M16 4.5a3.5 3.5 0 0 1 0 7M18 14.8c1.8.8 3 2.6 3.5 5.2" /></Svg>;
export const LogoutIcon = (p: P) => <Svg {...p}><path d="M15 4h3a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-3" /><path d="M10 17l-5-5 5-5M5 12h11" /></Svg>;
export const PlusIcon = (p: P) => <Svg {...p}><path d="M12 5v14M5 12h14" /></Svg>;
export const MinusIcon = (p: P) => <Svg {...p}><path d="M5 12h14" /></Svg>;
export const CalendarIcon = (p: P) => <Svg {...p}><rect x="3" y="5" width="18" height="16" rx="2" /><path d="M3 10h18M8 3v4M16 3v4" /></Svg>;
export const TicketIcon = (p: P) => <Svg {...p}><path d="M3 8a2 2 0 0 0 2-2h14a2 2 0 0 0 2 2v2a2 2 0 0 0 0 4v2a2 2 0 0 0-2 2H5a2 2 0 0 0-2-2v-2a2 2 0 0 0 0-4z" /><path d="M14 6v12" strokeDasharray="2 2" /></Svg>;
export const PencilIcon = (p: P) => <Svg {...p}><path d="M4 20h4L19 9l-4-4L4 16z" /><path d="m13.5 6.5 4 4" /></Svg>;
export const TrashIcon = (p: P) => <Svg {...p}><path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3" /></Svg>;
export const CheckIcon = (p: P) => <Svg {...p}><path d="m5 12 5 5L20 7" /></Svg>;
export const XIcon = (p: P) => <Svg {...p}><path d="M6 6l12 12M18 6 6 18" /></Svg>;
export const ChevronDownIcon = (p: P) => <Svg {...p}><path d="m6 9 6 6 6-6" /></Svg>;
export const ChevronRightIcon = (p: P) => <Svg {...p}><path d="m9 6 6 6-6 6" /></Svg>;
export const PictureIcon = (p: P) => <Svg {...p}><rect x="3" y="4" width="18" height="16" rx="2" /><circle cx="8.5" cy="9.5" r="1.5" /><path d="m21 16-5-5L5 20" /></Svg>;
export const VerifiedIcon = (p: P) => (
  <svg viewBox="0 0 24 24" className={`${base} ${p.className ?? "size-4"}`} fill="currentColor" aria-hidden="true">
    <path d="M12 2 14.4 4l3.1-.3.9 3 2.7 1.6-1.1 2.9 1.1 2.9-2.7 1.6-.9 3-3.1-.3L12 22l-2.4-2-3.1.3-.9-3-2.7-1.6 1.1-2.9L2.9 9.9l2.7-1.6.9-3 3.1.3z" />
    <path d="m8.5 12 2.5 2.5 4.5-5" stroke="white" strokeWidth="1.8" fill="none" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);
export const StoreIcon = (p: P) => <Svg {...p}><path d="M4 10v10h16V10M3 4h18l-1 6H4zM9 20v-5h6v5" /></Svg>;
export const SparkIcon = (p: P) => <Svg {...p}><path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2.5 2.5M15.5 15.5 18 18M18 6l-2.5 2.5M8.5 15.5 6 18" /></Svg>;
