function inJectScript() {
    if (printSettings.hasScript) {
        var injectScript = jsonFileName.substr(0, jsonFileName.lastIndexOf(".")) + ".js";
        phantom.injectJs(injectScript);
    }
}


function handleViewPort() {
    reportPage.viewportSize = { width: printSettings.viewPortWidth, height: printSettings.viewPortHeight };
    reportPage.settings.localToRemoteUrlAccessEnabled = true;
    reportPage.settings.webSecurityEnabled = false;
}

function footerMessage(pageNum, numPages) {
    return "<div style='text-shadow:1px 2px 1px rgba(198,184,255,1);font-weight:bold;color:#498FDE;font-size:8px'><span style='text-align:left;padding:1px'>Printed using Helical Insight</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
}

function pageHeightWidth() {
    reportPage.paperSize = {
        width: printSettings.paperWidth,
        height: printSettings.paperHeight,
        margin: "1cm",
        footer: {
            height: "0.5cm",
            contents: phantom.callback(function (pageNum, numPages) {
                return footerMessage(pageNum, numPages);
            })
        }
    };
}

function pageLayout() {
    reportPage.paperSize = {
        format: printSettings.paperSize,
        orientation: printSettings.layout,
        margin: "1cm",
        footer: {
            height: "0.5cm",
            contents: phantom.callback(function (pageNum, numPages) {
                return  footerMessage(pageNum, numPages);
            })
        }
    };
}

function handlePageLayout() {
    if (printSettings.paperWidth && printSettings.paperHeight) {
        pageHeightWidth();

    } else {

        pageLayout();
    }
}

function handleZoomFactor() {
    if (printSettings.scaling) {
        reportPage.zoomFactor = printSettings.scaling;
    }
}

function handleScroll() {
    if (printSettings.scrollPosition) {
        reportPage.scrollPosition = {
            top: printSettings.scrollPosition.top,
            left: printSettings.scrollPosition.left
        };
    }
}

function getPrintSettings() {

    var fileContent = JSON.parse(fileSystem.read(jsonFileName));
    printSettings = fileContent.body;
    console.log("print settings", printSettings);
    return printSettings;
}


function isExcel() {

    return arrayContains("xls", formatArray) || arrayContains("xlsx", formatArray);

}


function evalFrameCount() {
    return reportPage.evaluate(function () {
        var gridStackWrappers = $(".grid-stack-wrapper");
        return gridStackWrappers.length;
    });
}
function createExcelWithServiceApi(totalFrames) {
    var excelRequest = require('webpage').create();
    var jsForm = {};
    var actual = [];
    for (var i = 0; i < totalFrames; i++) {
        var boundClip = reportPage.evaluate(function (d) {
            return document.getElementsByClassName("grid-stack-wrapper")[d].getBoundingClientRect();
        }, i);

        reportPage.clipRect = {
            top: boundClip.top,
            left: boundClip.left,
            width: boundClip.width,
            height: boundClip.height
        };

        var newFileName = file.substr(0, file.lastIndexOf('.')) + "_" + i + '.png';
        boundClip.index = newFileName;
        actual.push(boundClip);
        reportPage.render(newFileName);
    }
    jsForm.report = actual;
    var jsonFile = file.substr(0, file.lastIndexOf('.')) + '.json';
    jsForm.destinationFile = file;
    fileSystem.write(jsonFile, JSON.stringify(jsForm), 'w');

    var downloadToExcel = baseUrl + 'services?type=print&serviceType=export&service=xls&formData={"targetJson":"' + (jsonFile.replace(/\\/g, "\\\\")) + '"}';
    console.log("Calling the link", downloadToExcel);
    excelRequest.open(downloadToExcel, function (status) {
        if (status !== "success") {
            console.log("Fatal Error. Could not open web page : " + url);
            createExcelWithContent();
            return;
        } else {
            while (!fileSystem.path(file)) {

                console.log("Waiting for excel");
            }
        }
    });
}
function handleExcel() {
    var totalFrames = evalFrameCount();

    console.log("totalFrames ", totalFrames);

    if (!totalFrames) {
        createExcelWithContent();
    } else {

        createExcelWithServiceApi(totalFrames);
    }
}

function createExcelWithContent() {
    var header = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><meta http-equiv=Content-Type content="text/html; charset=UTF-8"><!--[if gte mso 9]><xml> <x:ExcelWorkbook>  <x:ExcelWorksheets>   <x:ExcelWorksheet>    <x:Name>###sheetname###</x:Name>    <x:WorksheetOptions>     <x:ProtectContents>False</x:ProtectContents>     <x:ProtectObjects>False</x:ProtectObjects>     <x:ProtectScenarios>False</x:ProtectScenarios>    </x:WorksheetOptions>   </x:ExcelWorksheet>  </x:ExcelWorksheets>  <x:ProtectStructure>False</x:ProtectStructure>  <x:ProtectWindows>False</x:ProtectWindows> </x:ExcelWorkbook></xml><![endif]-->';
    var chartElement = reportPage.evaluate(function () {
        return document.getElementById('dashboard-canvas');
    });
    if (chartElement) {
        reportPage.evaluate(function () {
            var chartElem = document.getElementById('dashboard-canvas');
            document.body.innerHTML = "";
            document.body.appendChild(chartElem);
        });
    }

    /*remove the error panel and updating panel */

    reportPage.evaluate(function () {
        var element = document.getElementById('error-panel');
        if (element) {
            element.parentNode.removeChild(element);
        }
        element = document.getElementById('hdi-blockUI');
        if (element) {
            element.parentNode.removeChild(element);
        }


    });


    /*remove the error panel and updating panel*/
    var SELECT_REGEX = /<select\b[^<]*(?:(?!<\/select>)<[^<]*)*<\/select>/gi;
    var SCRIPT_REGEX = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;
    var pageContent = reportPage.content.replace(SCRIPT_REGEX, "").replace(SELECT_REGEX, "");

    fileSystem.write(file + ".xls", (header + pageContent.substring(pageContent.indexOf('<head>') + 6)), 'w');
}


