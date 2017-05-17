<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
    <c:set var="baseURL"
           value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"
           scope="request"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="decorator" content="minimal"/>
    <link rel="icon" type="image/x-icon" href="${baseURL}/images/favicon.ico"/>
    <title>HI: Error</title>
    <link rel="stylesheet" href="${baseURL}/css/fonts.css"/>
    <link rel="stylesheet" href="${baseURL}/css/styles.css">
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ui-theme.css"/> --%>
    <link rel="stylesheet" href="${baseURL}/css/styles.css"/>
    <link rel="stylesheet" href="${baseURL}/css/custom.css"/>
</head>
<body class="error-page">
<c:if test="${empty param['mode']}">
    <jsp:include page="/WEB-INF/pages/templates/error-header.jsp"/>
</c:if>
<div class="col-md-12 col-sm-12 col-xs-12 error-info">
    <h5>Oops!</h5> <br/>
    <img src="${baseURL}/images/alpha/404.svg" class="img-responsive error-img"/>
    <h5>An error has occurred. Please see your system administrator</h5>
    <a href="${baseURL}" class="btn btn-primary"> Return to home </a>

    <div class="alert alert-danger">
        <c:if test="${!empty message}">
            <p><c:out value="${message}"/></p>
        </c:if>

        <c:if test="${!empty requestScope.errorMessage}">
            <p><c:out value="${requestScope.errorMessage}"/></p>
        </c:if>
    </div>
<div>

        <script src="${baseURL}/js/vendors/jquery.js"></script>
        <script src="${baseURL}/js/vendors/bootstrap.js"></script>
</body>

