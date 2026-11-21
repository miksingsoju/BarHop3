import { RequireRole } from "@/components/RequireRole";

export default function OwnerLayout({ children }: LayoutProps<"/owner">) {
  return <RequireRole role="owner">{children}</RequireRole>;
}
