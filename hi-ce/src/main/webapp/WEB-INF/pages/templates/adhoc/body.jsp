<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active"><tiles:getAsString name='active' ignore='true'/></c:set>
<div class="col-md-10 col-md-offset-2 col-sm-9 col-sm-offset-3 col-xs-12 body-block">
    <div class="col-md-12 col-sm-12 col-xs-12 report-iframe">
        <div class="panel panel-default">
            <div class="panel-body nopad">
                <jsp:include page="/WEB-INF/jsp/userMenu.jsp"/>
                <iframe class="col-md-12 col-sm-12 col-xs-12 nopad" id="dashboard-canvas" name="parent_iframe"></iframe>
            </div>
        </div>
    </div>
    <div class="col-md-12 col-sm-12 col-xs-12 datasources-page">
        <ul class="nav nav-tabs action-tabs">
            <li class="<c:if test="${active eq 'datasource-create'}">active</c:if>">
                <a href="${baseURL}/datasource-create.html"><i class="flaticon-create"></i> Create</a></li>
            <li class="<c:if test="${active eq 'datasource-edit'}">active</c:if>">
                <a href="${baseURL}/datasource-edit.html"><i class="flaticon-edit"></i> Edit</a></li>
            <li class="<c:if test="${active eq 'datasource-share'}">active</c:if>">
                <a href="${baseURL}/datasource-share.html"><i class="flaticon-share"></i> Share</a></li>
        </ul>
        <div class="col-md-12 col-sm-12 col-xs-12 action-tab-content nopad">
            <div class="col-md-12 col-sm-12 col-xs-12 action-tab-pane active">
                <div class="col-md-12 col-sm-12 col-xs-12 sources-list">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="hi-adhoc-body" id='main'></div>
                        </div>
                    </div>
                </div>

                <div class="col-md-12 col-sm-12 col-xs-12 tutorials-block">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4><i class="flaticon-play-button-1"></i> Tutorials</h4>
                                <h5>Connecting Data Sources</h5>
                                <ul>
                                    <li>Click on Create data source</li>
                                    <li>Choose the data source type</li>
                                    <li>Enter data source name</li>
                                    <li>Select the driver from the list</li>
                                    <li>Enter username & password</li>
                                </ul>
                            </div>

                            <div class="col-md-6 col-sm-6 col-xs-12">

                                <a href="http://www.helicalinsight.com/videos/" target="_blank">
                                    <img src="${baseURL}/images/alpha/video-placeholder.png"
                                         class="img-responsive video-placeholder">
                                </a>
                            </div>

                        </div>
                    </div>

                </div>
            </div>
        </div>

    </div>

</div>