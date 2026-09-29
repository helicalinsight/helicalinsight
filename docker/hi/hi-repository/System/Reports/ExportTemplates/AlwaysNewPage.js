/**
 * Created by helical021 on 12/25/2016.
 */

(function doExecute() {
  
        var myElement = document.getElementsByClassName("react-grid-item");
        for (var i = myElement.length; i--;) {
            myElement[i].style.pageBreakAfter = "always";
        }

  
        return myElement.length
    

})()
