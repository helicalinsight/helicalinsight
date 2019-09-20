<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<c:set var="organizationAdmin" value=""/>

<jsp:include page="/WEB-INF/jsp/fileInfo.jsp"/>
<jsp:include page="/WEB-INF/jsp/fileTitle.jsp"/>

<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL"
       value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"/>

<c:choose>
    <c:when test="${(empty decorator) or (param['mode'] eq 'open')}">
        <!DOCTYPE html>
        <html class="hi-window">
        <head>
            <jsp:include page="common-meta.jsp"/>
            <title>${data[0].title} | HI: User</title>
            <link rel="shortcut icon" type="image/x-icon" href="${baseURL}/images/favicon.ico">
            <link rel="stylesheet" href="${baseURL}/css/fonts.css"/>
            <link rel="stylesheet" href="${baseURL}/css/styles.css"/>
            <link rel="stylesheet" href="${baseURL}/css/custom.css"/>

            <script data-clone="true" src="${baseURL}/js/modernizr.js"></script>
            <script data-clone="true" src="${baseURL}/js/plugins/es5-shim.js"></script>

            <script src="${baseURL}/js/vendors/jquery.js"></script>
            <script src="${baseURL}/js/vendors/bootstrap.js"></script>

            <script type="text/javascript" src="${baseURL}/customscript"></script>
            <script data-clone="true" src="${baseURL}/js/plugins/Base64.js"></script>

            <script type="text/javascript">
                window.DashboardGlobals.efwddir = "${dir}";
                <c:if test="${!empty urlParameters}">
                var urlParameters = ${urlParameters};
                window.DashboardGlobals.folderpath = urlParameters.dir;
                window.DashboardGlobals.file = urlParameters.file;
                </c:if>


                <c:if test="${!empty reportParameters}">
                reportParameters = ${reportParameters};
                window.DashboardGlobals.reportParameters = ${reportParameters};
                </c:if>

                window.DashboardGlobals.fileTitle = "${data[0].title}";
                window.DashboardGlobals.lastModified = "${data[0].lastModified}";
                var admin = '${organizationAdmin}';
            </script>

            <script src="${baseURL}/js/vendors/backbone.js"></script>
            <script src="${baseURL}/js/vendors/moment.js"></script>


            <script src="${baseURL}/js/ajax.js"></script>
            <script src="${baseURL}/js/vendors/jquery-ui.js"></script>


            <script src="${baseURL}/js/vendors/d3.js"></script>
            <script src="${baseURL}/js/plugins/select.js"></script>
            <script src="${baseURL}/js/plugins/multiselect.js"></script>


            <script src="${baseURL}/js/plugins/datetimepicker.js"></script>
            <script src="${baseURL}/js/plugins/table.js"></script>
            <script src="${baseURL}/js/plugins/daterangepicker.js"></script>
            <script src="${baseURL}/js/plugins/gridstack.js"></script>

            <script src="${baseURL}/js/utilities.js"></script>
            <script src="${baseURL}/js/validator.js"></script>
            <script src="${baseURL}/js/dashboard.js"></script>

            <script src="${baseURL}/js/slvIframe.js"></script>

            <script src="${baseURL}/js/vendors/c3.js"></script>
            <script src="${baseURL}/js/vendors/H3.js"></script>
            <script src="${baseURL}/js/downloadURL.js"></script>


            <script src="${baseURL}/js/user_utils.js"></script>


        </head>
        <body>
        <c:if test="${param['mode'] ne 'dashboard'}">
            <jsp:include page="/WEB-INF/jsp/new-window-menu.jsp"/>
        </c:if>
        <div id="dashboardCanvas" class="dashboardCanvas">
            <div class="container-fluid" id="dashboard-canvas">
                <div id="main" class="hi-report-container">

                    <c:if test="${isAdhoc eq null}">
                        ${templateData}
                    </c:if>
                </div>
            </div>
        </div>
        <div data-keyboard="false" data-backdrop="static" id="hdi-blockUI" class="loader-modal modal fade"
             role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title"><b>Updating</b>
                            <span class="glyphicon glyphicon-repeat spin pull-right"></span></h4>
                    </div>
                    <div class="modal-body">
                        <p><b>Please wait while the report is being loaded...</b></p>

                        <p>Pending Requests: <span id="request-count">0</span></p>
                    </div>
                    <div class="modal-footer">
                        <p class="pull-left text-danger">Time elapsed: <span id="elpased_time">0:00</span></p>
                        <button type="button" class="btn btn-danger" data-dismiss="modal"
                                onClick="_reset_loading_panel()">Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div class="modal fade" id="error-panel">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h4 class="modal-title"><b>Error Occurred</b></h4>
                        </div>
                        <div class="modal-body">
                            <p class="text-danger"><span id="error-generated"></span></p>
                        </div>
                        <div class="modal-footer">
                            <input type="button" class="btn btn-primary" value="OK" data-dismiss="modal">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <c:if test="${fn:contains(urlParameters, 'print')}">
            <script>

                $(document).ready(function () {
                    setTimeout(
                            function () {
                                window.HDIUI.downloadURL();
                            }, 500);
                })

            </script>
        </c:if>

        <script src="${baseURL}/js/tooltip.js"></script>
        <script src="${baseURL}/js/file-browser.js"></script>

        </body>
        </html>
    </c:when>
    <c:otherwise>
        <script>
            window.DashboardGlobals.efwddir = "${dir}";

            <c:if test="${!empty urlParameters}">
            var urlParameters = ${urlParameters};
            </c:if>


            <c:if test="${!empty reportParameters}">
            reportParameters = ${reportParameters};
            window.DashboardGlobals.reportParameters = ${reportParameters};
            </c:if>
        </script>

        <c:if test="${fn:contains(urlParameters, 'print')}">
            <script type="text/javascript">
                $(document).ready(function () {
                    setTimeout(
                            function () {
                                window.HDIUI.downloadURL();
                            }, 500);
                });
            </script>
        </c:if>
        <c:if test="${isAdhoc eq null}">
            ${templateData}
        </c:if>
    </c:otherwise>
</c:choose>
<script type="text/javascript">
    <c:if test="${isAdhoc}">
    var url = "${baseURL}${templateData}&refresh=true";

    var iframe = document.createElement('iframe');
    iframe.frameBorder = 0;
    iframe.width = "100%";
    iframe.height = window.innerHeight;
    iframe.id = "hi-adhoc-iframe";
    iframe.setAttribute("src", url);
    document.getElementById("main").appendChild(iframe);
    </c:if>
</script>

<c:if test="${!empty param['parameters']}">
    <script type="text/javascript">
        var parameters = ${param['parameters']};
    </script>
</c:if>