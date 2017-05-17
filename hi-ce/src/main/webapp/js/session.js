/* eslint-disable */
(function() {
    $(document).ready(function() {

        try {
            var notification = document.createElement("div");

            notification.innerHTML = '<div style="position:fixed; right:15px; bottom:15px; z-index: 9999">'+
                '<div class="alert alert-warning">' +
                '<button type="button" class="close" id="session-alert-close"><span>&times;</span></button>' +
                    '<p> Your session will expire in:' + '<span id="session-timer">0:00</span></p>'
                '</div>' +
            '</div>';

            var UIBlock = document.createElement("div");
            UIBlock.id = "session-timeout-block";
            UIBlock.classList.add("modal");
            UIBlock.classList.add("fade");
            UIBlock.innerHTML = '<div class="modal-dialog">' +
                '<div class="modal-content">' +
                    '<div class="modal-header">' +
                        '<h4 class="modal-title">Session Expired!</h4>' +
                    '</div>' +
                    '<div class="modal-body">' +
                            '<p>Your session has expired. Please login to continue</p>' +
                    '</div>' +
                    '<div class="modal-footer">' +
                        '<button type="button" class="btn btn-primary" id="timeout-redirect">OK</button>' +
                    '</div>' +
                '</div>' +
            '</div>';
		
            document.body.appendChild(UIBlock);
			function getCookies() {
                var cookies = document.cookie.split(";"),
                    cookiesObj = {};
                for (var i = 0, l = cookies.length; i < l; i++) {
                    var cookie = cookies[i].split("=");
                    if (cookie.length !== 2) { continue; }
                    cookiesObj[cookie[0].trim()] = cookie[1].trim();
                }
                return cookiesObj;
            }

            var dashboardDismissed = 0,
                dashboardDismissedThreshold = 5,
                maxThreshold = 3 * 60 * 1000, //3 mins
                minThreshold = 25 * 1000, //25 secs
                timeOut = 5 * 1000,
                ExpiryTime= Math.abs(getCookies().sessionExpiry),
				StartTime= Math.abs(getCookies().serverTime),
				Time= Math.abs(getCookies().serverTime),
                dismissButton = notification.querySelector("#session-alert-close");
				
			//console.log("ExpiryTime: " +ExpiryTime);
			//console.log("StartTime: " +StartTime);
			//console.log("Time: " +Time);

            dismissButton.addEventListener("click", function() {
                if (document.body.contains(notification)) {
                    document.body.removeChild(notification);
                }
                dashboardDismissed = dashboardDismissedThreshold;
            });

            function appendNotification() {
                if (!document.body.contains(notification)) {
                    document.body.appendChild(notification);
                }
            }

            function removeNotification() {
                if (document.body.contains(notification)) {
                    document.body.removeChild(notification);
                }
            }

            var checkSession = function () {
                var cookies = getCookies(),
                    sessionExpiry = Math.abs(cookies.sessionExpiry),
                    serverTime= Math.abs(cookies.serverTime);
				if(Time <= sessionExpiry)
				{
					if(StartTime == serverTime && ExpiryTime == sessionExpiry)
					{
						Time = Time + 1000;
					}
					else
					{
						StartTime = Time = serverTime,
						ExpiryTime = sessionExpiry

					}
				}
				else
				{
						appendNotification();
				}

                var timeLeft = sessionExpiry - Time;
				//console.log("timeLeft: "+timeLeft);
                if ((timeLeft < maxThreshold && dashboardDismissed <= 0) || timeLeft < minThreshold) {

                    appendNotification();

                    if (timeLeft <= timeOut) {
                        removeNotification();
                        $("#session-timeout-block").modal({ backdrop: "static" });

                        $("#timeout-redirect").one("click",  function () {
                            window.location.reload();
                        });
                        return;
                    }

                    var min = Math.floor(timeLeft / 60000),
                        sec = Math.floor((timeLeft/1000) % 60);
                        sec = sec < 10 ? "0" + sec : sec;
                    document.getElementById("session-timer").innerHTML = min + ":" + sec;
                } else {
                    removeNotification();

                    if(dashboardDismissed > 0) {
                        dashboardDismissed--;
                    }
                }
                setTimeout(checkSession, 999);
            };
            checkSession();
        } catch (e) {
			console.warn(e);
            console.warn("failed to start session notifier");
        }
    });
})();
