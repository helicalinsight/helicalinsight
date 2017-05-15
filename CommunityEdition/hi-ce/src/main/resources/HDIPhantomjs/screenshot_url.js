var system = require('system');

var header = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><meta http-equiv=Content-Type content="text/html; charset=UTF-8"><!--[if gte mso 9]><xml> <x:ExcelWorkbook>  <x:ExcelWorksheets>   <x:ExcelWorksheet>    <x:Name>###sheetname###</x:Name>    <x:WorksheetOptions>     <x:ProtectContents>False</x:ProtectContents>     <x:ProtectObjects>False</x:ProtectObjects>     <x:ProtectScenarios>False</x:ProtectScenarios>    </x:WorksheetOptions>   </x:ExcelWorksheet>  </x:ExcelWorksheets>  <x:ProtectStructure>False</x:ProtectStructure>  <x:ProtectWindows>False</x:ProtectWindows> </x:ExcelWorkbook></xml><![endif]-->';

// File system object of the server
var fs = require('fs');

// Web Address (URL) of the page to capture
var url = system.args[1].replace(/#/gi, "&");

// File name of the captured image
var file = system.args[2];

var fileExtensionUpperCase = file.substr(file.lastIndexOf('.') + 1, file.length).toUpperCase();

var isExcelDownload = false;

if (fileExtensionUpperCase == 'XLS' || fileExtensionUpperCase == 'XLSX') {
    isExcelDownload = true;
    url = url + "&downloadType=xls";
}

var domain = system.args[3];

var reportPage = require('webpage').create();

// Browser size - height and width in pixels
// Change the viewport to 480x320 to emulate the iPhone
reportPage.viewportSize = { width: 670, height: 1024 };

var resourceWait = 300,
    maxRenderWait = 10000,
    count = 0,
    forcedRenderTimeout,
    renderTimeout;

function doRender() {
    var logoutPage = require('webpage').create();

    var pageDimension = reportPage.evaluate(function () {

        var dimension = {};

        var element = document.getElementById('hi-cacheToolbar');
        if (element) {
            element.parentNode.removeChild(element);
        }
        var adhocMenu = document.getElementsByClassName('strechy-nav');
        if (adhocMenu[0]) {
            adhocMenu[0].parentNode.removeChild(adhocMenu[0]);
        }

        var mainElement = document.getElementById("main");
        if (mainElement) {
            dimension['width'] = mainElement.scrollWidth;
            dimension['height'] = mainElement.scrollHeight;
            return dimension;
        }

        return null;
    });


    console.log("The width of main is ", pageDimension.width, " height ", pageDimension.height);


    reportPage.paperSize = {
        format: 'A4',
        orientation: 'portrait',
        margin: '1cm',
        footer: {
            height: "0.5cm",
            contents: phantom.callback(function (pageNum, numPages) {
                return "<div style='text-shadow:1px 2px 1px rgba(198,184,255,1);font-weight:bold;color:#498FDE;font-size:8px'><span style='text-align:left;padding:1px'>Printed using Helical Insight</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
            })
        }
    };


    if (isExcelDownload) {
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
        var SCRIPT_REGEX = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;
        var SELECT_REGEX = /<select\b[^<]*(?:(?!<\/select>)<[^<]*)*<\/select>/gi;
        var pageContent = reportPage.content.replace(SCRIPT_REGEX, "").replace(SELECT_REGEX, "");

        fs.write(file, (header + pageContent.substring(pageContent.indexOf('<head>') + 6)), 'w');
    } else {
        fs.write(file.substr(0, file.lastIndexOf('.')) + ".html", reportPage.content, 'w');
        reportPage.render(file);
    }

    logoutPage.open(domain.substr(0, domain.lastIndexOf('/')) + '/j_spring_security_logout');
    console.log("The web page " + url + " screenshot is taken successfully. Phantom is exiting.");
    phantom.exit();
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

reportPage.settings.localToRemoteUrlAccessEnabled = true;
reportPage.settings.webSecurityEnabled = false;

function saveReport() {
    reportPage.open(url, function (status) {
        if (status !== "success") {
            console.log("Fatal Error. Could not open web page : " + url);
            phantom.exit();
        } else {
            forcedRenderTimeout = setTimeout(function () {
                console.log(count);
                doRender();
            }, maxRenderWait);
        }
    });
}

if (domain) {
    var loginPage = require('webpage').create();

    function getLoginUrl() {
        var loginUrl,
            username = system.args[4],
            passCode = system.args[5],
            organization = system.args[6];

        if (organization) {
            loginUrl = domain + "?j_organization=" + organization + "&j_username=" + username + "&j_password=" + passCode;
        } else {
            loginUrl = domain + "?j_username=" + username + "&j_password=" + passCode;
        }
        return loginUrl;
    }

    loginPage.open(getLoginUrl(), function (loginStatus) {
        if (loginStatus !== "success") {
            console.log("Fatal Error. Couldn't login to get the report screen-shot.");
        } else {
            saveReport();
        }
    });
} else {
    // Render the screen-shot image
    saveReport();
}

phantom.onError = function (msg, trace) {
    var msgStack = ['PHANTOM ERROR: ' + msg];
    if (trace && trace.length) {
        msgStack.push('TRACE:');
        trace.forEach(function (element) {
            msgStack.push(' -> ' + (element.file || element.sourceURL) + ': ' + element.line + (element.function ? ' (in function ' + element.function + ')' : ''));
        });
    }
    console.error(msgStack.join('\n'));
    phantom.exit(1);
};
