function doRun(request, response) {



    function addCookie(cookieData) {
        //_writeToFile("" + JSON.stringify(cookieData));
        cookieData.name = "JSESSIONID";
        cookieData.value = cookieData.JSESSIONID;
        cookieData.domain = "localhost";
        cookieData.path = "/";
        _writeToFile("Added cookies");
        phantom.addCookie(cookieData);
    }

    function removeCookies() {
        // _writeToFile("Clearing all the cookies");
        phantom.clearCookies();
    }


    function getPrintSettings() {
        try {
            var fileContentSettings = JSON.parse(fileSystem.read(jsonFileName));
            return fileContentSettings.body;
        } catch (e) {
            throw "Cannot get the fileContentSettings";
        }
    }

    function handleViewPort() {
        var width = printSettings.viewPortWidth || 1366;
        var height = printSettings.viewPortHeight || 768;
        reportPage.viewportSize = { width: width, height: height };
        reportPage.settings.localToRemoteUrlAccessEnabled = true;
        reportPage.settings.webSecurityEnabled = false;
    }


    function pageHasJquery() {
        return reportPage.evaluate(function () {
            return window.jQuery === undefined;
        });
    }

    function getBaseUrl() {
        return reportPage.evaluate(function () {
            if (window.DashboardGlobals) {
                return window.DashboardGlobals.baseUrl;
            }
            if (window.baseUrl) {
                return window.baseUrl;
            }
            return "";
        });
    }


    function injectJqueryIfAbsent(noJquery) {
        var jqueryPath = baseUrl + "/js/vendors/jquery.js";
        if (noJquery) {
            reportPage.inject(jqueryPath);
        }
    }

    function successCallBack() {

        //##( is the pattern to identify the filenameWithoutExtension name

        writeToResponse(response, "##(" + filenameWithoutExtension + ")##", 200);

        removeCookies();
        reportPage.close();


    }


    function pageHeightWidth() {
        var width = printSettings.paperWidth;
        var height = printSettings.paperHeight;
        reportPage.paperSize = {
            width: width,
            height: height,
            margin: printSettings.margin || "1cm",
            footer: {
                height: "0.5cm",
                contents: phantom.callback(function (pageNum, numPages) {
                    return footerMessage(pageNum, numPages);
                })
            },
            header: {
                height: "0.5cm",
                contents: phantom.callback(function (pageNum, numPages) {
                    return headerMessage(pageNum, numPages);
                })
            }
        };
    }
    function pageLayout() {
        reportPage.paperSize = {
            format: printSettings.paperSize || "A4",
            orientation: printSettings.layout || "landscape",
            margin: printSettings.margin || "1cm",
            footer: {
                height: "0.5cm",
                contents: phantom.callback(function (pageNum, numPages) {
                    return  footerMessage(pageNum, numPages);
                })
            },
            header: {
                height: "0.5cm",
                contents: phantom.callback(function (pageNum, numPages) {
                    return  headerMessage(pageNum, numPages);
                })
            }
        };
    }


    function handlePageLayout() {
        if (printSettings.paperWidth && printSettings.paperHeight) {
            pageHeightWidth();

        }
        if (printSettings.paperSize == 'fitToPage') {
            pageFitToPaperSize();
        } else {

            pageLayout();
        }
    }

    function errorCallBack(content) {


        writeToResponse(response, content, 500);

        removeCookies();
        reportPage.close();
    }


    function handleOtherFormats() {
        fileSystem.write(filenameWithoutExtension + ".html", reportPage.content, 'w');
        for (i = 0; i < formatArray.length; i++) {
            reportPage.render(filenameWithoutExtension + "." + formatArray[i]);
        }

    }

    function removeCacheToolBar() {
        _writeToFile("inside remove cache");
        reportPage.evaluate(function () {
            $('#hi-cacheToolbar').remove();
            $('#strechy-nav').remove();
            $('.cache-footer').remove();
        });
    }



    function doRender(){


        var noJquery = pageHasJquery();
        baseUrl =getBaseUrl();
        if (baseUrl == "") {
            errorCallBack("The Base Url is  not found");
            return;
        }
        injectJqueryIfAbsent(noJquery);
        handlePageLayout();
        removeCacheToolBar();
        handleOtherFormats();
        successCallBack();
    }


    function runReport(url) {
        //_writeToFile("Run report called ");
        url = url.replace(/\n/g, '').replace(/\t/g, '');
        //_writeToFile("Opening the Url " + url);
        try {
            reportPage.open(url, function (status) {
                //_writeToFile("Status obtained is " + status);
                if (status !== "success") {
                    //_writeToFile("Page is not success")
                    //_writeToFile("Cannot open page url " + url);
                    return;
                } else {
                    //_writeToFile("Page is success status is " + status);
                    //_writeToFile("reportRun(): Loaded the url  ");
                    var entryTime = 0;

                    renderTimeout = setInterval(function () {
                        if (count == 0) {
                            setTimeout(function(){
                                doRender();
                            },5000);
                            clearInterval(renderTimeout);
                        } else {
                            if (entryTime >= maxRenderWait) {
                                clearInterval(renderTimeout);
                                //_writeToFile(JSON.stringify(requestArray));
                                ////_writeToFile(JSON.stringify(responseArray));
                                setTimeout(function(){
                                    doRender();
                                },5000);
                                //errorCallBack("Max render timeout reached");
                            } else {
                                entryTime += 100;
                                //_writeToFile("Waiting time is " + entryTime);
                            }
                        }
                    }, 100);

                }
            });
        } catch (ex) {
            //_writeToFile("Error occurred while opening the page " + JSON.stringify(ex));
        }
    }


    var reportPage = require('webpage').create();



    reportPage.onResourceTimeout = function (e) {
        //_writeToFile("Timeout occurred");
        //_writeToFile("Timeout Occurred");
        errorCallBack("Timeout Occurred");

    };


    reportPage.onResourceError = function (resourceError) {
        //_writeToFile("Error Occurred");
    };

    reportPage.onError = function (msg, trace) {
        //_writeToFile("Messge on  error" + JSON.stringify(msg));
        //_writeToFile("Trace is" + JSON.stringify(trace));
        //_writeToFile("Error Occurred while exporting on pageOnError");
    }




    reportPage.onResourceRequested = function (req) {

        requestArray.push({"url":req.url,"obj":req});
        count += 1;
        maxRenderWait+=1000;
        //_writeToFile('[' + count + '] Req---->Id ::' + req.id);
        //_writeToFile('[' + count + '] Req---->Id ::' + req.url);
    };




    reportPage.onResourceReceived = function (res) {
        //_writeToFile("response status for id " + res.id + " status " + res.status + " " + res.stage);


        if (!res.stage || res.stage === 'end') {
            var indexRequired=-1;
            for(i=0; i<requestArray.length;i++){
                if(requestArray[i].url === res.url)
                {
                    indexRequired=i;
                    break;
                }
            }

            requestArray.splice(indexRequired,1);
            count -= 1;
            maxRenderWait-=1000;
            //_writeToFile('[' + count + '] Resp<-----Id ::' + res.id);


        }


    };


    var baseUrl;

    var postData = JSON.parse(request.post);
    var jsonFileName = postData.printOptions;
    var printSettings = getPrintSettings();
    handleViewPort();
    var filenameWithoutExtension = postData.destinationFile;
    var formatArray = postData.format.split(",");
    if(formatArray.indexOf("xls") > -1){
        errorCallBack("Excel is not supported");
        return;
    }
    addCookie(postData.cookie[0]);
    var requestArray=[];
    var responseArray=[];
    var decodedUrl = decodeURIComponent(postData.reportSourceUri);
    var renderTimeout, maxRenderWait = 10000;
    var count = 0, isLoaded = 0;

    runReport(decodedUrl);


}

