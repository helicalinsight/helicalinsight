<%@ page import="com.helicalinsight.efw.ApplicationProperties" %>
<%@ page import="com.helicalinsight.efw.resourceloader.BaseLoader" %>
<%@ page import="net.sf.json.JSONArray" %>

<%
    String dir = request.getParameter("dir");
    String file = request.getParameter("file");
    String resource = dir + "/" + file;

    BaseLoader baseLoader = new BaseLoader(ApplicationProperties.getInstance());
    try {
        String result = baseLoader.loadResources(resource, false);

        JSONArray jsonResult = JSONArray.fromObject(result);
        request.setAttribute("data", jsonResult);
    } catch (Exception ignore) {
    }
%>