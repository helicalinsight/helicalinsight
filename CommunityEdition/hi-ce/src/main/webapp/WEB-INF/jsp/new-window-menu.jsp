<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class='hi-iframe-navbar hi-container-fluid hidden-print' id="hi-cacheToolbar">
    <div class='row hi-row'>
        <div class='customColor-Text'>
            <ul class="nav navbar-nav hi-navbar-nav navbar-left hi-list-inline col-xs-5">
                <ul class="list-inline pull-left" id="hi-list">
                    <li id="hi-cache-fileTitle">
                        <a class="hi-iframe-CSS" id="hi-fileTitle" data-tip="${data[0].title}">${data[0].title}</a>
                    </li>
                </ul>
            </ul>
            <ul class='hi-list-inline list-inline col-xs-7' id='hi-list'>
                
                
                <li class='dropdown pull-right customColor-Text hi-iframe-hoverEffect'>
                    <a href='#' class='dropdown-toggle' data-toggle='dropdown' id="hi-export-icon" aria-expanded='false'>
                        <span class="hi-cache-toolbar-screen"><i class='fa fa-download'></i>&nbsp;Export</span>
                        <span class='fa fa-download hi-cache-toolbar-mobile'></span>
                    </a>
                    <ul class='dropdown-menu hi-dropdown-menu dropdown-menu-right' role='menu'>
					<c:if test="${applicationSettings.provideExportViaHtml eq true}">
                        <li>
                            <a href='#' class='handleIframeCSS' id="hi-export-pdf" data-format='pdf'>
                                <i class='fa fa-file-pdf-o'></i> HTML PDF
                            </a>
                        </li>
                        <li>
                            <a href='#' class='handleIframeCSS' id="hi-export-png" data-format='png'>
                                <i class='fa fa-file-image-o'></i> HTML PNG
                            </a>
                        </li>
                        <li>
                            <a href='#' class='handleIframeCSS' id="hi-export-jpg" data-format='jpg'>
                                <i class='fa fa-file-image-o'></i> HTML JPEG
                            </a>
                        </li>
					</c:if>
                        <li>
                            <a href="#"  data-format="pdf"  data-saveurl="true">
                                <i class="fa fa-file-pdf-o"></i> PDF
                            </a>
                        </li>
                        <li>
                            <a href="#"  data-format="png" data-saveurl="true">
                                <i class="fa fa-file-image-o"></i> PNG
                            </a>
                        </li>
                        <li>
                            <a href="#"  data-format="jpg" data-saveurl="true">
                                <i class="fa fa-file-image-o"></i> JPEG
                            </a>
                        </li>
						<li>
                            <a href="#" data-format="xls"  data-saveurl="true">
                                <i class="fa fa-file-excel-o"></i> EXCEL
                            </a>
                        </li>
                    </ul>
                </li>
                <li class='hi-iframe-hoverEffect pull-right customColor-Text'>
                    <a href='#' id='hi-cache-toolbar-refresh' class='hi-iframe-CSS'>
                        <span class="hi-cache-toolbar-screen"><i class='fa fa-history'></i>&nbsp;Cache</span>
                        <span class='fa fa-history hi-cache-toolbar-mobile'></span>
                    </a>
                </li>
                <li class='customColor-Text pull-right hi-cache-time-textColor' id='cacheRefresh' data-refresh="refreshTime" data-toggle="tooltip">
                </li>
                <span class="hi-IE-specific pull-right" data-toggle="tooltip" data-refresh="refreshTime" style="padding: 0 9px 0 9px;"></span>
            </ul>
        </div>
    </div>
</div>
