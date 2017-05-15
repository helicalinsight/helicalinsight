<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="activeClass"><tiles:getAsString name='activeClass' ignore='true'/></c:set>
<!--nav starts-->
<nav class="navbar navbar-default primary-nav">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${baseURL}/"><img src="${baseURL}/images/alpha/logo.svg"> <span>Community <br> Edition</span></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="http://www.helicalinsight.com/learn/" target="_blank"> <i class="flaticon-help"></i>
                    Help</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false">
                        <c:set var="loggedinUserName"
                               value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}"/>

                        <span class="profile-pic">${fn:toUpperCase(fn:substring(loggedinUserName,0,2))}</span>
                        <c:out value="${loggedinUserName}"/>
                        <span class="caret"></span> </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a class="handleOverflow" id="handleAuthCss">
                                <i class="fa fa-at"></i>
                                <sec:authentication
                                        property="principal.loggedInUser.emailAddress"/></a>
                        </li>


                        <li>
                            <a href="${baseURL}/j_spring_security_logout">
                                <i class="fa fa-sign-out"></i>Logout
                            </a>
                        </li>
                    </ul>
                </li>

                </li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</nav>

<div class="col-md-10 col-md-offset-2 col-sm-9 col-sm-offset-3 col-xs-12 secondary-nav">
    <nav class="navbar navbar-default">
        <div class="col-md-12 col-sm-12 col-xs-12 nopad">
            <ul class="nav navbar-nav dragscroll">
                <li class="<c:if test="${activeClass eq 'hdi'}">active</c:if>">
                    <a href="${baseURL}/"><i class="flaticon-home"></i> Home</a></li>
                <li class="<c:if test="${activeClass eq 'datasource'}">active</c:if>">
                    <a href="${baseURL}/datasource-create.html"><i class="flaticon-data-sources"></i> Data Sources</a>
                </li>
                <li class="<c:if test="${activeClass eq 'report'}">active</c:if>">
                    <a href="${baseURL}/report.html"><i class="flaticon-reports"></i> Reports</a>
                </li>
            </ul>

        </div>
    </nav>
</div>
