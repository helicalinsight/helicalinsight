//events.js
// This custom event is used for printing of pdf in the backend  
const publish=(eventName, data={changeTime:new Date(),status:"initial",apiCalls:0})=> {
    const event = new CustomEvent(eventName, { detail: data });
    event.data=data;
    window.dispatchEvent(event);
  }
  
  export { publish};