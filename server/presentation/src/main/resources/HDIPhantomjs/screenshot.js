function inJectScript() {
    if (printSettings.hasScript) {
        //console.log("setting has scripts");
        var injectScript = jsonFileName.substr(0, jsonFileName.lastIndexOf(".")) + ".js";
        phantom.injectJs(injectScript);
    }
}


function handleViewPort() {
    var width = printSettings.viewPortWidth||1366;
    var height = printSettings.viewPortHeight||768;
    if(system.os.name == 'mac' && printSettings.paperSize != 'fitToPage'){
        width = printSettings.viewPortWidth||800;
        height = printSettings.viewPortHeight||550;
    }
    reportPage.viewportSize = { width: width, height: height };
    //console.log("View port is set  to ",reportPage.viewportSize.width);
    //console.log("View port is set  to ",reportPage.viewportSize.height);
    reportPage.settings.localToRemoteUrlAccessEnabled = true;
    reportPage.settings.webSecurityEnabled = false;
}

function footerMessage(pageNum, numPages) {
    if(printSettings.footer){
        var fontColor = printSettings.footer.fontColor||"black";
        var fontStyle =printSettings.footer.fontStyle||"bold";
        var fontSize = printSettings.footer.fontSize||"12px";
        var textAlign = printSettings.footer.textAlign||"center";
        var text = printSettings.footer.text||"";
        if(printSettings.footer.useHtml){
            return text.replace(/pageNum/g,""+pageNum).replace(/numPages/g,""+numPages);
        }else {
            return "<div style='color:" + fontColor + ";font-weight:" + fontStyle + ";font-size:" + fontSize + "'><span style='text-align:" + textAlign + ";padding:1px'>" + text + "</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
        }
    }
    return "<div style='text-shadow:1px 2px 1px rgba(198,184,255,1);font-weight:bold;color:#498FDE;font-size:8px'><span style='text-align:left;padding:1px'>Printed using Helical Insight</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
}



function headerMessage(pageNum, numPages) {
    if(printSettings.header){
        var fontColor = printSettings.header.fontColor||"black";
        var fontStyle =printSettings.header.fontStyle||"bold";
        var fontSize = printSettings.header.fontSize||"12px";
        var textAlign = printSettings.header.textAlign||"center";
        var text = printSettings.header.text||"";
        if(printSettings.header.useHtml){
            return text.replace(/pageNum/g,""+pageNum).replace(/numPages/g,""+numPages);
        }
        return "<div style='color:"+ fontColor+";font-weight:"+ fontStyle+";font-size:"+ fontSize+"'><span style='text-align:"+ textAlign+";padding:1px'>"+ text+"</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
    }
    return "<div style='text-shadow:1px 2px 1px rgba(198,184,255,1);font-weight:bold;color:#498FDE;font-size:8px'><span style='text-align:left;padding:1px'>Printed using Helical Insight</span><span style='float:right'>Page " + pageNum + " of " + numPages + "</span></div>";
}

function getInCm(str,defaultValue){
    if(str){
        if(typeof(str)==="string" && str.indexOf("cm")==-1) {

            return str + "cm";
        }
    }
    return defaultValue+"cm";



}


function pageHeightWidth() {

    var width = printSettings.paperWidth||1366;
    var height = printSettings.paperHeight||786;
    reportPage.paperSize = {
        width: width,
        height: height,
        margin: getInCm(printSettings.margin,1),
        footer: {
            height: getInCm(printSettings.footer?printSettings.footer.height:"0.5cm",0.5) ,
            contents: phantom.callback(function (pageNum, numPages) {
                return footerMessage(pageNum, numPages);
            })
        },
        header: {
            height: getInCm(printSettings.header?printSettings.header.height:"0.5cm",0.5),
            contents: phantom.callback(function(pageNum, numPages) {
                return headerMessage(pageNum, numPages);
            })
        }
    };
}

