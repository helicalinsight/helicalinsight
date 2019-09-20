<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<% response.setContentType("application/javascript;charset=UTF-8");%>
<sec:authentication property="authorities" var="authorities"/>
<c:set var="organizationAdmin" value=""/>

var baseUrl = "${baseURL}/";
var admin = '${organizationAdmin}';
<c:set var="profileArray" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.profiles}" />
window.loggedInUser = [];

<c:forEach var="userProfile" items="${profileArray}" varStatus="loop">
    window.loggedInUser.push({
    profileName: '<c:out value="${userProfile.profile_name}"/>',
    profileValue: '<c:out value="${userProfile.profile_value}"/>'
    });
</c:forEach>
var tempRole=[];
    <c:forEach var="role" items="${authorities}">
        <c:if test = "${not fn:contains(role, 'Switch')}">
            tempRole.push('<c:out value="${role}"/>');
        </c:if>

    </c:forEach>


if (!window.DashboardGlobals) {
window.DashboardGlobals = (function () {


window.HDI = window.HDI || {};
window.HDI.adhoc = window.HDI.adhoc || {};
window.HDI.adhoc.urls = {
services: baseUrl + "/services",
createDataSource: baseUrl + "/createDataSource",
listDataSources: baseUrl + "/listDataSources",
listLocations: baseUrl + "/listLocations",
getResources: baseUrl + "/getResources"
};

var object = {
// __addBase__ : true,
baseUrl: baseUrl,
solutionLoader: baseUrl + "getSolutionResources.html",
resourceLoader: baseUrl + "getEFWSolution.html",
updateService: baseUrl + "executeDatasource.html",
chartingService: baseUrl + "visualizeData.html",
ceReportCreate: baseUrl + "ce-report-create.html",
ceReportEdit: baseUrl + "ce-report-edit.html",
exportData: baseUrl + "exportData.html",
reportDownload: baseUrl + "downloadReport.html",
productInfo: baseUrl + "getProductInformation.html",
sendMail: baseUrl + "sendMail.html",
updateEFWTemplate: baseUrl + "updateEFWTemplate.html",
sessionUserName: "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}",
sessionUserEmail: "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.emailAddress}",
sessionUserOrganization: "",
rootDirectoryPermission: "${applicationSettings.rootDirectoryPermission}",
provideHTMLExport: "${applicationSettings.provideExportViaHtml}",
enableReportSave: "${applicationSettings.enableReportSave}",
defaultEmailResourceType: "${applicationSettings.defaultEmailResourceType}",
controllers: {
efw: baseUrl + "getEFWSolution.html",
efwsr: baseUrl + "executeSavedReport.html",
efwfav: baseUrl + "executeFavourite.html",
report: baseUrl + "hi.html"

},
saveReport: baseUrl + "saveReport.html",
fsop: baseUrl + "fileSystemOperations.html",
importFile: baseUrl + "importFile.html",
downloadEnableSavedReport: baseUrl + "downloadEnableSavedReport.html",

"scheduling": {
"get": baseUrl + "getScheduleData.html",
"update": baseUrl + "updateScheduleData.html"
},
"adminPaths" : {
users: baseUrl+"admin/users",
profiles: baseUrl+"admin/profiles",
roles: baseUrl+"admin/roles",
services: baseUrl+"services.html"
},
services: baseUrl + "services",
openEfw: baseUrl + "hi.html",
optionalReportParams: {
location: undefined,
reportname: undefined
},
recursiveDirectoryLoad: false,
user: {
roles: tempRole,
profile: window.loggedInUser,
userName: "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}",
email: "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.emailAddress}",
organization: "",
actualUserName:'${actualUserName}'
}
};

return object;
})();
}

var count=0;
$("#dashboard-canvas").on("load",function(e){
if(count ==0){
$(".body-block").addClass("report-view");
count++;
}
});


if($("#reportsStats").length){

function openRecentReport(sampleReport) {
var sampleElement = {}
sampleElement.data = function(str) {
switch (str) {
case "path":
return sampleReport.reportPath;
case "name":
return sampleReport.file;
case "extension":
return "efw";
case "title":
return sampleReport.title;
case "type":
return "file";
}
}
HDIUI.filebrowser.efwLoader(sampleElement);
HDIUI.filebrowser.fetchData();

if(!window.DashboardGlobals.currentReport){
window.DashboardGlobals.currentReport= {}
}
window.DashboardGlobals.currentReport.path=sampleReport.reportPath;
window.DashboardGlobals.currentReport.__path=sampleReport.logicalPath+"/"+sampleReport.title;
window.DashboardGlobals.currentReport.title=sampleReport.title;

}

<auth:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
$.post( window.HDI.adhoc.urls.services+"?type=monitor&serviceType=system&service=reportStats", function( data ) {
$( "#reportsStats" ).text( data.response.reportsCount);
jsArray = data.response.latestReports;
$.each(jsArray, function ( index,reportElement){
var temp= reportElement;

$("#sampleReports").append('<li class="col-md-3 col-sm-4 col-xs-12"> <a href="#"  onclick="openRecentReport(jsArray['+index+'])"> <img src="${baseURL}/images/alpha/sample-report-1.png" class="img-responsive"/> <p class="reportTitle">'+reportElement.title+'</p> </a> </li>');
});


}).fail(function() {
$( "#reportsStats" ).text('NA');
$("#sampleReports").append('<h5>There is no report available</h5>');
});
$(".reportTitle").on("click",function(e){
$("#report-title").text($(this).val);
})
$.post( window.HDI.adhoc.urls.services+"?type=monitor&serviceType=system&service=datasourceCount", function( data ) {
$( "#dsStats" ).text( data.response.dataSourceCount);
}).fail(function() {
$( "#dsStats" ).text('NA');

});
</auth:authorize>
}

