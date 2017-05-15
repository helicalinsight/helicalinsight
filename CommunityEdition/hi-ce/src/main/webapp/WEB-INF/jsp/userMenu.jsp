<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-default">
    <ul class="nav navbar-nav">
        <li>
            <a class="report-title"> <i class="flaticon-folder"></i> <span id="report-title">Report Name</span>
            </a>
        </li>
    </ul>
    <ul class="nav navbar-nav pull-right">
        <li>
            <a href="#" class="preview-mode" data-tip="Preview Report"> <i class="flaticon-preview-mode"></i> </a>
        </li>
        <li>
            <a href="#" id="hi-report-new-window" data-tip="Open in new window" target="_blank"> <i class="flaticon-new-window"></i> </a>
        </li>
        <li class="dropdown" data-tip="Export">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                <i class="flaticon-export"></i>
            </a>
            <ul class="hi-dropdown-menu dropdown-menu" role="menu">
                <c:if test="${applicationSettings.provideExportViaHtml eq true}">
                    <li class="disabled">
                        <a href="#" data-format="pdf" data-save="save">
                            <i class="fa fa-file-pdf-o"></i> HTML PDF
                        </a>
                    </li>
                    <li class="disabled">
                        <a href="#" data-format="png" data-save="save">
                            <i class="fa fa-file-image-o"></i> HTML PNG
                        </a>
                    </li>
                    <li class="disabled">
                        <a href="#" data-save="save" onclick="enablePrintFrame()">
                            <i class="fa fa-file-image-o"></i> HTML JPEG
                        </a>
                    </li>
                </c:if>
                <li class="disabled">
                    <a href="#" data-saveurl="true" data-format="pdf">
                        <i class="fa fa-file-pdf-o"></i> PDF
                    </a>
                </li>
                <li class="disabled">
                    <a href="#" data-saveurl="true" data-format="png">
                        <i class="fa fa-file-image-o"></i> PNG
                    </a>
                </li>
                <li class="disabled">
                    <a href="#" data-saveurl="true" data-format="jpg">
                        <i class="fa fa-file-image-o"></i> JPEG
                    </a>
                </li>
                <li class="disabled">
                    <a href="#" data-saveurl="true" data-format="xls">
                        <i class="fa fa-file-excel-o"></i> EXCEL
                    </a>
                </li>
            </ul>
        </li>


        <li class="dropdown" data-tip="Refresh">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                <i class="flaticon-reload"></i>
            </a>
            <ul class="hi-dropdown-menu dropdown-menu" role="menu">
                <li class="disabled">
                    <a href="#" data-refresh="refresh"><i class="fa fa-history"></i> Cache</a>
                </li>
                <li class="disabled">
                    <a href="#" data-refreshCurrent="refresh"><i class="fa fa-refresh"></i> Current Report</a>
                </li>
            </ul>
        </li>

        <!-- Mailing Reports -->
        <li class="dropdown" data-tip="Email / Schedule">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                <i class="flaticon-mail"></i>
            </a>
            <ul class="hi-dropdown-menu dropdown-menu" role="menu">
                <li class="disabled">
                    <a href="#" data-schedule="email"><i class="fa fa-envelope"></i> Email</a>
                </li>
                <li class="disabled">
                    <a href="#" data-schedule="report"><i class="fa fa-clock-o"></i> Schedule</a>
                </li>
            </ul>
        </li>

        <!-- Save Report -->
        <li class="disabled" data-tip="Save Report">
            <a href="#" data-saveReport="true" class="hi-theme-saveButton"> <i class="flaticon-save"></i> </a>
        </li>
    </ul>
</nav>