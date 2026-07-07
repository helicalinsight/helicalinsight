import React, { useState } from "react";

export function ChipsEditor({
  items = [],
  placeholder = "",
  kind = "",
  suggestions = [],
  scrollable = false,
  onChange,
}) {
  const [inputValue, setInputValue] = useState("");
  const listId = "chips-" + Math.random().toString(36).slice(2);

  const commit = (raw) => {
    const v = raw.trim().replace(/,$/, "");
    if (!v || items.includes(v)) {
      setInputValue("");
      return;
    }
    onChange?.([...items, v]);
    setInputValue("");
  };

  const removeAt = (index) => {
    const next = items.filter((_, i) => i !== index);
    onChange?.(next);
  };

  return (
    <div className={`me-chips${scrollable ? " me-chips-scrollable" : ""}`}>
      {items.map((it, i) => (
        <span key={`${it}-${i}`} className={`me-chip ${kind}`}>
          {it}
          <button
            type="button"
            className="me-chip-x"
            onClick={() => removeAt(i)}
          >
            &times;
          </button>
        </span>
      ))}
      <input
        className="me-chip-input"
        type="text"
        value={inputValue}
        placeholder={placeholder}
        list={suggestions.length ? listId : undefined}
        onChange={(e) => setInputValue(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === ",") {
            e.preventDefault();
            commit(inputValue);
          } else if (e.key === "Backspace" && !inputValue && items.length) {
            onChange?.(items.slice(0, -1));
          }
        }}
        onBlur={() => commit(inputValue)}
      />
      {suggestions.length > 0 && (
        <datalist id={listId}>
          {suggestions.map((s) => (
            <option key={s} value={s} />
          ))}
        </datalist>
      )}
    </div>
  );
}
