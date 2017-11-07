<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<%@page session="true" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL"
       value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"/>

<body class="login-page">

<sec:authorize access="isAuthenticated()">
    <jsp:forward page="/welcome.html" />
</sec:authorize>

<div class="container">
    <div class="col-md-12 col-sm-12 col-xs-12 login-page-inner">
        <div class="col-md-12 col-sm-12 col-xs-12 login-box">

            <div class="col-md-4 col-sm-12 col-xs-12 login-form-block">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="login-logo col-md-12 col-sm-12 col-xs-12 nopad">
                        <div class="col-md-3 col-sm-2 col-xs-2 nopad">
                            <img src="${baseURL}/images/alpha/logo.svg" class="img-responsive login-logo">
                        </div>
                        <div class="col-md-4 col-sm-4 col-xs-4 nopad">
                            <span>Community <br> Edition</span>
                        </div>
                    </div>

                    <form autocomplete="off" name="hi-loginForm" id="hi-loginForm" class="form-horizontal" role="form" action="<c:url value='${baseURL}/j_spring_security_check' />">

                        <div class="form-group">
                            <label for="hi-login-org">User Name</label>
                            <div class="input-group">
                                <span class="input-group-addon"><img src="${baseURL}/images/alpha/user.png"></span>
                                <input id="user-name" type="text" class="form-control"  name="j_username" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="hi-login-org">Password</label>
                            <div class="input-group">
                                <span class="input-group-addon" ><img src="${baseURL}/images/alpha/pass.png"></span>
                                <input id="user-password" type="password" class="form-control" name="j_password" required>
                            </div>
                        </div>


                        <c:if test="${not empty error and not empty sessionScope['SPRING_SECURITY_LAST_EXCEPTION']}">
                            <c:if test="${fn:length(sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message ) gt 0}">
                                <div class="alert alert-danger" role="alert">
                                    <p>${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}</p>
                                </div>
                                <c:set var="SPRING_SECURITY_LAST_EXCEPTION" scope="session" value="" />
                            </c:if>

                            <c:if test="${not empty sessionScope['SPRING_SECURITY_LAST_EXCEPTION']}">
                                <div class="alert alert-danger" role="alert">
                                    <p>Please enter valid credentials.</p>
                                </div>
                                <c:set var="SPRING_SECURITY_LAST_EXCEPTION" scope="session" value="" />
                            </c:if>
                            <c:set var="error" scope="session" value="" />
                        </c:if>

                        <div class="form-group text-center">
                            <div>
                                <button name="submit" type="submit" id="login" class="btn btn-default login-btn" data-validate="#hi-login-userName, #hi-login-passWord" data-validate-on="click" value="Log In"> Log In</button>
                            </div>
                        </div>
                        <div class="form-group">
                            <ul class="list-inline nomar">
                                <li class="col-md-6 col-sm-6 col-xs-6 nopad text-center">
                                    <a href="#" id="login-admin" class="credientials-hint" >
                                        <img src="${baseURL}/images/alpha/admin_signin.png" class="img-responsive" data-place="bottom" >
                                        <p> Default
                                            <br> Admin </p>
                                    </a>
                                </li>
                                <li class="col-md-6 col-sm-6 col-xs-6 nopad text-center" >
                                    <a href="#" id="login-user" class="credientials-hint" >
                                        <img src="${baseURL}/images/alpha/user_signin.png" class="img-responsive" data-place="bottom" >
                                        <p> Default
                                            <br> User </p>
                                    </a>
                                </li>
                            </ul>
                        </div>

                    </form>
                </div>
            </div>

            <div class="col-md-8 col-sm-12 col-xs-12 nopad login-placeholder">
                <img src="${baseURL}/images/alpha/log_placeholder.png" class="img-responsive">
                <h1 class="login-text">Visualize, Analyze, Be Wise</h1>
            </div>

        </div>
    </div>

</div>