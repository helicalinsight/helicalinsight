<%@ page import="com.helicalinsight.efw.framework.utils.ApplicationContextAccessor" %>
<%-- <%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% response.setHeader("P3P","CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'"); %>
<c:if test="${empty baseURL}">
    <c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
    <c:set var="baseURL" value="${pageContext.request.contextPath}" scope="request"/>
    <c:set var="applicationSettings" value='<%=ApplicationContextAccessor.getBean("applicationSettings")%>'
           scope="request"/>
    <c:if test='${applicationSettings.getSettingJson().get("defaultBaseurl").getAsBoolean()}'>
        <c:set var="baseURL"
               value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"
               scope="request"/>
                </c:if>
    
</c:if>