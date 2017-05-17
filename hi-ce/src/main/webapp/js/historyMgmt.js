window.onload = function () {
    if (typeof InstallTrigger !== 'undefined') {
        var frame1 = document.getElementById('dashboard-canvas');
        history.pushState(null, null);
        window.addEventListener('popstate', function (event) {
            event.preventDefault();
            if (frame1) {
                frame1.src = "";
            }
            history.pushState(null, null);
        }, false);
    } else if (/*@cc_on!@*/false || !!document.documentMode) {
        var iframeCopy = document.getElementById('dashboard-canvas');
        window.history = null;
        history.pushState(null, null);
        window.addEventListener('popstate', function (event) {
            if (iframeCopy) {
                iframeCopy.src = "";
            }
            history.pushState(null, null);
        }, false);
    }
};