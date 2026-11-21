"use client";

import { use } from "react";
import { Breadcrumbs, PageTitle } from "@/components/Feedback";
import { BarForm } from "../../../BarForm";
import { OwnedBar } from "../../../OwnedBar";

export default function EditBarPage({ params }: PageProps<"/owner/bars/[id]/edit">) {
  const { id } = use(params);
  return (
    <OwnedBar barId={Number(id)}>
      {(bar) => (
        <>
          <Breadcrumbs items={[{ label: "My Bars", href: "/owner" }, { label: bar.name, href: `/bars/${bar.id}` }, { label: "Edit" }]} />
          <div className="mx-auto mt-8 flex max-w-3xl flex-col gap-6 px-6">
            <PageTitle>Edit {bar.name}</PageTitle>
            <BarForm bar={bar} />
          </div>
        </>
      )}
    </OwnedBar>
  );
}
