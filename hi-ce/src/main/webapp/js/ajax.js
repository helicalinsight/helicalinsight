/* eslint-disable */
(function () {

    var elapsedTime, requestTimer;

    $.ajaxSetup({
        type: "POST"
    });


    /*
     *       Holders for keeping track of elpased time of request
     */
    requestTimer = null;

    elapsedTime = 0;


    /*
     *       Holds the XHR objects while an AJAX request is in progress
     */

    $.ajaxQueue = $.ajaxQueue || [];

    var _requests = {};
    
$.notifyDefaults({
        position: "fixed",
        placement: {
            from: "bottom",
            align: "right"
        },
        mouse_over: "pause",
        delay: 3000,
        timer: 1000
    });

    /*
     *       Method to abort all in progress AJAX requests
     */

    _requests.abortAll = function () {
        var i, len, ref, request;
        ref = $.ajaxQueue;
        for (i = 0, len = ref.length; i < len; i++) {
            request = ref[i];
            if (typeof request.abort === 'function') {
                request.abort();
            }
        }
        if ($.ajaxQueue.length > 0) {
            $.ajaxQueue = [];
        }
    };


    /*
     Removes specific XHR object from queue
     */

    _requests.remove = function (identifier) {
        $.ajaxQueue = _.filter($.ajaxQueue, function (obj) {
            return obj.identifier !== identifier;
        });
    };

    _reset_loading_panel = function () {
        $("#hdi-blockUI").modal('hide');
        clearInterval(requestTimer);
        elapsedTime = 0;
        $('#elpased_time').html('0:00');
    };

    // Cookies function
    getCookies = function () {
        var cookies = document.cookie.split(";"),
            cookiesObj = {};
        for (var i = 0, l = cookies.length; i < l; i++) {
            var cookie = cookies[i].split("=");
            if (cookie.length !== 2) {
                continue;
            }
            cookiesObj[cookie[0].trim()] = cookie[1].trim();
        }
        return cookiesObj;
    }

    var setCookies = getCookies(),
        serverTime = Math.abs(setCookies.serverTime),
        clientTime = Math.abs(window.moment ? moment.utc().format("x") : (new Date()).getTime()),
        copyServerTime = serverTime;

    $(document).ready(function () {
        var parseDate = moment(parseInt(serverTime, 10)),
            readableDate = parseDate.format('[Last cached on:] LLLL');

        if (!$.ajaxQueue || $.ajaxQueue.length === 0) {
            $('[data-refresh]').off("click").hover(function () {
                $(this).tooltip({'placement': 'bottom'}).attr('data-original-title', readableDate).tooltip('fixTitle').tooltip('show');
            });
        }

    });


    if (window.__HI_cache_notify__) clearInterval(window.__HI_cache_notify__);
    window.__HI_cache_notify__ = setInterval(function () {

        if (!$.ajaxQueue || $.ajaxQueue.length === 0) {
            var parseDate = moment(parseInt(serverTime, 10)),
                readableDate = parseDate.format('[Last cached on:] LLLL');
            copyServerTime = (copyServerTime + 1000),
                noLMdiff = copyServerTime - serverTime,
                delta = serverTime + noLMdiff,
                parseST = moment(parseInt(delta, 10));
            var currentRefreshTime = parseDate.from(parseST);
            var updatedTime = readableDate + ' (' + currentRefreshTime + ")";
            var wrapp = currentRefreshTime;
            $("#cacheRefresh").text(" Last Cached: " + currentRefreshTime);

            //window.parent.postMessage(wrapp, "*");
        }
    }, 1000);

    /*
     AJAX setup
     */

    $(document).on("ajaxStart", function () {

        /*
         Register a handler to be called when the first Ajax request begins
         1. Show the loading panel
         2. Start the timer for 'Elapsed Time'
         */
        $(document).trigger($.Event('first.request.hdi'));
        requestTimer = setInterval(function () {
            var minutes, seconds;
            ++elapsedTime;
            seconds = (elapsedTime % 60) > 9 ? (elapsedTime % 60).toString() : '0' + (elapsedTime % 60).toString();
            minutes = Math.floor(elapsedTime / 60).toString();
            $('#elpased_time').html(minutes + ':' + seconds);
        }, 1000);
    }).on("ajaxSend", function (event, xhr, options) {

        /*
         Attach a function to be executed before an Ajax request is sent.
         1. Update number of active requests
         2. Push XHR object in queue
         */

        currentTime = Math.abs((window.moment ? moment.utc().format("x") : (new Date()).getTime()));
        xhr.setRequestHeader('currentTime', currentTime);
        $(document).trigger($.Event('start.request.hdi'));
        $('#request-count').html($.active);
        if ($.ajaxQueue.length > 0) {
            $.ajaxQueue.push(xhr);
        }
    }).on("ajaxSuccess", function (event, xhr, options) {
        /*
         Register a handler to be called when Ajax requests complete successfully
         1. Remove XHR object from queue
         */
        xhr.done(function (data) {
            if (!options._noBlock && _.isObject(data) && data.hasOwnProperty("status")) {
                var icon = "fa fa-check",
                    type = "success";

                if (data.status === 0) {
                    icon = "fa fa-times";
                    type = "danger";
                }

                var message = "";

                if (data.response.message) {
                    message += data.response.message;
                    $.notify({message: message, icon: icon}, {type: type});
                }
            }

            var setCookies = getCookies(),
                serverTime = Math.abs(setCookies.serverTime),
                clientTime = Math.abs(window.moment ? moment.utc().format("x") : (new Date()).getTime()),
                copyServerTime = serverTime;
            if (_.isObject(data) && data.hasOwnProperty("lastModified")) {
                var lastModified = moment(parseInt(data.lastModified, 10)),
                    lastUpdated = lastModified.format('[Last cached on:] LLLL');
                $('[data-refresh]').off("click").hover(function () {
                    $(this).tooltip({'placement': 'bottom'}).attr('data-original-title', lastUpdated).tooltip('fixTitle').tooltip('show');
                });
            }
            else {
                var parseDate = moment(parseInt(serverTime, 10)),
                    readableDate = parseDate.format('[Last cached on:] LLLL');
                $('[data-refresh]').off("click").hover(function () {
                    $(this).tooltip({'placement': 'bottom'}).attr('data-original-title', readableDate).tooltip('fixTitle').tooltip('show');
                });
            }
            if (window.__HI_cache_notify__) clearInterval(window.__HI_cache_notify__);
            window.__HI_cache_notify__ = setInterval(function () {

                if (_.isObject(data) && data.hasOwnProperty("lastModified")) {
                    copyServerTime = (copyServerTime + 1000);
                    var lastModified = moment(parseInt(data.lastModified, 10)),
                        lastModifiedDiff = copyServerTime - lastModified,
                        deltaTime = lastModified + lastModifiedDiff,
                        parseLMDiff = moment(parseInt(deltaTime, 10)),
                        lastUpdated = lastModified.format('[Last cached on:] LLLL');

                    var timeFrom = lastModified.from(parseLMDiff);
                    var lastDate = lastUpdated + ' (' + timeFrom + ")";

                    $("#cacheRefresh").text(" Last Cached: " + timeFrom);

                    //window.parent.postMessage(timeFrom, "*");
                } else {
                    var parseDate = moment(parseInt(serverTime, 10)),
                        readableDate = parseDate.format('[Last cached on:] LLLL');
                    copyServerTime = (copyServerTime + 1000),
                        noLMdiff = copyServerTime - serverTime,
                        delta = serverTime + noLMdiff,
                        parseST = moment(parseInt(delta, 10));

                    var currentRefreshTime = parseDate.from(parseST);
                    var updatedTime = readableDate + ' (' + currentRefreshTime + ")";
                    var wrapp = currentRefreshTime;

                    $("#cacheRefresh").text(" Last Cached: " + currentRefreshTime);

                    //window.parent.postMessage(wrapp, "*");
                }
            }, 1000);

            // var timeChanger = setInterval(tooltip_notifier, 1000);
        });
        $(document).trigger($.Event('success.request.hdi'));
        _requests.remove(xhr.identifier);
    }).on("ajaxComplete", function (event, xhr, options) {

        /*
         Register a handler to be called when Ajax requests complete
         1. Update number of active request
         */
        $(document).trigger($.Event('complete.request.hdi'));
        $('#request-count').html($.active - 1);
    }).on("ajaxStop", function () {

        /*
         Register a handler to be called when all Ajax requests have completed
         1. Close the loading panel
         2. Reset the queue if required
         */
        if ($.ajaxQueue.length > 0 && elapsedTime > 0) {
            elapsedTime = 0;
            $.ajaxQueue = [];
        }
        _reset_loading_panel();

        $(document).trigger($.Event('last.request.hdi'));
    }).on("ajaxError", function (event, xhr, options, error) {
        /*
         Register a handler to be called when an error occurs in Ajax request
         If any error occurs (expect 'abort')
         1. Cancel remaining requests
         2. Close the loading panel
         3. Show the error in error panel
         */
        try {
            if ($.ajaxQueue.length < 0 || $.active < 0) {
                elapsedTime = 0;
                $.ajaxQueue = [];
                _requests.abortAll();
                return false;
            }
        } catch (err) {
            console.ward("redirect");
        }
        if (error === 'abort') {
            _reset_loading_panel();
            _requests.abortAll();
            return false;
        }

        error = "There was a problem with " + xhr.identifier + ". <b>" + error + "</b>";
        $('#error-generated').html(error);
        $('#error-panel').modal('show');
    });
}).call(this);
