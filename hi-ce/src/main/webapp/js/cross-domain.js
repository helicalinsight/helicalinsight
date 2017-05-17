/* eslint-disable */
(function() {
    window.onload = function() {
        var iframe = document.getElementById('dashboard-canvas');

        iframe.onload =  function() {
            function tipMessage(event) {
                event.preventDefault();
                // document.getElementById("cacheRefresh").innerHTML = event.data;
                $('#cacheRefresh').attr("title", event.data);
            }

            window.addEventListener("message", tipMessage, true);
        }
    }
}).call(this);
