<%@ page import="com.helicalinsight.efw.framework.utils.ApplicationContextAccessor" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="sec" %>
<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:if test="${empty applicationSettings}">
    <c:set var="applicationSettings" value='<%=ApplicationContextAccessor.getBean("applicationSettings")%>'
           scope="session"/>
</c:if>
<div>
    <auth:authorize access="hasRole('ROLE_ADMIN')">
        <c:if test="${param.file ne null and param.dir ne null}">
            <jsp:forward page="/hi.html"/>
        </c:if>

        <c:if test="${param.file eq null}">
            <jsp:forward page="/admin/home.html"/>
        </c:if>
    </auth:authorize>

    <auth:authorize access="isAuthenticated()">
        <jsp:forward page="/hi.html"/>
    </auth:authorize>
</div>