function pageLayout() {

    reportPage.paperSize = {
        format: printSettings.paperSize || "A4",
        orientation: printSettings.layout || "landscape",
        margin: getInCm(printSettings.margin,1),
        footer: {
            height: getInCm(printSettings.footer?printSettings.footer.height:"0.5cm",0.5),
            contents: phantom.callback(function (pageNum, numPages) {
                return  footerMessage(pageNum, numPages);
            })
        },
        header: {
            height: getInCm(printSettings.header?printSettings.header.height:"0.5cm",0.5) ,
            contents: phantom.callback(function (pageNum, numPages) {
                return  headerMessage(pageNum, numPages);
            })
        }
    };
}

function pageFitToPaperSize() {
    var size = reportPage.evaluate(function () {
        var body = document.body,
            html = document.documentElement;
        var dht = Math.max( body.scrollHeight, body.offsetHeight,
            html.clientHeight, html.scrollHeight, html.offsetHeight);

        var dwd = Math.max( body.scrollWidth, body.offsetWidth,
            html.clientWidth, html.scrollWidth, html.offsetWidth );

        return {
            //	format: "A4",
            //width: (dwd*2) +"px",
            //height : (dht*2) + "px",
            width:dwd,
            height:dht
        };
    });
    var dpi = 72;
    if(system.os.name == 'windows'){
        dpi = 120;
    }
    if(system.os.name == 'linux'){
        dpi = 96;
    }
    if(system.os.name == 'mac'){
        dpi = 72;
    }
    var width = size.width/dpi;
    var height = size.height/dpi;
    reportPage.paperSize = {
        width:width+"in",
        height:(height+1)+"in",
        orientation: "portrait",
        margin: "1cm",
        footer: {
            height: "0.5cm",
            contents: phantom.callback(function (pageNum, numPages) {
                return footerMessage(pageNum, numPages);
            })
        },
        header: {
            height: "0.5cm",
            contents: phantom.callback(function(pageNum, numPages) {
                return headerMessage(pageNum, numPages);
            })
        }
    };
}

function handlePageLayout() {
    if (printSettings.paperWidth && printSettings.paperHeight) {
        pageHeightWidth();

    }else if(printSettings.paperSize == 'fitToPage'){
        pageFitToPaperSize();
    }else {
        pageLayout();
    }
}

function handleZoomFactor() {
    if (printSettings.scaling) {
        reportPage.zoomFactor = printSettings.scaling;
    }else{
        reportPage.zoomFactor = "1";
    }
}

function handleScroll() {
    if (printSettings.scrollPosition) {
        reportPage.scrollPosition = {
            top: printSettings.scrollPosition.top || "0px",
            left: printSettings.scrollPosition.left || "0px"
        };
    }
}

function getPrintSettings() {

    var fileContent = JSON.parse(fileSystem.read(jsonFileName));
    printSettings = fileContent.body;
    //console.log("print settings", JSON.stringify(printSettings));
    return printSettings;
}


function isExcel() {

    return arrayContains("xls", formatArray) || arrayContains("xlsx", formatArray);

}


function evalFrameCount() {
    return reportPage.evaluate(function () {
        /*
         changing the logic to support all type to reports and dashboard
         if(window.urlParameters){
         */
        var gridStackWrappers;
        if($(".grid-stack-wrapper").length > 0){
            gridStackWrappers = $(".grid-stack-wrapper");
        }
        else if($(".report-container").length > 0){
            gridStackWrappers = $(".report-container");
        }
        else{
            gridStackWrappers = $("#main");
        }
        console.log("[Print] evalFrameCount() - total frames calculated [",gridStackWrappers.length,"]");
        return gridStackWrappers.length;
    });
}

