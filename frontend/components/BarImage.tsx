"use client";

import { useState } from "react";
import { PictureIcon } from "./Icons";

/** Bar photo with the old gray placeholder when there is no image (or it fails to load). */
export function BarImage({ src, alt, className }: { src: string | null; alt: string; className?: string }) {
  const [failedSrc, setFailedSrc] = useState<string | null>(null);
  const show = src && failedSrc !== src;
  return (
    <div className={`flex items-center justify-center overflow-hidden bg-gray-400 ${className ?? ""}`}>
      {show ? (
        // eslint-disable-next-line @next/next/no-img-element -- owner-supplied URLs from any host
        <img src={src} alt={alt} className="size-full object-cover" onError={() => setFailedSrc(src)} />
      ) : (
        <PictureIcon className="size-12 text-gray-600" />
      )}
    </div>
  );
}
