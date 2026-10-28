import { createContext, useCallback, useContext, useMemo, useState } from "react";

const DashboardViewContext = createContext({
  activeDashboardViewId: null,
  activateDashboardView: () => {},
  deactivateDashboardView: () => {},
});

export const DashboardViewProvider = ({ children }) => {
  const [activeDashboardViewId, setActiveDashboardViewId] = useState(null);

  const activateDashboardView = useCallback((id) => {
    setActiveDashboardViewId(id || null);
  }, []);

  const deactivateDashboardView = useCallback((id) => {
    setActiveDashboardViewId((current) => (current === id ? null : current));
  }, []);

  const value = useMemo(() => ({
    activeDashboardViewId,
    activateDashboardView,
    deactivateDashboardView,
  }), [activeDashboardViewId, activateDashboardView, deactivateDashboardView]);

  return (
    <DashboardViewContext.Provider value={value}>
      {children}
    </DashboardViewContext.Provider>
  );
};

export const useDashboardView = () => useContext(DashboardViewContext);
