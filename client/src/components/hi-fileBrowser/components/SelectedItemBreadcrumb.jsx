import { HomeOutlined } from "@ant-design/icons";
import { useState, useEffect } from "react";

const SelectedItemBreadcrumb = ({ pathItem, action }) => {
  const [modifiedText, setModifiedText] = useState();

  useEffect(() => {
    let finalText = " > ";
    const items = pathItem?.path?.split("/");
    items?.forEach((bc, i) => {
      if (i !== items.length - 1) {
        finalText = finalText + bc + " > ";
      } else {
        finalText = finalText + bc;
      }
    });
    setModifiedText(finalText.split('"').join(""));
  }, [pathItem]);

  return (
    <div
      ref={(node) => {
        if (node && action === "rename") {
          node.style.setProperty("inline-size", "350px", "important");
          node.style.setProperty("word-break", "break-all");
          node.style.setProperty("height", "auto", "important");
        }
      }}
    >
      <HomeOutlined /> {modifiedText}
    </div>
  );
};

export { SelectedItemBreadcrumb };
