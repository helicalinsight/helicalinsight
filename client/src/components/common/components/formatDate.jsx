import moment from "moment";

const formatDate = (dateString, includeTime = true) => {
    console.log('includeTime', includeTime)
  try {
    let dateFormat = "dddd, MMMM Do, YYYY, h:mm:ss a";
    if (!includeTime) dateFormat = "dddd, MMMM Do, YYYY";
    return moment(new Date(dateString)).format(dateFormat);
  } catch (error) {
    console.log('Error in formatDate function', error)
  }
};

export default formatDate;
