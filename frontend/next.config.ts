import type { NextConfig } from "next";

// The Spring Boot backend (Eclipse project in ../backend). Override with BACKEND_URL.
const backend = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  // Tailwind CSS v4 through its Turbopack loader.
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
  // The browser calls /api/... on the Next.js origin and Next.js forwards it to the backend,
  // so no CORS setup is needed.
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${backend}/api/:path*` }];
  },
};

export default nextConfig;
