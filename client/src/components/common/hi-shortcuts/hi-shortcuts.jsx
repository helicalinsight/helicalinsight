import { useSelector } from "react-redux";
import { Dropdown, Typography } from "antd";
import "./hi-shortcuts.scss";

const ShortCutText = (props) => {
  const { text, menuItem, id, scLocation, navItem, dropdownItem, isButton } = props;

  const altTriggered = useSelector((state) => state.app.altTriggered);
  const keysPressed = useSelector((state) => state.app.keysPressed);
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);

  // Dynamic class name for shortcut text
  let shortTextClassName = "hi-shortcut-text";
  if (menuItem) shortTextClassName += " menu-item";
  if (navItem) shortTextClassName += " navbar-item";
  if (dropdownItem) shortTextClassName += " dropdown-item";
  if (isButton) shortTextClassName += " button-item";

  // Check if shortcut key is triggered
  const shortcutKeyTriggered = 
    (text && Array.isArray(keysPressed) && keysPressed[0] === "Alt" && scLocation === currentSCLocation) ||
    (text && altTriggered && scLocation === currentSCLocation);

  // Return shortcut text if triggered, otherwise return children
  return (
    <span
      id={id}
      className="hi-shortcut-container"
      ref={props?.children?.ref && props?.children?.ref}
    >
      {shortcutKeyTriggered ? (
        <>
          <span className={shortTextClassName}>{text}</span>
          {props.children}
        </>
      ) : (
        props.children
      )}
    </span>
  );
};

export default ShortCutText;
