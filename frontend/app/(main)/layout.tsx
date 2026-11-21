import { Navbar } from "@/components/Navbar";

export default function MainLayout({ children }: LayoutProps<"/">) {
  return (
    <>
      <Navbar />
      <main className="pb-24 sm:pt-[75px] sm:pb-16">{children}</main>
    </>
  );
}
