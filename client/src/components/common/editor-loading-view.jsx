import React from "react";
import LoadingBar from "./components/hi-loading-bar";
import { CustomCannedSkeleton } from "./custom-icons/CustomSkeletons/cannedReports/customCannedSkeleton";

export function EditorLoadingView({ handleAbort, className = "" }) {
  return (
    <div className={className} style={{ height: "100%", width: "100%" }}>
      <div style={{ padding: "10px" }}>
        <LoadingBar handleClick={handleAbort} />
      </div>
      <div style={{ height: "calc(100% - 60px)", margin: "20px" }}>
        <CustomCannedSkeleton canvasHeight={500} />
      </div>
    </div>
  );
}
