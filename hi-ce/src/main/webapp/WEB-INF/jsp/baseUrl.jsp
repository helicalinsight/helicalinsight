<%@ page import="com.helicalinsight.efw.framework.utils.ApplicationContextAccessor" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextUrl">${pageContext.request.requestURL}</c:set>
<c:set var="baseURL" value="${fn:replace(contextUrl, pageContext.request.requestURI, pageContext.request.contextPath)}"
       scope="session"/>
<c:set var="applicationSettings" value='<%=ApplicationContextAccessor.getBean("applicationSettings")%>'
       scope="session"/>
