(function () {

    $(function () {
        var pgurl = window.location.href.substr(window.location.href
            .lastIndexOf("/") + 1);
        $(".secondary-nav .nav li").each(function () {
            if ($('a', this).attr("href") == pgurl || $(this).attr("href") == '')
                $(this).addClass("active");
        })

        $(".admin-panel .action-tabs li").each(function () {
            if ($('a', this).attr("href") == '#'+pgurl || $(this).attr("href") == ''){
                $(".admin-panel .action-tabs li").removeClass('active');
                $(this).addClass("active");
            }
        })
    });

    /*    var stickyOffset = $('.secondary-nav').offset().top;

     $(window).scroll(function () {
     var sticky = $('.secondary-nav'),
     scroll = $(window).scrollTop();

     if (scroll >= stickyOffset) {
     sticky.addClass('fixed-top');
     $('.primary-nav .navbar-brand').addClass('fixed-brand');
     return false;
     }
     else {
     sticky.removeClass('fixed-top');
     $('.primary-nav .navbar-brand').removeClass('fixed-brand');
     return false;
     }
     });*/

    $(".admin-head ul li a").click(function () {
        $(".admin-head ul li").removeClass('active');
        $(this).parent().addClass('active');
    });


    $(".preview-mode").click(function () {
        $("body").toggleClass('report-preview-mode');
        $(this).toggleClass('rotate');
    });


    $.get("getProductInformation.html", function (data) {
        $("#hi-version").text(data.Version);
    });


    $("#report-title").on("click", function () {
        var physicalPath = DashboardGlobals.currentReport.path.replace(/\\/g, "/");
        var logicalPath = DashboardGlobals.currentReport.__path.substring("1");
        HDIUI.filebrowser.show();
        HDIUI.filebrowser.updateBreadCrumb(logicalPath);
        setTimeout(
            function () {
                HDIUI.filebrowser.filetree.expandGivenPath(physicalPath);
            }, 500);
    });


})();
	
	
	
	
	
	