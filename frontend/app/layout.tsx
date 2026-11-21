import type { Metadata } from "next";
import { Inter, Poppins } from "next/font/google";
import { SessionProvider } from "@/lib/session";
import "./globals.css";

const inter = Inter({ variable: "--font-inter", subsets: ["latin"] });
const poppins = Poppins({
  variable: "--font-poppins",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800"],
});

export const metadata: Metadata = {
  title: { default: "BarHop", template: "BarHop - %s" },
  description: "Find lively bars around Katipunan, Maginhawa, Poblacion and BGC, and reserve a table.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en" className={`${inter.variable} ${poppins.variable}`}>
      <body className="min-h-screen">
        <SessionProvider>{children}</SessionProvider>
      </body>
    </html>
  );
}
