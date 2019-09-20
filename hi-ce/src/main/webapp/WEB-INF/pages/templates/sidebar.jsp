<%@ taglib prefix="auth" uri="http://www.springframework.org/security/tags" %>
<!--Side bar-->
    <div class="col-sm-3 col-md-2 col-xs-12 sidebar">
        <div class="col-md-12 col-sm-12 col-xs-12 sidebar-inner">
            <ul class="nav nav-sidebar discover-block">
                <li>
                    <h4>Discover</h4></li>
                <li>
                    <p>Training</p>
                </li>
                <li>
                    <a role="button" data-toggle="collapse" href="#getting-started" aria-expanded="false"
                       aria-controls="getting-started">
                        <i class="flaticon-play-button-1"></i> Getting Started</a>
                    <ul class="collapse" id="getting-started">
                        <li><a href="https://helicalinsight.github.io/helicalinsight/#/quickstart" target="_blank">
                            Helical insight</a></li>
                        <li><a href="https://helicalinsight.github.io/helicalinsight/#/user-role-management" target="_blank">
                            User & role management</a></li>

                        <li><a href="https://helicalinsight.github.io/helicalinsight/#/ui-introduction"
                               target="_blank">
                            UI Introduction </a></li>
                    </ul>

                </li>
                <li>
                    <a role="button" data-toggle="collapse" href="#connecting-data" aria-expanded="false"
                       aria-controls="connecting-data">
                        <i class="flaticon-play-button-1"></i> Connecting to Data</a>
                    <ul class="collapse" id="connecting-data">
                        <li><a href="https://helicalinsight.github.io/helicalinsight/#/connecting-to-datasource"
                               target="_blank">
                            Connecting to a data source </a></li>
                        <li><a href="https://helicalinsight.github.io/helicalinsight/#/view-edit-datasources"
                               target="_blank">
                            View & edit existing data sources </a></li>
                    </ul>

                </li>
                <li>
                    <a target="_blank" href="https://helicalinsight.github.io/helicalinsight/#/create-efw-report">
                        <i class="flaticon-play-button-1"></i> Creating Reports</a>
                </li>
                <li>
                    <a target="_blank" href="https://helicalinsight.github.io/helicalinsight/"> <i class="flaticon-play-button-1"></i>
                        Learn More</a>
                </li>
            </ul>
        </div>
        <div class="col-md-12 col-sm-12 col-xs-12 sidebar-footer">

            <ul class="nav nav-sidebar">
                <auth:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
                <li>
                    <a class="file-browser-btn" href="#" data-place="right" data-tip="Filebrowser" id="homeButton"
                       data-filebrowser="filebrowser">
                        <i class="flaticon-folder"></i>File Browser</a></li>
                    </auth:authorize>
                <li class="version-copy">
                    <p class="pull-left" id="hi-version"></p>
                    <a href="http://www.helicalinsight.com" class="pull-right" target="_blank"> &copy;&nbsp;Helical
                        Insight CE</a>
                </li>
            </ul>
        </div>
    </div>

