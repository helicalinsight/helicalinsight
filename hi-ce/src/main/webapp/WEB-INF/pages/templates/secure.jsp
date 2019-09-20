<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<c:set var="organizationAdmin" value=""/>


<auth:authorize access="hasRole('ROLE_ADMIN')">


    <!DOCTYPE html>
    <html>

    <head>
        <jsp:include page="/WEB-INF/jsp/baseUrl.jsp"/>
        <jsp:include page="/WEB-INF/jsp/common-meta.jsp"/>
        <link rel="icon" type="image/x-icon" href="${baseURL}/images/favicon.ico"/>
        <title>HI: <tiles:insertAttribute name="title"/></title>
        <jsp:include page="/WEB-INF/jsp/common-css.jsp"/>
        <link rel="stylesheet" href="${baseURL}/css/admin.css">

    </head>
    <body  class="admin-page">
    <tiles:insertAttribute name="main-nav" ignore="true"/>
    <tiles:insertAttribute name="sidebar"/>
    <div class="col-md-10 col-md-offset-2 col-sm-9 col-sm-offset-3 col-xs-12 body-block">

        <div class="col-md-12 col-sm-12 col-xs-12 report-iframe">
            <div class="panel panel-default">
                <div class="panel-body nopad">
                    <jsp:include page="/WEB-INF/jsp/userMenu.jsp"/>
                    <iframe class="col-md-12 col-sm-12 col-xs-12 nopad" id="dashboard-canvas"
                            name="parent_iframe"></iframe>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 admin-page">
            <div class="panel panel-default admin-panel">
                <div class="panel-heading admin-head">
                    <ul class="nav nav-tabs action-tabs">
                        <li class="active"><a href="#">Overview</a></li>
                        <li><a href="#systemdetails">System Details</a></li>
                        <li><a href="#users">User Management</a></li>
                        <li><a href="#roles">Roles</a></li>
                        <li><a href="#scheduling">Scheduling</a></li>
                    </ul>
                </div>
                <div class="panel-body nopad">
                    <div class="tab-content action-tab-content col-md-12 col-sm-12 col-xs-12 nopad">
                        <div class="tab-pane active action-tab-pane col-md-12 col-sm-12 col-xs-12">
                            <div class="col-md-12 col-sm-12 col-xs-12" id="main">
                                <tiles:insertAttribute name="body-content"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/common-js.jsp"/>
    <jsp:include page="/WEB-INF/jsp/hdiscript.jsp"/>
    <script src="${baseURL}/js/admin/vendor.js"></script>
    <script src="${baseURL}/js/admin/app.js"></script>
    <jsp:include page="admin-overview.jsp"/>


    </body>
    </html>
</auth:authorize>