function doLogout() {
    var logoutPage = require('webpage').create();
    logoutPage.open(domain.substr(0, domain.lastIndexOf('/')) + '/j_spring_security_logout');
    logoutPage.close();
}
function printStats() {
    console.log("The web page " + url + " screenshot is taken successfully. Phantom is exiting.");
    console.log("Time taken to print " + (Date.now() - timeStart) + "ms ");
    console.log(JSON.stringify(printSettings));
}

function arrayContains(item, arrayElement) {
    return (arrayElement.indexOf(item) > -1);
}

function getLoginUrl() {
    var loginUrl,
        username = system.args[4],
        passCode = system.args[5];

    loginUrl = domain + "?j_username=" + username + "&j_password=" + passCode;
    console.log("Login url is ", loginUrl);
    return loginUrl;
}


function removeCacheToolBar() {
    reportPage.evaluate(function () {
        $('#hi-cacheToolbar').remove();
        $('#strechy-nav').remove();
    });
}
function handleOtherFormats() {
    fileSystem.write(file + ".html", reportPage.content, 'w');
    for (i = 0; i < formatArray.length; i++) {
        reportPage.render(file + "." + formatArray[i]);
    }
    reportPage.close();
}
function pageHasJquery() {
    return reportPage.evaluate(function () {
        return window.jQuery === undefined;
    });
}
function getBaseUrl() {
    return reportPage.evaluate(function () {
        return window.baseUrl;
    });
}
function injectJqueryIfAbsent(noJquery) {
    var jqueryPath = baseUrl + "/js/vendors/jquery.js";
    console.log("The jquery url  " + !noJquery);
    if (noJquery) {
        reportPage.inject(jqueryPath);
    }
}
function doRender() {


    var noJquery = pageHasJquery();

    baseUrl = getBaseUrl();

    injectJqueryIfAbsent(noJquery);

    removeCacheToolBar();

    doExecute();

    handlePageLayout();

    handleZoomFactor();

    handleScroll();


    if (isExcel()) {
        handleExcel();
    } else {
        handleOtherFormats();
    }
    doLogout();
    printStats();
    phantom.exit();


}

function saveReport() {
    reportPage.open(url, "post", function (status) {
        if (status !== "success") {
            console.log("Fatal Error. Could not open web page : " + url);
            phantom.exit(1);
        } else {
            forcedRenderTimeout = setTimeout(function () {
                console.log("do ren called");
                doRender();
            }, maxRenderWait);
        }
    });
}


phantom.onError = function (msg, trace) {
    var msgStack = ['PHANTOM ERROR: ' + msg + trace];
    if (trace && trace.length) {
        msgStack.push('TRACE:');
        trace.forEach(function (element) {
            msgStack.push(' -> ' + (element.file || element.sourceURL) + ': ' + element.line + (element.function ? ' (in function ' + element.function + ')' : ''));
        });
    }
    console.error(msgStack.join('\n'));
    phantom.exit(1);
};


var system = require('system');
var fileSystem = require('fs');
var reportPage = require('webpage').create();
var PID = system.pid;
var timeStart = Date.now();

console.log("phantomPID:", PID);

var url = system.args[1];

var file = system.args[2];
var domain = system.args[3];


var jsonFileName = system.args[6];
var reportDirectory = system.args[7];
var formatArray = system.args[8].split(",");
console.log("Format Array", system.args[8]);

var resourceWait = 300, maxRenderWait = 15000, count = 0, forcedRenderTimeout, renderTimeout;

var baseUrl = "";

var printSettings = getPrintSettings();

inJectScript();

handleViewPort();


if (domain) {
    var loginPage = require('webpage').create();
    loginPage.open(getLoginUrl(), function (loginStatus) {
        if (loginStatus !== "success") {
            console.log("Fatal Error. Couldn't login to get the report screen-shot.");
            phantom.exit(1);
        } else {
            saveReport();
        }
    });
} else {
    // Render the screen-shot image
    saveReport();
}


reportPage.onResourceRequested = function (request) {
    count += 1;
    clearTimeout(renderTimeout);
};

reportPage.onResourceReceived = function (response) {
    if (!response.stage || response.stage === 'end') {
        count -= 1;
        if (count === 0) {
            renderTimeout = setTimeout(doRender, (resourceWait + 5000)); //5 second extra time to render animated charts
        }
    }
};
