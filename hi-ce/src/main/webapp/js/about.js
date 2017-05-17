
var $ = window.$;

window.HDIUI = window.HDIUI || {};

window.HDIUI.about = function () {
    var xhr;

    xhr = $.get(window.DashboardGlobals.productInfo);
    return xhr.done(function(data){
        var content, key, value;
        data = typeof data === "string" ? JSON.parse(data) : data;
        content = "<p>";
        for (key in data) {
            if (!hasProp.call(data, key)){ continue; }
            value = data[key];
            content += "<b>" + key + "</b> : " + value + "<br>";
        }
        content += "</p>";
        return bootbox.alert({
            title: "About HI",
            message: content
        });
    });
};

$("[data-about=hdi]").on("click", function (event) {
    window.HDIUI.about();
    event.preventDefault();
});
