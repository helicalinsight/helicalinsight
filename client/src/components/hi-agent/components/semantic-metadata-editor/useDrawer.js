import React, { useState } from "react";

export const useDrawerWithDraft = (createEmptyDraft) => {
  const [isOpen, setIsOpen] = useState(false);
  const [draft, setDraft] = useState(null);

  const openDrawer = () => {
    setDraft(createEmptyDraft());
    setIsOpen(true);
  };

  const closeDrawer = () => {
    setIsOpen(false);
    setDraft(null);
  };

  return { isOpen, draft, setDraft, openDrawer, closeDrawer };
}