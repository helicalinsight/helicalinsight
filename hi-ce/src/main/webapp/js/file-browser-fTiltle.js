$(document).ready(function() {
    $(window).load(function (){
        $('iframe').load(function() {
            // var fileId = $(document.getElementById('dashboard-canvas').contentWindow.document.getElementById('hi-fileTitle'));
            var fileId = $('#dashboard-canvas').contents().find("#hi-fileTitle");
            // var fileId = $(document.getElementById('dashboard-canvas').contentDocument.getElementById('hi-fileTitle'));
            fileId.tooltip({'title': "Open File Browser", 'placement': 'bottom'});
            fileId.css('cursor', 'pointer');
            fileId.on("click", function() {
                var newpath = DashboardGlobals.currentReport.__path.substring("1").split("/");
                var popped = newpath.pop();
                newpath = newpath.join("/");
                var path = newpath +"/"+ DashboardGlobals.file;
                var path1 = DashboardGlobals.currentReport.__path.substring("1");

                HDIUI.filebrowser.show();
                HDIUI.filebrowser.updateBreadCrumb(path1);

                setTimeout(
                    function()
                    {
                        HDIUI.filebrowser.filetree.expandGivenPath(path);
                    }, 500);
            });
        });
    });
});
