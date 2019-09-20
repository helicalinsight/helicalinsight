<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
    <c:set var="baseURL" value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}" scope="request"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="decorator" content="minimal"/>
    <link rel="icon" type="image/x-icon"
          href="${baseURL}/images/favicon.ico"/>
    <title>HI: User Not Found</title>

    <link rel="stylesheet" href="${baseURL}/css/styles.css"/>
    <link rel="stylesheet" href="${baseURL}/css/custom.css"/>
    <link rel="stylesheet" href="${baseURL}/css/404.css"/>
</head>
<body>
<c:if test="${empty param['mode']}">
    <jsp:include page="/WEB-INF/pages/templates/error-header.jsp"/>
</c:if>
<main class="fourzerofour">
    <div>
        <div>
            <span> No User</span>
            <span> The user is not found</span>

        </div>
        <svg viewBox='0 0 200 600'>
            <polygon points='118.302698 8 59.5369448 66.7657528 186.487016 193.715824 14 366.202839 153.491505 505.694344 68.1413353 591.044514 200 591.044514 200 8'></polygon>
        </svg>
    </div>
    <svg class='crack' viewBox='0 0 200 600'>
        <polyline points='118.302698 8 59.5369448 66.7657528 186.487016 193.715824 14 366.202839 153.491505 505.694344 68.1413353 591.044514'></polyline>
    </svg>
    <div>
        <svg viewBox='0 0 200 600'>
            <polygon points='118.302698 8 59.5369448 66.7657528 186.487016 193.715824 14 366.202839 153.491505 505.694344 68.1413353 591.044514 0 591.044514 0 8'></polygon>
        </svg>
        <div>
            <span>The user is either disabled or does not exists</span>
            <c:if test="${empty param['mode']}">
        <span>
        <a href="${baseURL}">
            <b>return to home?</b>
        </a>
      </span>
            </c:if>
        </div>
    </div>
</main>



<script src="${baseURL}/js/vendors/jquery.js"></script>
<script src="${baseURL}/js/vendors/bootstrap.js"></script>
</body>
