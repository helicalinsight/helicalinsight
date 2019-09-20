<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${(param['dir'] ne null) and (param['file'] ne null)}">
	<script>(function(w){var d = "${param['dir']}",f = "${param['file']}";w.localStorage.setItem("hdi_ce_report",JSON.stringify({dir: d, file: f}))})(window);</script>
</c:if>


<script src="${baseURL}/js/editor/vendor.js"></script>
<script src="${baseURL}/js/editor/editor.js"></script>
<script src="${baseURL}/js/editor/ce-report-edit.js"></script>
		