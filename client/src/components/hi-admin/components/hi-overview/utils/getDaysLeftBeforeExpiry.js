// This function is not used in the entire application.
export const getDaysLeftBeforeExpiry = ({ expiry }) => {
  const oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
  const now = new Date();
  const expiryDate = new Date(expiry?.split("/").reverse().join(","));
  return Math.round(Math.abs((expiryDate - now) / oneDay));
};
