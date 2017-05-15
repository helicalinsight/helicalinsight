<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="activeClass"><tiles:getAsString name='activeClass' ignore='true'/></c:set>
    <!--nav starts-->
    <nav class="navbar navbar-default primary-nav">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="${baseURL}/"><img src="${baseURL}/images/alpha/logo.svg"> <span>Community <br> Edition</span></a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="http://www.helicalinsight.com/learn/" target="_blank"> <i class="flaticon-help"></i>
                        Help</a></li> 
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>
