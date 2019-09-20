<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<sec:authentication property="authorities" var="authorities"/>
<c:set var="organizationAdmin" value=""/>
<c:set var="firstLoggedIn" value="set" scope="session"/>
<sec:authorize access="hasAnyRole('ROLE_PREVIOUS_ADMINISTRATOR')">
    <c:set var="firstLoggedIn" value="notSet" scope="session"/>
</sec:authorize>
<c:if test="${firstLoggedIn eq 'set'}">
    <c:set var="actualUserName"
           value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}" scope="session"/>

</c:if>

<div class="col-md-10 col-md-offset-2 col-sm-9 col-sm-offset-3 col-xs-12 body-block ">
    <div class="col-md-12 col-sm-12 col-xs-12 report-iframe">
        <div class="panel panel-default">
            <div class="panel-body nopad">
                <jsp:include page="/WEB-INF/jsp/userMenu.jsp"/>
                <iframe class="col-md-12 col-sm-12 col-xs-12 nopad" id="dashboard-canvas" name="parent_iframe"></iframe>
            </div>
        </div>
    </div>

    <div class="col-md-12 col-sm-12 col-xs-12 home-page">
        <div class="col-md-6 col-sm-6 col-xs-12 home-profile">
            <div class="panel panel-default">
                <div class="panel-body nopad">
                    <div class="col-md-6 col-sm-12 col-xs-12 nopad">
                        <c:set var="loggedinUserName" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}"/>
                        <h6><span class="profile-picture">${fn:toUpperCase(fn:substring(loggedinUserName,0,2))}</span>
                            <sec:authorize access="hasAnyRole('ROLE_PREVIOUS_ADMINISTRATOR')">
                                ${actualUserName} as
                            </sec:authorize>
                            <c:out value="${loggedinUserName}" />
                        </h6>

                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <ul class="list-unstyled">
                                <li class="col-md-12 col-sm-12 col-xs-12 nopad">

                                    <c:set var="profileArray"
                                           value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.profiles}"/>
                                    <c:forEach var="userProfile" items="${profileArray}" varStatus="status">
                                        <c:if test="${status.index!=0}">
                                            <c:set var="profileOfUser"  value="${profileOfUser}, ${userProfile.profile_name}"/>
                                        </c:if>
                                        <c:if test="${status.index eq 0}">

                                            <c:set var="profileOfUser"
                                                   value="${profileOfUser} ${userProfile.profile_name}"/>
                                        </c:if>
                                    </c:forEach>

                                    <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space">Profile : </span>
                                            <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"
                                                  data-place="right" data-tip="<c:out value="[${profileOfUser}]"/>">
                                            <c:out value="${profileOfUser}"/>
                                            </span></li>

                                <li class="col-md-12 col-sm-12 col-xs-12 nopad">
                                    <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"> Role : </span>
                                         <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"
                                               data-place="right" data-tip='<c:forEach var="role" items="${authorities}" varStatus="status"><c:if test="${not fn:contains(role.toString(),\'Switch\')}"><c:if test="${status.index!=0}">,</c:if><c:out value="${role} "/></c:if></c:forEach>'>
                                             <c:forEach var="role" items="${authorities}" varStatus="status">

                                                 <c:if test="${not fn:contains(role.toString(),'Switch')}">
                                                     <c:if test="${status.index!=0}">,</c:if>
                                                     <c:out value="${role}"/>
                                                 </c:if>
                                             </c:forEach>
                                          </span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-6 col-sm-12 col-xs-12  profile-stats">
                        <div class="col-md-12 col-sm-12 col-xs-12 nopad">
                            <ul class="list-unstyled">
                                <li class="col-md-12 col-sm-12 col-xs-12">
                                    <i class="flaticon-dashboard"></i>
                                    <h4>
                                        <span id="reportsStats"></span>
                                        <small>Reports</small>
                                    </h4>
                                </li>
                                <li class="col-md-12 col-sm-12 col-xs-12">
                                    <i class="flaticon-database"></i>
                                    <h4>
                                        <span id="dsStats"></span>
                                        <small>Data Sources</small>
                                    </h4>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

            </div>

            <div>

            </div>
        </div>

        <div class="col-md-6 col-sm-6 col-xs-12 whats-new">
            <div class="panel panel-default">
                <div class="panel-body">
                    <h4>What's New?</h4>

                    <ul>
                        <li>REST Login using JWT Token based Auth</li>
                        <li>Reports editor UI available	</li>
                        <li>UI Support for impersonate/MIMIC login</li>
                        <li>New "Community Report" in Sample Reports</li>


                        <li>Inbuilt JDBC drivers support for databases provided</li>
                        <li>Groovy Datasource support through Reports</li>
                        <li>File Browser Filter by type: Community Report for EFWCE file</li>
                        <li>Share: Permission levels and Inheritance rule</li>
                        <li class="github-link"><a target="_blank" href="https://github.com/helicalinsight/helicalinsight">Github</a></li>
                    </ul>



                </div>
            </div>
        </div>

        <div class="col-md-12 col-sm-12 col-xs-12 sample-reports">
            <div class="panel panel-default">
                <div class="panel-body">
                    <h4>Recent Reports</h4>
                    <ul class="list-unstyled" id="sampleReports">

                    </ul>
                </div>
            </div>
        </div>
    </div>

</div>


