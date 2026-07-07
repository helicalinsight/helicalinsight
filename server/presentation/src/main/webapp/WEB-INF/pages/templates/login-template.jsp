<%@ page import="java.util.Enumeration" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- <%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %> --%>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="/WEB-INF/jsp/baseUrl.jsp"/>
<%-- <!--Test added to handle jetty issue--> --%>
<c:if test="${empty sessionScope or (empty sessionScope.SPRING_SECURITY_CONTEXT)}">

</c:if>
<%-- <tiles:insertAttribute name="body" ignore="true"/> --%>
<c:if test="${empty sessionScope or (empty sessionScope.SPRING_SECURITY_CONTEXT)}">

</c:if>