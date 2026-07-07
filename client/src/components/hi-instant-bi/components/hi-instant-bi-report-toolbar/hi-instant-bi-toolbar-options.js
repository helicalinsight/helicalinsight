export const handleInstantBIToolbarMenuClick = ({
  key,
  setEditModalVisible,
}) => {
  switch (key) {
    case "edit":
      setEditModalVisible(true);
      break;
    default:
      break;
  }
};
