function searchAndReplace(search,replace) {
    // Get all of the document components (e.g. body, header, etc.)
   // Prompt the user to enter the search string and the replacement string
if(process.env.NODE_ENV !== "test"){
var searchString = prompt("Enter the search string:");
var replaceString = prompt("Enter the replace string:");
}
if(process.env.NODE_ENV === "test"){
    searchString=search
    replaceString=replace 
}
// Get all the elements in the document
var elements = document.getElementsByTagName("*");

// Loop through each element and replace the text content
for (var i = 0; i < elements.length; i++) {
  var element = elements[i];

  // Check if the element has any text content
  if (element.childNodes.length && element.childNodes[0].nodeType === Node.TEXT_NODE) {
    // Replace the text content with the search string replaced by the replace string
    element.childNodes[0].textContent = element.childNodes[0].textContent.replace(new RegExp(searchString, 'g'), replaceString);
  }
}

  }
  
  export default searchAndReplace
  
  
  
  