<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>

<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL"
       value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"/>

<!DOCTYPE html>
<html class="hi-window">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="decorator" content="minimal"/>
    <link rel="icon" type="image/x-icon" href="${baseURL}/images/favicon.ico"/>
    <title><tiles:insertAttribute name="title"/></title>
    <link rel="stylesheet" href="${baseURL}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${baseURL}/css/custom.css">
</head>

<tiles:insertAttribute name="body-content"/>
<script src="${baseURL}/js/vendors/jquery.js"></script>
<script src="${baseURL}/js/vendors/bootstrap.js"></script>
<script>
    $("#login-admin").click(function () {
        $("#user-name").val("hiadmin");
        $("#user-password").val("hiadmin");
        $("#login").click();
    });
    $("#login-user").click(function () {
        $("#user-name").val("hiuser");
        $("#user-password").val("hiuser");
        $("#login").click();
    });

</script>
</body>
</html>