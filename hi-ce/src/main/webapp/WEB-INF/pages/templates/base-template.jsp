<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/baseUrl.jsp"/>
    <jsp:include page="/WEB-INF/jsp/common-meta.jsp"/>
	<tiles:insertAttribute name="commonCss" ignore="true"/>
	<tiles:insertAttribute name="pageCss" ignore="true"/>
    <title>HI: <tiles:insertAttribute name="title"/></title>
</head>
<body style="padding-right: 0 !important">
<tiles:insertAttribute name="main-nav"/>
<tiles:insertAttribute name="sidebar"/>
<tiles:insertAttribute name="body"/>
<jsp:include page="/WEB-INF/jsp/common-js.jsp"/>
<tiles:insertAttribute name="pageScripts" ignore="true"/>
<tiles:insertAttribute name="footer" ignore="true"/>
</body>
</html>