var getUrlParameter = function getUrlParameter(url,sParam) {
    var sPageURL = url,
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
function createExcelWithServiceApi(totalFrames) {
    console.log("[Print]createExcelWithServiceApi()");
    var url1 = decodeURIComponent(window.atob(system.args[1]));
    var authToken = getUrlParameter(url1,'authToken');

    var excelRequest = require('webpage').create();
    var jsForm = {};
    var actual = [];
    for (var i = 0; i < totalFrames; i++) {
        var boundClip = reportPage.evaluate(function (d) {
            /*if(document.getElementsByClassName("grid-stack-wrapper").length > 0){
             return document.getElementsByClassName("grid-stack-wrapper")[d].getBoundingClientRect();
             }

             else{
             return document.getElementById("main").getBoundingClientRect();
             }*/
            if(document.getElementsByClassName("grid-stack-wrapper").length > 0){
                return document.getElementsByClassName("grid-stack-wrapper")[d].getBoundingClientRect();
            } else if(document.getElementsByClassName("report-container").length > 0){
                return document.getElementsByClassName("report-container")[d].getBoundingClientRect();
            }
            else{
                return document.getElementById("main").getBoundingClientRect();
            }
        }, i);

        reportPage.clipRect = {
            top: boundClip.top,
            left: boundClip.left,
            width: boundClip.width,
            height: boundClip.height
        };

        var newFileName = file + "_" + i + '.png';
        boundClip.index = newFileName;
        actual.push(boundClip);
        reportPage.render(newFileName);
    }
    jsForm.report = actual;
    var jsonFile = file+'.json';
    jsForm.destinationFile = file+".xls";
    fileSystem.write(jsonFile, JSON.stringify(jsForm), 'w');
    if(authToken !== undefined){
        var downloadToExcel = baseUrl + '/services?' + 'authToken='+authToken+'&type=print&serviceType=export&service=xls&formData={"targetJson":"' + (jsonFile.replace(/\\/g, "\\\\")) + '"}'
    }else{
        var downloadToExcel = baseUrl + '/services?type=print&serviceType=export&service=xls&formData={"targetJson":"' + (jsonFile.replace(/\\/g, "\\\\")) + '"}'
    }
    console.log("[Print] createExcelWithServiceApi() - generating excel using url ",downloadToExcel);



    excelRequest.open(downloadToExcel, function (status) {
        function checkReadyState() {
            console.log("[Print] createExcelWithServiceApi() - waiting for excel ");
            setTimeout(function () {
                var readyState = excelRequest.evaluate(function () {
                    return document.readyState;
                });

                if ("complete" === readyState) {
                    console.log("Excel is ready.");
                    excelRequest.close();
                    phantom.exit(1);
                } else {
                    checkReadyState();
                }
            });
        }
        checkReadyState();
    });

}

function handleExcel() {
    var totalFrames = evalFrameCount();

    //console.log("totalFrames ", totalFrames);

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

    /*var abc = reportPage.evaluate(function(){
     var chart =  document.getElementById("chart");
     return chart;
     })*/
    reportPage.evaluate(function(){
        var chart2 =  document.getElementById("chart");
        document.body.innerHTML="";

        var test = document.body.innerHTML += "<div id='main'><div id='chart' style='display: none;'><table id='chart'>"+chart2.innerHTML+"<table></div></div>";

        var elements = document.getElementsByClassName('pvtUnused');
        var elements1 = document.getElementsByClassName('hi-pvtAxisContainer');

        while(elements.length > 0){
            elements[0].parentNode.removeChild(elements[0]);
        }

        while(elements1.length > 0){
            elements1[0].parentNode.removeChild(elements1[0]);
        }

    });



    /*remove the error panel and updating panel*/
    var SELECT_REGEX = /<select\b[^<]*(?:(?!<\/select>)<[^<]*)*<\/select>/gi;
    var SCRIPT_REGEX = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;
    var pageContent = reportPage.content.replace(SCRIPT_REGEX, "").replace(SELECT_REGEX, "");






    //console.log("---------------------------------------------------------------------------------")
    // console.log(reportPage.content);
//console.log("---------------------------------------------------------------------------------")
//console.log(pageContent);
    fileSystem.write(file + ".xls", (header + pageContent.substring(pageContent.indexOf('<head>') + 6)), 'w');
}


function doLogout() {
    var logoutPage = require('webpage').create();
    logoutPage.open(domain.substr(0, domain.lastIndexOf('/')) + '/j_spring_security_logout');
    logoutPage.close();
}
function printStats() {
    console.log("The web page " + url + " screen-shot is taken successfully. Export Manager is exiting.");
    console.log("Time taken to print " + (Date.now() - timeStart) + "ms ");
    console.log(JSON.stringify(printSettings));
}

function arrayContains(item, arrayElement) {
    return (arrayElement.indexOf(item) > -1);
}

function getLoginUrl() {
    var loginUrl,
        username = system.args[4],
        passCode = system.args[5-decrementor],
        organization = system.args[6-decrementor];
    if(decrementor==1){

        loginUrl=domain.substr(0, domain.lastIndexOf('/'))+"/mock/impersonate?username="+username+":"+organization+"&j_username=downloadManager&j_password=downloadManager";

    }else  if (organization && organization != "\"\"") {
        loginUrl = domain + "?j_organization=" + organization + "&j_username=" + username + "&j_password=" + passCode;
    } else {
        loginUrl = domain + "?j_username=" + username + "&j_password=" + passCode;
    }
    //console.log("Login url is ", loginUrl);
    return loginUrl;
}


function removeCacheToolBar() {
    reportPage.evaluate(function () {
        $('#hi-cacheToolbar').remove();
        $('#strechy-nav').remove();
        $('.cache-footer').remove();
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
        //return window..baseUrl;
        return window.DashboardGlobals.baseUrl;
    });
}
function injectJqueryIfAbsent(noJquery) {
    var jqueryPath = baseUrl + "/js/vendors/jquery.js";
    //console.log("The jquery url  " + !noJquery);
    if (noJquery) {
        reportPage.includeJs(jqueryPath);
    }
}
function doRender() {


    var noJquery = pageHasJquery();

    baseUrl = getBaseUrl();

    injectJqueryIfAbsent(noJquery);

    removeCacheToolBar();

    if(printSettings.hasScript)  doExecute();

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

    //##( is the pattern to identify the file name
    //Any kind of file naming operation has to be perforemed here
    console.log("##("+file+")##");
    if (!isExcel()) {
        phantom.exit();
    }


}

function saveReport() {
    reportPage.open(url, "post", function (status) {
        if (status !== "success") {
            console.log("Fatal Error. Could not open web page : " + url);
            phantom.exit(1);
        } else {
            forcedRenderTimeout = setTimeout(function () {
                //console.log("do ren called");
                doRender();
            }, maxRenderWait);
        }
    });
}


phantom.onError = function (msg, trace) {
    var msgStack = ['EXPORT ERROR: ' + msg + trace];
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
var decrementor = 10-system.args.length;

var fileSystem = require('fs');
var reportPage = require('webpage').create();
var PID = system.pid;
var timeStart = Date.now();

console.log("phantomPID:", PID);

var url = decodeURIComponent(window.atob(system.args[1]));

var file = system.args[2];

var domain = system.args[3];


var jsonFileName = system.args[7-decrementor];
var reportDirectory = system.args[8-decrementor];
var formatArray = system.args[9-decrementor].split(",");
//console.log("Format Array", system.args[9]);

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
    //console.log("do render cleared")
    clearTimeout(renderTimeout);
    //clearTimeout(forcedRenderTimeout);
};

reportPage.onResourceReceived = function (response) {
    if (!response.stage || response.stage === 'end') {
        count -= 1;
        if (count === 0) {
            //      console.log("do render called")
            renderTimeout = setTimeout(doRender, (resourceWait + 5000)); //5 second extra time to render animated charts
        }
    }
};
