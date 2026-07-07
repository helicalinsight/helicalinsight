<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<auth:authentication property="authorities" var="authorities"/>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/baseUrl.jsp"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="decorator" content="minimal"/>
    <link rel="icon" type="image/x-icon"
          href="${baseURL}/images/favicon.ico"/>
    <title>HI: Access Denied</title>
    <link rel="stylesheet" href="${baseURL}/css/styles.css"/>
    <link rel="stylesheet" href="${baseURL}/css/404.css"/>
</head>
<body>
<c:if test="${empty param['mode']}">
    <jsp:include page="/WEB-INF/jsp/errorPages/errorHeader.jsp" />
</c:if>
<main class="fourzerofour">
    <div>
        <div>
            <span> 401</span>
            <span> Access Denied</span>

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
            <span>You may not have sufficient privileges</span>
            <c:if test="${empty param['mode']}">
        <span>
  <c:if test="${fn:length(authorities) ne 0}">

      <a href="${baseURL}">
          <b>return to home?</b>
      </a>
  </c:if>
            <c:if test="${fn:length(authorities) eq 0}">
                <a href="${baseURL}/logout">
                    <b>Logout</b>
                </a>
            </c:if>

            <auth:authorize access="hasAnyRole('ROLE_VIEWER')">
                <a href="${baseURL}/logout">
                    <b>Logout</b>
                </a>
            </auth:authorize>

      </span>
            </c:if>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/errorPages/errorFooter.jsp" />

</body>
</html>