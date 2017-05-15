<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<sec:authentication property="authorities" var="authorities"/>
<c:set var="organizationAdmin" value=""/>

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
                        <h6><span
                                class="profile-picture">${fn:toUpperCase(fn:substring(loggedinUserName,0,2))}</span>
                           <c:out value="${loggedinUserName}" />
                        </h6>

                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <ul class="list-unstyled">
                                <li class="col-md-12 col-sm-12 col-xs-12 nopad">

                                    <c:set var="profileArray"
                                           value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.profiles}"/>
                                    <c:forEach var="userProfile" items="${profileArray}">
                                        <c:set var="profileOfUser"
                                               value="${profileOfUser} ${userProfile.profile_name}"/>
                                    </c:forEach>

                                    <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space">Profile : </span>
                                            <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"
                                                  data-place="right" data-tip="<c:out value="[${profileOfUser}]"/>">
                                            <c:out value="${profileOfUser}"/>
                                            </span></li>
                                <li class="col-md-12 col-sm-12 col-xs-12 nopad">
                                    <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"> Role : </span>
                                         <span class="col-md-6 col-sm-6 col-xs-6 nopad white-space"
                                               data-place="right" data-tip="<c:out value='${authorities}'/>">
                                               <c:forEach var="role" items="${authorities}">
                                                   <c:out value="${role} "/>
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

        </div>
        <div class="col-md-6 col-sm-6 col-xs-12 whats-new">
            <div class="panel panel-default">
                <div class="panel-body">
                    <h4>What's New?</h4>
                    <ul>
                        <li>New version 2.0 is released.</li>
                        <li>New generation UI with one click access.</li>
                        <li>Exporting and printing bugs fixed.</li>
                        <li>Direct links to tutorials.</li>
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


