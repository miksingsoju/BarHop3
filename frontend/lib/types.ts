// Shapes of the BarHop REST API (see docs/proposal.txt).

export type ActivityStatus = "QUIET" | "MODERATE" | "LIVELY" | "PACKED";
export type TableType = "SOLO" | "GROUP" | "VIP";
export type ReservationStatus = "PENDING" | "CONFIRMED" | "CANCELLED" | "COMPLETED" | "NO_SHOW";

export interface Hopper {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  createdAt: string;
}

export interface Owner extends Hopper {
  businessName: string | null;
  barCount: number;
}

export interface BarSummary {
  id: number;
  name: string;
  city: string;
  address: string;
  averageRating: number;
  reviewCount: number;
  activeLikeCount: number;
  activityStatus: ActivityStatus;
  imageUrl: string | null;
}

export interface BarDetail extends BarSummary {
  ownerId: number;
  ownerName: string;
  description: string | null;
  openingTime: string;
  closingTime: string;
  capacity: number;
  amenities: string | null;
  createdAt: string;
}

export interface BarTable {
  id: number;
  barId: number;
  label: string;
  tableType: TableType;
  capacity: number;
  rate: number;
  active: boolean;
}

export interface BarEvent {
  id: number;
  barId: number;
  barName: string;
  title: string;
  description: string | null;
  startDateTime: string;
  endDateTime: string;
  coverCharge: number | null;
}

export interface Reservation {
  id: number;
  hopperId: number;
  barId: number;
  barName: string;
  tableId: number;
  tableLabel: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  guestCount: number;
  contactName: string;
  contactNumber: string;
  status: ReservationStatus;
  agreedToPolicy: boolean;
  createdAt: string;
}

export interface Review {
  id: number;
  hopperId: number;
  hopperName: string;
  barId: number;
  barName: string;
  rating: number;
  comment: string | null;
  visitDate: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Like {
  id: number;
  hopperId: number;
  barId: number;
  barName: string;
  likedAt: string;
  expiresAt: string;
  barActiveLikeCount: number;
  barActivityStatus: ActivityStatus;
}

export interface MessageResponse {
  message: string;
  [key: string]: unknown;
}

export const METRO_MANILA_CITIES = [
  "Caloocan", "Las Piñas", "Makati", "Malabon", "Mandaluyong", "Manila", "Marikina", "Muntinlupa",
  "Navotas", "Parañaque", "Pasay", "Pasig", "Pateros", "Quezon City", "San Juan", "Taguig", "Valenzuela",
];
