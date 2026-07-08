import { createContext, useContext } from "react";

const AgentNameContext = createContext({
  agentName: "",
  onAgentNameChange: undefined,
});

export function AgentNameProvider({ agentName, onAgentNameChange, children }) {
  return (
    <AgentNameContext.Provider value={{ agentName, onAgentNameChange }}>
      {children}
    </AgentNameContext.Provider>
  );
}

export function useAgentName() {
  return useContext(AgentNameContext);
}
