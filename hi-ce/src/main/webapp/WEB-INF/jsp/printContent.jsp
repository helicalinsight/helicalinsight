<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL" value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}" />
<script type="text/javascript" src="${baseURL}/js/upgrade/theme.js"></script>

<div id="printFrame" style="visibility: hidden"></div>
<script type="text/javascript" src="${baseURL}/js/printFrame.js"></script>