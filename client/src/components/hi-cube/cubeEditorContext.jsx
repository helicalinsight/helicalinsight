import React, { createContext, useContext, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";

const CubeEditorContext = createContext(null);

export function CubeEditorProvider({ cubeState, dispatch, variant = "cube", children }) {
  const value = useMemo(
    () => ({
      cubeState,
      dispatch,
      variant,
    }),
    [cubeState, dispatch, variant],
  );

  return (
    <CubeEditorContext.Provider value={value}>{children}</CubeEditorContext.Provider>
  );
}

export function useCubeEditorBindings() {
  const context = useContext(CubeEditorContext);
  const reduxDispatch = useDispatch();
  const cubeState = useSelector((store) => store.cube);

  if (context) {
    return {
      cubeState: context.cubeState,
      dispatch: context.dispatch,
      variant: context.variant || "cube",
    };
  }

  return {
    cubeState,
    dispatch: reduxDispatch,
    variant: "cube",
  };
}
