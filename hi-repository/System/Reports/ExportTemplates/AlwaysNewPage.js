/**
 * Created by helical021 on 12/25/2016.
 */

function doExecute() {
    var message = reportPage.evaluate(function () {
        var myElement = document.getElementsByClassName("grid-stack-item");
        for (var i = myElement.length; i--;) {
            myElement[i].style.pageBreakAfter = "always";
        }
        return myElement.length
    });

    setTimeout(function () {
        console.log("waiting for the frames")
    }, 10000);
}