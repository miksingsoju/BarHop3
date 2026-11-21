"use client";

import { Breadcrumbs, PageTitle } from "@/components/Feedback";
import { BarForm } from "../../BarForm";

export default function NewBarPage() {
  return (
    <>
      <Breadcrumbs items={[{ label: "My Bars", href: "/owner" }, { label: "Create Bar" }]} />
      <div className="mx-auto mt-8 flex max-w-3xl flex-col gap-6 px-6">
        <PageTitle>List a Bar</PageTitle>
        <BarForm />
      </div>
    </>
  );
}
