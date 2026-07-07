import actionTypes from "./actionTypes";

export const toggleNotifications = () => {
  return { type: actionTypes.TOGGLE_NOTIFICATIONS };
};
export const updateNotificationData = (obj) => {
  return { type: actionTypes.UPDATE_NOTIFICATION_DATA, payload: obj };
};
export const deleteNotificationItem = (id) => {
  return { type: actionTypes.DELETE_NOTIFICATION_ITEM, payload: id };
};
export const resetNotificationData = () => {
  return { type: actionTypes.RESET_NOTIFICATION_DATA };
};
export const updateNotificationItem = (obj) => {
  return { type: actionTypes.UPDATE_NOTIFICATION_ITEM, payload: obj };
};
