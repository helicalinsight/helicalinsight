<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL"
       value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"/>
<c:set var="firstLoggedIn" value="set" scope="session"/>
<sec:authorize access="hasAnyRole('ROLE_PREVIOUS_ADMINISTRATOR')">
    <c:set var="firstLoggedIn" value="notSet" scope="session"/>
</sec:authorize>
<c:if test="${firstLoggedIn eq 'set'}">
    <c:set var="actualUserName"
           value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}" scope="session"/>

</c:if>
<nav class="navbar hi-navbar <tiles:insertAttribute name='fixedNavbar' ignore='true'/>" role="navigation">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle hi-navbar-toggle" data-toggle="collapse" data-target="#main-nav">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <div>
            	<span><a class="navbar-brand hi-logo">
                    <img src="${baseURL}/images/logo-hi.png">
                </a></span>

                <div class="hi-navbar-brand">
            		<span>
            			<a class="navbar-brand hi-logo">
                            <strong>Helical Insight</strong>
                        </a>
            		</span>
                </div>
            </div>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="main-nav">
            <ul class="nav navbar-nav hi-navbar-nav navbar-left">
                <!-- Tiles -->
                <li class="dropdown hi-hoverEffect">
                    <a href="#" data-place="right" data-tip="Home" class="dropdown-toggle" data-toggle="dropdown"
                       aria-expanded="false">
                        <i class="fa fa-th"></i>
                    </a>
                    <ul class="hi-dropdown-menu dropdown-menu dropdown-menu-left" role="menu">
                        <li>
                            <a href="${baseURL}/hi.html"><i class="fa fa-magnet"></i> HI</a>
                        </li>
                        <auth:authorize access="hasRole('ROLE_ADMIN')">
                            <li>
                                <a href="${baseURL}/admin.html"><i class="fa fa-cogs"></i> Admin</a>
                            </li>
                        </auth:authorize>

                    </ul>
                </li>
                <!-- File Browser -->

                <li class="hi-hoverEffect">
                    <a href="#" data-place="right" data-tip="Filebrowser" id="homeButton"
                       data-filebrowser="filebrowser">
                        <i class="fa fa-folder-open"></i>
                    </a>
                </li>
                <!-- File Browser Extra -->
            </ul>
            <ul class="nav navbar-nav hi-navbar-nav navbar-right">
                <tiles:insertAttribute name="rightMenu" ignore="true"/>
                <!-- About -->
                <li class="hi-hoverEffect">
                    <a href="#" data-about="hdi"><i class="fa fa-info-circle"></i>&nbsp;About</a>
                </li>

                <!-- Help -->
                <li class="hi-hoverEffect">
                    <a href="http://www.helicalinsight.com/learn/" target="_blank"><i class="fa fa-question"></i>&nbsp;Help
                    </a>
                </li>
                <li class="dropdown hi-hoverEffect">
                    <a href="#" class="dropdown-toggle handleOverflow" data-toggle="dropdown"
                       aria-expanded="false"><i class="fa fa-user"></i>&nbsp;&nbsp;
                        <sec:authorize access="hasAnyRole('ROLE_PREVIOUS_ADMINISTRATOR')">
                            ${actualUserName} as
                        </sec:authorize>
                        ${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}
                    </a>

                    <ul class="hi-dropdown-menu dropdown-menu" role="menu">

                        <li>
                            <a class="handleOverflow" id="handleAuthCss">
                                <i class="fa fa-at"></i>
                                <sec:authentication
                                        property="principal.loggedInUser.emailAddress"/></a>
                        </li>
                        <sec:authorize access="hasAnyRole('ROLE_PREVIOUS_ADMINISTRATOR')">
                            <li>
                                <a href="${baseURL}/j_spring_security_exit_user">
                                    <i class="fa fa-sign-out"></i>Logout (${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username})
                                </a>
                            </li>
                        </sec:authorize>
                        <li>
                            <a href="${baseURL}/j_spring_security_logout">
                                <i class="fa fa-sign-out"></i>Logout
                            </a>
                        </li>
                    </ul>
                </li>

            </ul>
        </div>
    </div>
</nav>