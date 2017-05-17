var HDI, hasProp = {}.hasOwnProperty;
!function (e) {
    "use strict";
    var a;
    a = {_keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=", encode: function (e) {
        var t, r, s, o, l, i, n, d, c;
        for (c = "", d = 0, e = a._utf8_encode(e); d < e.length;)t = e.charCodeAt(d++), r = e.charCodeAt(d++), s = e.charCodeAt(d++), o = t >> 2, l = (3 & t) << 4 | r >> 4, i = (15 & r) << 2 | s >> 6, n = 63 & s, isNaN(r) ? i = n = 64 : isNaN(s) && (n = 64), c = c + this._keyStr.charAt(o) + this._keyStr.charAt(l) + this._keyStr.charAt(i) + this._keyStr.charAt(n);
        return c.replace(/\u0000/g, "")
    }, decode: function (e) {
        var t, r, s, o, l, i, n, d, c;
        for (c = "", d = 0, e = e.replace(/[^A-Za-z0-9\+\/\=]/g, ""); d < e.length;)o = this._keyStr.indexOf(e.charAt(d++)), l = this._keyStr.indexOf(e.charAt(d++)), i = this._keyStr.indexOf(e.charAt(d++)), n = this._keyStr.indexOf(e.charAt(d++)), t = o << 2 | l >> 4, r = (15 & l) << 4 | i >> 2, s = (3 & i) << 6 | n, c += String.fromCharCode(t), 64 !== i && (c += String.fromCharCode(r)), 64 !== n && (c += String.fromCharCode(s));
        return c = a._utf8_decode(c), c.replace(/\u0000/g, "")
    }, _utf8_encode: function (e) {
        var a, t, r, s, o;
        for (e = e.replace(/\r\n/g, "\n"), o = "", r = t = 0, s = e.length; 0 <= s ? t <= s : t >= s; r = 0 <= s ? ++t : --t)a = e.charCodeAt(r), a < 128 ? o += String.fromCharCode(a) : a > 127 && a < 2048 ? (o += String.fromCharCode(a >> 6 | 192), o += String.fromCharCode(63 & a | 128)) : (o += String.fromCharCode(a >> 12 | 224), o += String.fromCharCode(a >> 6 & 63 | 128), o += String.fromCharCode(63 & a | 128));
        return o
    }, _utf8_decode: function (e) {
        var a, t, r, s, o, l;
        for (l = "", o = 0, t = 0, r = 0, a = 0; o < e.length;)a = e.charCodeAt(o), a < 128 ? (l += String.fromCharCode(a), o++) : a > 191 && a < 224 ? (r = e.charCodeAt(o + 1), l += String.fromCharCode((31 & a) << 6 | 63 & r), o += 2) : (r = e.charCodeAt(o + 1), s = e.charCodeAt(o + 2), l += String.fromCharCode((15 & a) << 12 | (63 & r) << 6 | 63 & s), o += 3);
        return l
    }}, e.Base64 = a
}(this), $.download = function (e, a, t) {
    var r, s, o, l, i, n, d, c, p, h;
    if (null == t && (t = "post"), d = function (e, a, t) {
        return document.cookie = e + "=" + a + "; Path=/" + t
    }, a && e) {
        c = window.location.pathname.substring(1).split("/")[0], r = d("hi_report_downloadStatus", 0, c), o = function () {
            var e, a, t, r, s;
            for (a = document.cookie.split(";"), t = {}, r = 0, s = a.length; r < s;)e = a[r].split("="), 2 === e.length ? (t[e[0].trim()] = e[1].trim(), r++) : r++;
            return t
        }, p = !1, n = setInterval(function () {
            var e;
            e = o(), "0" !== e.hi_report_downloadStatus || p || (bootbox.dialog({message: "Please wait while downloading", closeButton: !1, title: 'Downloading<i class="fa fa-repeat spin pull-right"></i>', buttons: {danger: {label: "Close", className: "btn-danger hi-cancelDownload", callback: function () {
                return $(".hi-cancelDownload").on("click", function () {
                    return!1
                })
            }}}}), p = !0), "1" === e.hi_report_downloadStatus && (clearInterval(n), bootbox.hideAll(), e.hi_report_downloadStatus = "0")
        }, 100), s = $(document.createElement("form")).attr("method", t).attr("action", e);
        for (i in a)hasProp.call(a, i) && (h = a[i], l = $(document.createElement("input")).attr("name", i).attr("value", h).attr("type", "hidden"), s.append(l));
        s.appendTo("body").submit().remove()
    }
}, HDI = HDI || {}, HDI.validate = {filename: function (e) {
    return/^\w[\w \.\-\&\#\+\~]*/i.test(e)
}}, function (e, a, t, r, s) {
    var o, l, i, n, d;
    o = {_requests: {}}, r.templateSettings = {evaluate: /\{%(.*?)%\}/g, interpolate: /\{\{(.*?)\}\}/g}, o.about = function () {
        var a;
        return a = t.get(e.DashboardGlobals.productInfo), a.done(function (e) {
            var a, t, r;
            e = "string" == typeof e ? JSON.parse(e) : e, a = "<p>";
            for (t in e)hasProp.call(e, t) && (r = e[t], a += "<b>" + t + "</b> : " + r + "<br>");
            return a += "</p>", bootbox.alert({title: "About HDI", message: a})
        })
    }, o.saveAs = function (r, s, o, l) {
        var i, n, d, c, p, h, f, b, u, m, v, g;
        n = '<base href="' + e.DashboardGlobals.baseUrl + '">', i = t("#dashboard-canvas").contents().find("iframe").filter("iframe[name='viz']"), d = void 0 === typeof i || 0 === i.length ? t("#dashboard-canvas").contents().find("html").clone() : i.contents().find("html").clone(), c = function () {
            var e, t, r, s, o;
            if (o = a.getElementById("dashboard-canvas").contentDocument.querySelectorAll("style"), r = [], !o)return"";
            for (t = 0; t < o.length;)s = o[t], "" === s.textContent ? (s.sheet.cssRules && (e = [].reduce.call(s.sheet.cssRules, function (e, a) {
                return e += a.cssText
            }, ""), r.push(e)), t += 1) : t += 1;
            return r.join("\n")
        }, h = a.createElement("style"), h.type = "text/css", h.textContent = c(), d.find("script:not([data-strip=false])").remove(), t("#dashboard-canvas").contents().find("html").find("URI"), d.find("head").prepend(n), d.find("head").append(h), d.find("head").append("<style>.hidden-print {display:none !important;} .visible-print {display: block !important;}</style>"), g = "<html>" + d.html() + "</html>", g = encodeURIComponent(e.btoa ? btoa(unescape(encodeURIComponent(g))) : Base64.encode(g)), b = {}, v = Dashboard.getAllVariables();
        for (f in v)hasProp.call(v, f) && (m = v[f], "" === m || "object" == typeof m && 0 === m.length || (b[f] = m));
        return u = e.DashboardGlobals.file.split(".")[1], p = {xml: g, format: r}, void 0 !== typeof e.DashboardGlobals.optionalReportParams.reportname && (p.dir = e.DashboardGlobals.folderpath, p.filename = e.DashboardGlobals.file, p.reportType = e.DashboardGlobals.extension, p.reportNameParam = e.DashboardGlobals.fileTitle, p.reportFile = e.DashboardGlobals.file, p.resultDirectory = o || e.DashboardGlobals.optionalReportParams.location || "Save Result", p.reportName = s || e.DashboardGlobals.optionalReportParams.reportname), l || t.download(e.DashboardGlobals.reportDownload, p), g
    }, o.downloadURL = function (r, s, o, l) {
        var i, n, d, c, p, h, f;
        if (r || urlParameters.hasOwnProperty("print") && (r = urlParameters.print), r && /^[a-z]*$/.test(r) === !1 || "" === r)return t.notify({message: "Print cannot be empty", icon: "fa fa-exclamation-triangle"}, {type: "danger"}), !1;
        n = {format: r}, n.xml = "", c = {}, i = a.getElementById("dashboard-canvas").contentWindow, i.Dashboard.getAllVariables(), i.Dashboard.getAllVariables() ? (f = i.Dashboard.getAllVariables(), Dashboard.getVariable("printOptions") && null !== Dashboard.getVariable("printOptions") && (f.printOptions = Dashboard.getVariable("printOptions"))) : f = Dashboard.getAllVariables();
        for (d in f)hasProp.call(f, d) && (h = f[d], "" === h || "object" == typeof h && 0 === h.length || (c[d] = h));
        p = e.DashboardGlobals.file.split(".")[1], void 0 !== typeof e.DashboardGlobals.optionalReportParams.reportname && (n.dir = e.DashboardGlobals.folderpath, n.filename = e.DashboardGlobals.file, n.reportType = p, n.reportNameParam = e.DashboardGlobals.fileTitle, n.reportFile = e.DashboardGlobals.file, n.resultDirectory = o || e.DashboardGlobals.optionalReportParams.location || "Save Result", n.reportName = s || e.DashboardGlobals.optionalReportParams.reportname, c.mode = "efwsr" === p ? "open" : "", n.reportParameters = c ? JSON.stringify(c) : ""), l || t.download(e.DashboardGlobals.reportDownload, n)
    }, o.toggleFavourite = function (a, r) {
        return t.post(e.DashboardGlobals.saveReport, {reportDirectory: r.path, reportFile: r.name, operation: "favourite", markAsFavourite: a, favouriteLocation: r.saveLocation})
    }, l = s.View.extend({initialize: function (e) {
        this.id = e.id, this.render()
    }, className: "modal fade", attributes: {"data-keyboard": !1, "data-backdrop": "static"}, render: function () {
        var e;
        return e = r.template(this.template), this.$el.html(e())
    }, show: function () {
        return this.$el.modal("show")
    }, hide: function () {
        return this.$el.modal("hide")
    }, template: '<div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <h4 class="modal-title"><b>Error Occurred</b></h4> </div> <div class="modal-body"> <p class="text-danger"><span id="error-generated"></span></p> <p>Pending Requests: <span id="request-count">0</span> has been Canceled</p> </div> <div class="modal-footer"> <input type="button" class="btn btn-primary" value="OK" data-dismiss="modal"> </div> </div> </div>'}), i = s.View.extend({initialize: function (e) {
        this.id = e.id, this.render()
    }, className: "modal fade", attributes: {"data-keyboard": !1, "data-backdrop": "static"}, render: function () {
        var e;
        return e = r.template(this.template), this.$el.html(e())
    }, show: function () {
        return this.$el.modal("show")
    }, hide: function () {
        return this.$el.modal("hide")
    }, template: '<div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <h4 class="modal-title"><b>Updating</b><i class="fa fa-repeat spin pull-right"></i></h4> </div> <div class="modal-body"> <p><b>Please wait while your request is being processed...</b></p> <p>Pending Requests: <span id="request-count">0</span></p> </div> <div class="modal-footer"> <p class="pull-left text-danger">Time elapsed: <span id="elpased_time">0:00</span></p> <button type="button" class="btn btn-danger" data-dismiss="modal">Close</button> </div> </div> </div>'}), o.saveReport = function (a, r) {
        var s, o, l, i, n;
        l = {command: "add", reportName: a, reportDirectory: e.DashboardGlobals.folderpath, reportFile: e.DashboardGlobals.file, location: r}, o = {}, n = Dashboard.getAllVariables();
        for (s in n)hasProp.call(n, s) && (i = n[s], "" === i || "object" == typeof i && 0 === i.length || (o[s] = i));
        return l.reportParameters = o ? JSON.stringify(o) : "", t.post(e.DashboardGlobals.saveReport, l)
    }, n = s.Model.extend({initialize: function (e) {
        return this.dateFormat = e.dateFormat || "YYYY-MM-DD HH:mm", this.reset()
    }, defaults: {DaysofWeek: null, Frequency: "Weekly", RepeatBy: "dayOfTheMonth", RepeatsEvery: 1, StartDate: null, EndDate: null, endsRadio: "Never", timeZone: null, EndAfterExecutions: 35}, reset: function (e) {
        return e || this.set("Frequency", this.defaults.Frequency), this.set("DaysofWeek", [moment().format("dddd")]), this.set("RepeatsEvery", this.defaults.RepeatsEvery), this.set("RepeatBy", this.defaults.RepeatBy), this.set("StartDate", moment().format(this.dateFormat)), this.set("EndDate", moment().format(this.dateFormat)), this.set("endsRadio", this.defaults.endsRadio), this.set("timeZone", moment().format("Z")), this.set("EndAfterExecutions", this.defaults.EndAfterExecutions)
    }}), d = s.View.extend({initialize: function (e) {
        this.id = e.id, this.dateFormat = this.model.get("dateFormat"), this.endOccurences = this.model.get("EndAfterExecutions"), this.attributes = new s.Model({mode: "schedule", step: 1}), this.listenTo(this.attributes, "change:mode", this.changeMode), this.listenTo(this.attributes, "change:step", this.switchTab), this.listenTo(this.model, "change", this.getScheduleSummary), this.listenTo(this.model, "change:Frequency", this.updateViewForFrequency), this.listenTo(this.model, "change:endsRadio", this.updateEnds), this.render()
    }, className: "modal fade", attributes: {"data-keyboard": !1, "data-backdrop": "static"}, events: {"click [data-step]": "setStep", "keyup #endsAfterOccurrences": "allowOnlyNumbers", "click #schedule-btn": "sendRequest", "click #email-btn": "sendRequest", "click #select-location-btn": "promptForSavePath", "change #timezone": "getScheduleSummary"}, bindings: {".weeks": "DaysofWeek", ".ends": "endsRadio", ".monthlyCheck": "RepeatBy", "#repeatEvery": "RepeatsEvery", "#repeatOrder": "Frequency", "#endsAfterOccurrences": "EndAfterExecutions", "#startsOn": {observe: "StartDate", events: ["hide.daterangepicker"], update: function (e, a, t, r) {
        e.data("daterangepicker").setStartDate(a), e.data("daterangepicker").setEndDate(a), e.val(a)
    }}, "#endsOnDate": {observe: "EndDate", events: ["hide.daterangepicker"], update: function (e, a, t, r) {
        e.data("daterangepicker").setStartDate(a), e.data("daterangepicker").setEndDate(a), e.val(a)
    }}, "#timezone": {observe: "timeZone", update: function (e, a, t, r) {
        e.select2("val", a)
    }}}, render: function () {
        var e, a, s, o;
        return s = r.template(this.template), o = this, this.$el.html(s()).modal({show: !1, background: "static"}).on("hidden.bs.modal", function () {
            try {
                o.resettingVariables()
            } catch (e) {
            }
            return t("#email-form").find("input[type=text], textarea").val(""), t("#formatValidation").find("input").prop("checked", !0)
        }), e = {singleDatePicker: !0, showDropdowns: !0, timePicker: !0, timePickerIncrement: 1, startDate: moment(), endDate: moment(), minDate: moment(), autoUpdateInput: !0, locale: {format: "DD/MM/YYYY h:mm A"}, format: this.dateFormat}, a = this.endOccurences, this.$("#startsOn").on("show.daterangepicker", function () {
            return t(this).daterangepicker(e)
        }), this.$("#startsOn").daterangepicker(e), this.$("#endsOnDate").daterangepicker(t.extend({}, e, {drops: "up"})), this.$("#startsOn").on("hide.daterangepicker", function (a, s) {
            var o;
            return o = r.extend(e, {startDate: s.startDate, endDate: s.startDate, minDate: s.startDate}), t("#endsOnDate").data("daterangepicker").setOptions(o)
        }), this.$("#never").on("click", function () {
            return t("#endsAfterOccurrences").unbind("focusout"), t(".error-msg").remove(), t("div .has-error").removeClass("has-error"), t("#endsAfterOccurrences").val(a)
        }), this.$("#timezone").select2(), this.$("#timezone").select2("val", moment().format("Z"), !0), this.getScheduleSummary(), this.stickit(), this
    }, show: function (e) {
        this.resetSteps(), this.model.reset(), this.attributes.set("mode", e), this.$el.modal("show"), t.resetValidate();
        try {
            this.settingVariables()
        } catch (a) {
        }
        t("#timezone").trigger("change"), "email" === e ? this.$("h4").html("<b>Email Options</b>") : this.$("h4").html("<b>Scheduling Options</b>")
    }, hide: function () {
        this.$el.modal("hide");
        try {
            this.resettingVariables()
        } catch (e) {
        }
    }, updateViewForFrequency: function (e, a) {
        var t, r;
        switch (this.model.reset(!0), this.$("#repeatOrder-text").text(a.replace("ly", "").replace("Dai", "Day").toLowerCase()), t = this.$("#repeatBy"), r = this.$("#repeatOn"), a) {
            case"Daily":
            case"Yearly":
                return t.addClass("hidden"), r.addClass("hidden");
            case"Weekly":
                return t.addClass("hidden"), r.removeClass("hidden");
            case"Monthly":
                return t.removeClass("hidden"), r.addClass("hidden")
        }
    }, updateEnds: function (e, a) {
        var t, r;
        return t = this.$("#endsOnDate"), r = this.$("#endsAfterOccurrences"), {Never: function () {
            return t.prop("disabled", !0), r.prop("disabled", !0)
        }, After: function () {
            return t.prop("disabled", !0), r.prop("disabled", !1)
        }, On: function () {
            return t.prop("disabled", !1), r.prop("disabled", !0)
        }}[a]()
    }, getScheduleSummary: function () {
        var e, a, r, s, o;
        switch (s = "", e = this.model.toJSON(), r = moment(e.StartDate, this.dateFormat), o = this.$("#timezone option:selected").text(), s += e.RepeatsEvery > 1 ? "Every " + e.RepeatsEvery + " " + e.Frequency.replace("ly", "").replace("Dai", "Day") + "(s)" : "" + e.Frequency, e.Frequency) {
            case"Weekly":
                s += t.isArray(e.DaysofWeek) ? " on " + e.DaysofWeek.join(", ") : " on " + e.DaysofWeek;
                break;
            case"Monthly":
                "dayOfTheMonth" === e.RepeatBy ? s += " on day " + r.format("D") : "dayOfTheWeek" === e.RepeatBy && (a = ["First", "Second", "Third", "Fourth", "Last"], s += " on  " + a[Math.ceil(r.date() / 7) - 1] + " " + r.format("dddd"));
                break;
            case"Yearly":
                s += " on  " + r.format("MMMM Do")
        }
        return s += " on " + r.format("HH:mm") + " (" + o + ")", "After" === e.endsRadio ? s += ", " + e.EndAfterExecutions + " times" : "On" === e.endsRadio && (s += ", until " + moment(e.EndDate, this.dateFormat).format("HH:mm Do MMMM, YYYY") + " (" + o + ")"), this.$("#schedule-summary").html(s)
    }, settingVariables: function () {
        var r, s, o, l;
        e.Dashboard = a.getElementById("dashboard-canvas").contentWindow ? a.getElementById("dashboard-canvas").contentWindow.Dashboard : e.Dashboard, o = Dashboard.getAllVariables(), this.$("#outputVariables").addClass("form-horizontal");
        for (s in o)hasProp.call(o, s) && (l = o[s], "" === l || "object" == typeof l && 0 === l.length || (o[s] = l, r = t.isArray(l), this.$("#outputVariables").append('<div class="form-group"> <label class="col-sm-3 control-label overflow">' + s + '</label> <div class="col-sm-9"> <input class="form-control overflow" name="' + s + '" value="' + l + '" title="' + l + '" ' + (r ? 'data-array="true"' : "") + " readonly> </div> </div>")))
    }, resettingVariables: function () {
        return this.$("#outputVariables").empty()
    }, getParameters: function () {
        var e, t, r, s, o;
        r = {}, e = a.getElementById("dashboard-canvas").contentWindow, e.Dashboard.getAllVariables(), o = e.Dashboard.getAllVariables() ? e.Dashboard.getAllVariables() : Dashboard.getAllVariables();
        for (t in o)hasProp.call(o, t) && (s = o[t], "" === s || "object" == typeof s && 0 === s.length || (r[t] = s));
        return r
    }, sendRequest: function (a) {
        var r, s, l, i, n, d, c, p, h, f, b, u, m, v, g;
        return a.preventDefault(), g = this, this.hide(), p = {}, this.settingVariables(), h = this.getParameters(), c = [], g = this, this.$(".formats:checked").each(function () {
            return c.push(t(this).val())
        }), 0 === c.length ? (Dashboard.warn("Please select at least one format"), !1) : (r = '<base href="' + e.location.protocol + "//" + (e.location.host + e.DashboardGlobals.baseUrl) + '">', l = null, d = this.$("#email-to").val().split(";").filter(function (e) {
            return!!e
        }), i = o.saveAs(null, null, null, !0), v = t("#email-subject").val(), s = t("#email-body").val(), u = this.scheduleLocation ? this.scheduleLocation.name : "", b = e.DashboardGlobals.file.split(".")[1], "email" !== this.attributes.get("mode") ? (m = this.model.toJSON(), m.StartDate = moment(this.model.get("StartDate"), this.model.get("dateFormat")).format("YYYY-MM-DD"), m.EndDate = moment(this.model.get("EndDate"), this.model.get("dateFormat")).format("YYYY-MM-DD"), m.ScheduledTime = moment(this.model.get("StartDate"), this.model.get("dateFormat")).format("HH:mm:ss"), m.ScheduledEndTime = moment(this.model.get("EndDate"), this.model.get("dateFormat")).format("HH:mm:ss"), m.timeZone = t("#timezone").select2("data").element[0].getAttribute("data-name"), n = {Formats: c, Recipients: JSON.stringify(d), Zip: !1}, v && "" !== v && (n.Subject = v), s && "" !== s && (n.Body = s), this.$("#outputVariables").find("input").each(function () {
            var e, a, r;
            return r = t(this).val(), e = t(this).attr("name"), t(this).attr("data-array") && (r = r.split(",")), p.hasOwnProperty(e) ? (a = p[e], t.isArray(a) || (p[e] = [a]), p[e].push(r || "")) : p[e] = r || ""
        }), f = {command: "add", reportDirectory: e.DashboardGlobals.folderpath, reportFile: e.DashboardGlobals.file, location: this.scheduleLocation.path.actual, EmailSettings: JSON.stringify(n), ScheduleOptions: JSON.stringify(m), isActive: !0}, f.reportParameters = JSON.stringify(p), f.reportName = u && "" !== u ? u : e.DashboardGlobals.currentReport.title, t.post(e.DashboardGlobals.saveReport, f)) : "url" === e.DashboardGlobals.defaultEmailResourceType ? (n = {dir: e.DashboardGlobals.folderpath, reportFile: e.DashboardGlobals.file, reportType: b, formats: JSON.stringify(c), recipients: JSON.stringify(d), reportSourceType: "url", reportParameters: h ? JSON.stringify(h) : ""}, v && "" !== v && (n.subject = v), s && "" !== s && (n.body = s), n.reportName = u && "" !== u ? u : e.DashboardGlobals.currentReport.title, t.post(e.DashboardGlobals.sendMail, n).always(function () {
            return g.hide()
        })) : (n = {formats: JSON.stringify(c), recipients: JSON.stringify(d), reportSourceType: "adhoc", reportSource: i}, v && "" !== v && (n.subject = v), s && "" !== s && (n.body = s), n.reportName = u && "" !== u ? u : e.DashboardGlobals.currentReport.title, t.post(e.DashboardGlobals.sendMail, n).always(function () {
            return g.hide()
        })), !1)
    }, allowOnlyNumbers: function (e) {
        var a;
        a = t(e.target), a.val(a.val().replace(/(^0|\D)/g, ""))
    }, getSchedule: function () {
        return this.model.toJSON()
    }, setSchedule: function (e) {
        var a, t;
        for (a in e)hasProp.call(e, a) && (t = e[a], this.model.set(a, t))
    }, setStep: function (e) {
        var a;
        return e.preventDefault(), a = t(e.target), a.is(":not([data-step])") && (a = a.closest("[data-step]")), !a.hasClass("disabled") && !a.closest("li").hasClass("disabled") && void this.attributes.set("step", parseInt(a.data("step")))
    }, changeMode: function (e, a) {
        return"email" === a ? (t("[data-visible=email]").removeClass("hidden"), t("[data-hidden=email]").addClass("hidden"), this.attributes.set("step", 1)) : (t("[data-hidden=email]").removeClass("hidden"), t("[data-visible=email]").addClass("hidden"), this.resetSteps())
    }, resetSteps: function () {
        return this.attributes.set("step", 1), this.$(".nav-tabs a").filter(":not([data-step=1])").closest("li").addClass("disabled")
    }, switchTab: function (e, a) {
        this.$(".nav-tabs a[data-step=" + a + "]").tab("show").removeClass("disabled").closest("li").removeClass("disabled")
    }, promptForSavePath: function (e) {
        var a;
        a = this, e.preventDefault(), o.filebrowser.show({title: "Schedule report", footer: '<div class="form" id="ScheduleReportTemp"> <div class="form-group has-error hidden"  id="error-folder"> <br> <span class="error-msg">Please select a location</span> </div> <br> <div class="form-group"> <label class="control-label" for="report-name">Report name</label> <input type="text" class="form-control" id="report-name" name="report-name" maxlength="60" data-validation-rules="required|filename|length(3)" data-validation-on="focusout" data-validation-msg="Please enter valid report name, minimum length 3."> </div> <div class="form-group text-right"> <button class="btn btn-primary" id="select-location" data-validate="#report-name" data-validate-on="click">Save Report</button> <button class="btn btn-danger" id="hide-file-explorer" data-dismiss="modal">Cancel</button> </div> </div>', hideFiles: !0, hideProperties: !0, hideFilters: !0, hideContext: !1, hideNonWritable: !0}), o.filebrowser.selectedPath = null, o.filebrowser.updateBreadCrumb(""), o.filebrowser.filetree.collapseAll(), o.filebrowser.$("#hdi-file-properties").html(""), o.filebrowser.$("#error-folder").addClass("hidden"), o.filebrowser.$el.off("click.hideFileExp", "#hideSaveReport").on("click.hideFileExp", "#hideSaveReport", function (e) {
            return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
        }), o.filebrowser.$el.off("click.hideFileExp", "#hide-file-explorer").on("click.hideFileExp", "#hide-file-explorer", function (e) {
            return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
        }), o.filebrowser.$el.off("click.saveReport", "#select-location").on("click.saveReport", "#select-location", function (e) {
            var r, s;
            return o.filebrowser.$("#error-folder").addClass("hidden"), (s = o.filebrowser.getSelected()) ? !!t("#report-name").validate() && (r = o.filebrowser.$("#report-name").val(), a.scheduleLocation = {path: s, name: r}, a.$("#save-report-name").val(s.logical + "/" + r), o.filebrowser.hide()) : (o.filebrowser.$("#error-folder").removeClass("hidden"), !1)
        })
    }, template: '<div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> <h4 class="modal-title toggleHeading"><b>Email Options</b></h4> </div> <div class="modal-body"> <ul class="nav nav-tabs hi-tab-highlight"> <li class="active"> <a href="#variables-review" data-step="1">Parameters review</a> </li> <li class="disabled"> <a href="#email-form" data-step="2">Email options</a> </li> <li class="disabled"> <a href="#scheduling-form" data-step="3" data-hidden="email">Scheduling options</a> </li> </ul> <div class="tab-content"> <div class="tab-pane active" id="variables-review"> <div id="variables-data"> <form class="addScroll" id="outputVariables"> </form> </div> <hr style="margin-left:-15px;margin-right:-15px;border-top:1px solid #e5e5e5"> <div class="text-center"> <a href="#" class="btn btn-primary" data-step="2"> Next&nbsp;&nbsp;<i class="fa fa-arrow-right"></i> </a> </div> </div> <div class="tab-pane" id="email-form"> <form id="clearForm" role="form"> <div id="formatValidation" class="form-group" data-validation-group="output" data-validation-rules="required" data-validation-on="focusout" data-validation-msg="Please select at least one format"> <label for="email-to">Format<span style="color:red">*</span>&nbsp;:&nbsp;</label> <label class="checkbox-inline"> <input type="checkbox" value="pdf" name="output" class="formats" checked="checked"> PDF </label> <label class="checkbox-inline"> <input type="checkbox" value="png" name="output" class="formats" checked="checked"> PNG </label> <label class="checkbox-inline"> <input type="checkbox" value="jpg" name="output" class="formats" checked="checked"> JPG </label> </div> <div class="form-group"> <label class="control-label" for="email-to">To<span style="color:red">*</span></label> <input type="text" class="form-control" id="email-to" name="email-to" data-validation-rules="required|email" data-validation-on="focusout" data-validation-msg="Please enter valid email ID!"> <span><i class="fa fa-info-circle"></i>&nbsp; Please use ; as a separator to send email to multiple recipients </span> </div> <div class="form-group"> <label class="control-label" for="email-subject">Subject<span style="color:red">*</span></label> <input type="text" class="form-control" id="email-subject" name="email-subject" data-validation-rules="required" data-validation-on="focusout" data-validation-msg="Please enter valid subject."> </div> <div class="form-group"> <label for="email-body">Body</label> <textarea class="form-control" rows="3" style="resize:vertical" id="email-body"></textarea> </div> <div class="form-group" data-hidden="email"> <label class="control-label" for="save-report-name">Report Name<span style="color:red">*</span></label> <div class="input-group"> <input type="text" class="form-control" id="save-report-name" placeholder="Provide a name to your report schedule" data-validation-rules="required" data-validation-on="focusout" data-validation-msg="Please select a valid location." readonly="readonly"> <div class="input-group-btn"> <button class="btn btn-primary" id="select-location-btn">Select</button> </div> </div> </div> <span style="color:red">* Required fields</span> <hr style="margin-left:-15px;margin-right:-15px;border-top:1px solid #e5e5e5"> <div class="text-center" data-hidden="email"> <a href="#" class="btn btn-danger" data-step="1"> <i class="fa fa-arrow-left"></i>&nbsp;&nbsp;Prev </a>&nbsp;&nbsp; <a href="#" class="btn btn-primary" id="data-step-3" data-step="3" > Next&nbsp;&nbsp;<i class="fa fa-arrow-right"></i> </a> </div> <div class="text-center hidden" data-visible="email"> <a href="#" class="btn btn-danger" data-step="1"> <i class="fa fa-arrow-left"></i>&nbsp;&nbsp;Prev </a>&nbsp;&nbsp; <button class="btn btn-primary" id="email-btn"> <i class="fa fa-send"></i>&nbsp;&nbsp;Email now </button> </div> </form> </div> <div class="tab-pane" id="scheduling-form"> <form class="form-horizontal"> <div class="form-group"> <label class="col-xs-3 control-label">Repeats :</label> <div class="col-xs-8"> <select class="form-control" name="Frequency" id="repeatOrder"> <option value="Daily">Daily</option> <option value="Weekly">Weekly</option> <option value="Monthly">Monthly</option> <option value="Yearly">Yearly</option> </select> </div> </div> <div class="form-group"> <label class="col-xs-3 control-label">Repeats every:</label> <div class="col-xs-4"> <select class="form-control" name="RepeatsEvery" id="repeatEvery"> {% _.each(_.range(1,32), function(i){ %} <option value=\'{{i}}\'>{{i}}</option> {% }) %} </select> </div> <label class="col-xs-5 control-label" style="text-align:left;margin-left:-15px"><span id="repeatOrder-text">week</span>(s)</label> </div> <div class="form-group" id="repeatOn"> <label class="col-xs-3 control-label" id="repeatText">Repeat On :</label> <div class="col-xs-9" id="weekMonthSelector" data-validation-group="DaysofWeek" data-validation-rules="required" data-validation-on="focusout" data-validation-msg="Please select a day(s)"> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Sunday" name="DaysofWeek" id="sunday"> S </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Monday" name="DaysofWeek" id="monday"> M </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Tuesday" name="DaysofWeek" id="tuesday"> T </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Wednesday" name="DaysofWeek" id="wednesday"> W </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Thursday" name="DaysofWeek" id="thursday"> T </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Friday" name="DaysofWeek" id="friday"> F </label> <label class="checkbox-inline"> <input type="checkbox" class="weeks" value="Saturday" name="DaysofWeek" id="saturday"> S </label> </div> </div> <div class="form-group hidden" id="repeatBy"> <label class="col-xs-3 control-label" id="repeatText">Repeat By :</label> <div class="col-xs-9"> <label class="dayofmonth radio-inline" id="dayofmonth"> <input type="radio" name="RepeatBy" class="monthlyCheck" checked id="dayOfMonth" value="dayOfTheMonth"> day of the month </label> <label class="dayofweek radio-inline" id="dayofweek"> <input type="radio" name="RepeatBy" class="monthlyCheck" id="dayOfWeek" value="dayOfTheWeek"> day of the week </label> </div> </div> <div class="form-group"> <label class="col-xs-3 control-label">Time Zone:</label> <div class="col-xs-8"> <select class="form-control" name="timeZone" id="timezone"> {% _.each(moment.timezones, function(zone){ %} <option data-name="{{zone.name}}" value=\'{{zone.offset}}\'>{{zone.name}}  (GMT {{zone.offset}})</option> {% }) %} </select> </div> </div> <div class="form-group"> <label class="col-sm-3 control-label" for="startsOn">Starts On :</label> <div class="col-sm-8" id="start"> <input type="text" class="form-control" name="StartDate" id="startsOn"> </div> </div> <div class="form-group"> <label class="col-sm-3 control-label">Ends :</label> <div class="col-sm-9" id="ends"> <div class="radio"> <label> <input type="radio" class="ends" name="endsRadio" id="never" value="Never" checked> Never </label> </div> </div> </div> <div class="form-group"> <div class="col-sm-2 col-sm-offset-3"> <div class="radio"> <label> <input type="radio" name="endsRadio" class="ends" id="endsAfter" value="After"> After </label> </div> </div> <div class="col-sm-3"> <input type="number" max="100" min="1" class="form-control" id="endsAfterOccurrences" disabled="disabled" value="" name="EndAfterExecutions" data-validation-rules="required|numeric|numericlength" data-validation-on="focusout" data-validation-msg="Occurences should be >= 1 and <=99"> </div> <label class="col-sm-3 control-label" style="text-align:left;margin-left:-15px">Occurences</label> </div> <div class="form-group"> <div class="col-sm-2 col-sm-offset-3"> <div class="radio"> <label> <input type="radio" class="ends" name="endsRadio" id="endsOn" value="On"> On </label> </div> </div> <div class="col-sm-5"> <input type="text" class="form-control" name="EndDate" id="endsOnDate" disabled="disabled"> </div> </div> <div class="form-group"> <label class="col-xs-3 control-label">Summary :</label> <label class="col-xs-8 control-label" style="text-align:left"><span id="schedule-summary"></span></label> </div> <hr style="margin-left:-15px;margin-right:-15px;border-top:1px solid #e5e5e5"> <div class="text-center"> <a href="#" class="btn btn-danger" data-step="2"> <i class="fa fa-arrow-left"></i>&nbsp;&nbsp;Prev </a>&nbsp;&nbsp; <button class="btn btn-primary" id="schedule-btn"> <i class="fa fa-clock-o"></i>&nbsp;&nbsp;Schedule </button> </div> </form> </div> </div> </div> </div> </div>'}), o.showExport = function () {
        o.filebrowser.fetchData().done(function () {
            return o.filebrowser.show({title: "Download Report", footer: '<div class="form" id="hideDownloadReportTemp"> <div class="form-group has-error hidden"  id="error-folder"> <br> <span class="error-msg">Please select a location</span> </div> <br> <div class="form-group"> <label class="control-label" for="download-report-name">Download Report Name</label> <input type="text" class="form-control" maxlength="60" id="download-report-name" name="download-report-name" data-validation-rules="required|filename|length(3)" data-validation-on="focusout" data-validation-msg="Please enter valid report name, minimum length 3."> </div> <div class="form-group text-right"> <button class="btn btn-primary" id="download-report" data-validate="#download-report-name" data-validate-on="click">Download Report</button> <button class="btn btn-danger" id="hide-file-explorer" data-dismiss="modal">Cancel</button> </div> </div>', hideFiles: !0, hideProperties: !0, hideFilters: !0, hideContext: !1, hideNonWritable: !0})
        }), o.filebrowser.selectedPath = null, o.filebrowser.updateBreadCrumb(""), o.filebrowser.$("#hdi-file-properties").html(""), o.filebrowser.$("#error-folder").addClass("hidden"), o.filebrowser.$el.off("click.saveAs", "hdi-file-filter", "#hideSaveReport").on("click.saveAs", "hdi-file-filter", "#hideSaveReport", function (e) {
            return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
        }), o.filebrowser.$el.off("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer").on("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer", function (e) {
            return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
        })
    }, o.simpleModel = function (e) {
        var t, r, s, o, l, i, n, d;
        s = void 0, o = void 0, d = void 0, l = void 0, i = "", t = function () {
        }, r = {}, n = void 0 !== e && e.parameters ? e.parameters : "", s = a.createElement("div"), o = JSON.stringify(n), l = "<script> var params = " + o + " </script> ", "object" == typeof e && void 0 !== e && null !== e ? (s.innerHTML = l + e.uiSnippet, i = e.name || e.title ? e.name || e.title : "Untitled Model", e.scriptId && e.parameters ? t = e.callback && e.callback instanceof Function ? e.callbackFunction : function () {
            var a, t, r, s;
            t = {}, r = [], s = void 0, s = adhocScripts.getAllVariables();
            for (a in e.parameters)s.hasOwnProperty(a) && (e.parameters[a] = s[a]);
            t.parameterValues = e.parameters, t.scriptId = e.scriptId, r.push(t), flux.visualisations.actions.updateParametersOfSelectedScripts(r)
        } : e.callback && e.callback instanceof Function && (t = e.callback), (e.showFooter && e.showFooter !== !1 || void 0 === e.showFooter) && (r = {main: {label: "Ok", className: "btn-primary", callback: t}, danger: {label: "Cancel", className: "btn-danger"}})) : "string" == typeof e ? (s.innerHTML = e, i = "Untitled Model") : void 0 === e && (s.innerHTML = "No UI for this script", i = "Untitled Model"), bootbox.dialog({title: i, message: s, buttons: r})
    }, t(a).ready(function () {
        var s, c, p, h, f, b, u, m, v, g;
        s = a.getElementById("dashboard-canvas"), e.location.pathname.indexOf("/adhoc") === -1 && (t.ajaxSetup({type: "POST"}), b = new i({id: "hdi-blockUI"}), b.$el.appendTo("body"), f = new l({id: "error-panel"}), f.$el.appendTo("body"), u = null, c = 0, t.ajaxQueue = [], t.notifyDefaults({position: "fixed", placement: {from: "bottom", align: "right"}, mouse_over: "pause", delay: 3e3, timer: 1e3}), o._requests.abortAll = function () {
            var e, a, r, s;
            for (r = t.ajaxQueue, e = 0, a = r.length; e < a; e++)s = r[e], "function" == typeof s.abort && s.abort();
            t.ajaxQueue.length > 0 && (t.ajaxQueue = []), o._reset_loading_panel(), c = 0
        }, o._requests.remove = function (e) {
            t.ajaxQueue = r.filter(t.ajaxQueue, function (a) {
                return a.identifier !== e
            })
        }, o._reset_loading_panel = function () {
            b.hide(), clearInterval(u), c = 0, t("#elpased_time").html("0:00")
        }, h = function () {
            var e, t, r, s, o;
            for (t = a.cookie.split(";"), r = {}, s = 0, o = t.length; s < o;)e = t[s].split("="), 2 === e.length ? (r[e[0].trim()] = e[1].trim(), s++) : s++;
            return r
        }, t(a).ajaxStart(function () {
            t(a).trigger(t.Event("first.request.hdi")), b.show(), u = setInterval(function () {
                var e, a;
                ++c, a = c % 60 > 9 ? (c % 60).toString() : "0" + (c % 60).toString(), e = Math.floor(c / 60).toString(), t("#elpased_time").html(e + ":" + a)
            }, 1e3)
        }).ajaxSend(function (r, s, o) {
            var l;
            l = Math.abs(e.moment ? moment.utc().format("x") : (new Date).getTime()), s.setRequestHeader("currentTime", l), t(a).trigger(t.Event("start.request.hdi")), t("#request-count").html(t.active), t.ajaxQueue.push(s)
        }).ajaxSuccess(function (s, l, i) {
            l.done(function (a) {
                var s, o, l, i, n, d, c, p, f, b, u;
                return r.isObject(a) && a.hasOwnProperty("status") && (i = "fa fa-check", u = "success", 0 === a.status && (i = "fa fa-times", u = "danger"), c = "", a.response.message && (c += a.response.message, t.notify({message: c, icon: i}, {type: u}))), o = h(), b = Math.abs(o.serverTime), s = Math.abs(e.moment ? moment.utc().format("x") : (new Date).getTime()), l = b, r.isObject(a) && a.hasOwnProperty("lastModified") ? (n = moment(parseInt(a.lastModified, 10)), d = n.format("[Last cached on:] LLLL"), t("[data-refreshNew]").off("click").hover(function () {
                    return t(this).tooltip({placement: "bottom"}).attr("data-original-title", d).tooltip("fixTitle").tooltip("show")
                })) : (p = moment(parseInt(b, 10)), f = p.format("[Last cached on:] LLLL"), t("[data-refreshNew]").off("click").hover(function () {
                    return t(this).tooltip({placement: "bottom"}).attr("data-original-title", f).tooltip("fixTitle").tooltip("show")
                })), e.__HI_cache_notify__ && clearInterval(e.__HI_cache_notify__), e.__HI_cache_notify__ = setInterval(function () {
                    var s, o, i, c, h, u, m, v, g, y, w;
                    r.isObject(a) && a.hasOwnProperty("lastModified") ? (l += 1e3, n = moment(parseInt(a.lastModified, 10)), h = l - n, i = n + h, m = moment(parseInt(i, 10)), d = n.format("[Last cached on:] LLLL"), g = n.from(m), c = d + " (" + g + ")", t("#hi-newWindow-cacheRefresh").html("<a class='hi-cache-toolbar-cacheDetails' style='color: #337ab7;'><span class='hi-cache-toolbar-screen'><i class='fa fa-hourglass-half'></i>&nbsp;<b>Last Cached: </b>" + g + "</span><span class='fa fa-hourglass-half hi-cache-toolbar-mobile'></span></a>"), e.parent.postMessage(g, "*")) : (p = moment(parseInt(b, 10)), f = p.format("[Last cached on:] LLLL"), l += 1e3, u = l - b, o = b + u, v = moment(parseInt(o, 10)), s = p.from(v), y = f + " (" + s + ")", w = s, t("#hi-newWindow-cacheRefresh").html("<a class='hi-cache-toolbar-cacheDetails' style='color: #337ab7;'><span class='hi-cache-toolbar-screen'><i class='fa fa-hourglass-half'></i>&nbsp;<b>Last Cached: </b>" + s + "</span><span class='fa fa-hourglass-half hi-cache-toolbar-mobile'></span></a>"), e.parent.postMessage(w, "*"))
                }, 1e3)
            }), t(a).trigger(t.Event("success.request.hdi")), o._requests.remove(l.identifier)
        }).ajaxComplete(function (e, r, s) {
            t(a).trigger(t.Event("complete.request.hdi")), t("#request-count").html(t.active - 1)
        }).ajaxStop(function () {
            o._reset_loading_panel(), t.ajaxQueue.length > 0 && (t.ajaxQueue = [], c = 0), t(a).trigger(t.Event("last.request.hdi")), b.hide()
        }).ajaxError(function (e, a, r, s) {
            return b.hide(), f.show(), "abort" === s ? (c = 0, o._requests.abortAll(), o._reset_loading_panel(), !1) : (o._requests.abortAll(), o._reset_loading_panel(), a.identifier ? (s = "There was a problem with " + a.identifier + ". <b>'" + s + "'</b>.", t("#error-generated").html(s)) : (s = "There was a problem : <b>'" + s + "'</b>.", t("#error-generated").html(s)), t("#request-count").html(t.active - 1), void f.show())
        })), o.info = function (e, a) {
            a || (a = "fa fa-info"), t.notify({message: e, icon: a}, {type: "info"})
        }, o.alert = function (e, a) {
            a || (a = "fa fa-info"), t.notify({message: e, icon: a}, {type: "info"})
        }, o.warn = function (e, a) {
            a || (a = "fa fa-exclamation-triangle"), t.notify({message: e, icon: a}, {type: "warn"})
        }, o.success = function (e, a) {
            a || (a = "fa fa-check"), t.notify({message: e, icon: a}, {type: "success"})
        }, e.onerror = function (e, a, t, r, s) {
            var l, i;
            return"undefined" != typeof e && (i = r ? ", column: " + r : "", i += s ? " (" + s + ")" : "", l = "Error: " + e + "<br><b>url: " + a + ", line: " + t + i + "</b>", o.error(l)), !1
        }, o.error = function (e) {
            var a;
            return a || (a = "fa fa-times"), t.notify({message: e, icon: a}, {type: "danger"})
        }, o.iframe = '<html> <head> <meta content="text/html;" charset="UTF-8"> <meta http-equiv="cache-control" content="no-cache"> <meta http-equiv="expires" content="0"> <meta http-equiv="pragma" content="no-cache"> <link rel="stylesheet" href="' + e.DashboardGlobals.baseUrl + 'css/fonts.css"> <link rel="stylesheet" href="' + e.DashboardGlobals.baseUrl + 'css/styles.css"> <link rel="stylesheet" href="' + e.DashboardGlobals.baseUrl + 'css/custom.css"> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/jquery.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/bootstrap.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/backbone.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/moment.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/d3.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/vendors/jquery-ui.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/plugins/select.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/dashboard.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/plugins/daterangepicker.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/plugins/datetimepicker.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/plugins/gridstack.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/ajax.js"></script> <script src="' + e.DashboardGlobals.baseUrl + 'js/user_utils.js"></script> <script> (function() { var topwindow = window.top; topwindow.Dashboard = Dashboard; window.Base64 = topwindow.Base64; if(!window.DashboardGlobals) { window.DashboardGlobals = topwindow.DashboardGlobals; } window.onload = function() { if(typeof InstallTrigger !== "undefined") { var iframe1 = document.getElementById("dashboard-canvas"); history.pushState(null, null); window.addEventListener("popstate", function(event) { event.preventDefault(); if(iframe1){ iframe1.src=""; } history.pushState(null, null); }, false); } else if (/*@cc_on!@*/false || !!document.documentMode) { var iframeCopy=document.getElementById("dashboard-canvas"); window.history = null; history.pushState(null, null); window.addEventListener("popstate", function(event) { if(iframeCopy){ iframeCopy.src=""; } history.pushState(null, null); }, false); } }; $(document).ajaxStart(function(){ $("#hdi-blockUI").modal("show"); }); $(document).ajaxStop(function(){ $("#hdi-blockUI").modal("hide"); }); $(document).ajaxError(function(){ $("#error-panel").modal("show"); }); })() </script> </head> <body> <div id="cache-footer"> <span class="customColor-Text hi-cache-time-textColor" id="cacheRefresh" data-refresh="refreshTime" data-place="top"> </span> <span class="hi-IE-specific" data-toggle="tooltip" data-refresh="refreshTime"></span> </div> <div class="clearfix"></div> <div class="modal" id="hdi-blockUI"> <div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <h4 class="modal-title"> <b>Updating</b> <i class="fa fa-repeat fa-spin spin pull-right"></i> </h4> </div> <div class="modal-body"> <p><b>Please wait while the request is being processed...</b></p> <p>Pending Requests: <span id="request-count">0</span></p> </div> <div class="modal-footer"> <p class="pull-left text-danger">Time elapsed: <span id="elpased_time">0:00</span></p> <button type="button" class="btn btn-danger" data-dismiss="modal" onClick="_reset_loading_panel()">Close</button> </div> </div> </div> </div> <div> <div class="modal fade" id="error-panel"> <div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <h4 class="modal-title"> <b>Error Occurred</b> </h4> </div> <div class="modal-body"> <p class="text-danger"> <span id="error-generated"></span> </p> <p>Pending Requests: <span id="request-count">0</span> has been cancelled</p> </div> <div class="modal-footer"> <input type="button" class="btn btn-primary" value="OK" data-dismiss="modal"> </div> </div> </div> </div> </div> <div id="main" class="hi-report-container">{{data}}</div> </body> </html>', t("[data-about=hdi]").off("click").on("click", function (e) {
            o.about(), e.preventDefault()
        }), t(a).on("click", "[data-saveurl]", function (a) {
            var r, s;
            return a.preventDefault(), r = t(this), !r.hasClass("disabled") && !r.closest("li").hasClass("disabled") && (!!(s = r.data("format")) && void("true" === e.DashboardGlobals.enableReportSave ? (o.filebrowser.fetchData().done(function () {
                return o.filebrowser.show({title: "Download Report", footer: '<div class="form" id="hideDownloadReportTemp"> <div class="form-group has-error hidden"  id="error-folder"> <br> <span class="error-msg">Please select a location</span> </div> <br> <div class="form-group"> <label class="control-label" for="download-report-name">Download Report Name</label> <input type="text" class="form-control" maxlength="60" id="download-report-name" name="download-report-name" data-validation-rules="required|filename|length(3)" data-validation-on="focusout" data-validation-msg="Please enter valid report name, minimum length 3."> </div> <div class="form-group text-right"> <button class="btn btn-primary" id="download-report" data-validate="#download-report-name" data-validate-on="click">Download Report</button> <button class="btn btn-danger" id="hide-file-explorer" data-dismiss="modal">Cancel</button> </div> </div>', hideFiles: !0, hideProperties: !0, hideFilters: !0, hideContext: !1, hideNonWritable: !0})
            }), o.filebrowser.selectedPath = null, o.filebrowser.updateBreadCrumb(""), o.filebrowser.$("#hdi-file-properties").html(""), o.filebrowser.$("#error-folder").addClass("hidden"), o.filebrowser.$el.off("click.saveAs", "hdi-file-filter", "#hideSaveReport").on("click.saveAs", "hdi-file-filter", "#hideSaveReport", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), o.filebrowser.$el.off("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer").on("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), o.filebrowser.$el.off("click.saveAs", "#download-report").on("click.saveAs", "#download-report", function (e) {
                var a, r;
                return o.filebrowser.$("#error-folder").addClass("hidden"), (r = o.filebrowser.getSelected()) ? !!t("#download-report-name").validate() && (a = o.filebrowser.$("#download-report-name").val(), o.filebrowser.hide(), o.downloadURL(s, a, r.actual), o.filebrowser.fetchData()) : (o.filebrowser.$("#error-folder").removeClass("hidden"), !1)
            })) : o.downloadURL(s)))
        }), t("body").on("report.opened.hdi", function () {
            t(".disabled[data-saveurl]").removeClass("disabled"), t(".disabled > [data-saveurl]").closest(".disabled").removeClass("disabled")
        }), e.HDIFileBrowser && (p = new HDIFileBrowser({id: "hdi-filebrowser", skipFetch: !0}), p.$el.appendTo("body"), o.filebrowser = p), e.location.pathname.indexOf("/adhoc") === -1 && t("[data-filebrowser=filebrowser]").click(function (e) {
            var a, t;
            a = void 0, e.preventDefault();
            try {
                p.fetchData().done(function () {
                    return p.show()
                })
            } catch (r) {
                return t = r, a = t, console.warn("Event was cancelled.")
            }
        }), t("[data-refresh]").on("click", function (e) {
            var r, s, l, i, n, d;
            if (s = t(this), s.hasClass("disabled") || s.closest("li").hasClass("disabled"))return!1;
            i = {}, r = a.getElementById("dashboard-canvas").contentWindow, r.Dashboard.getAllVariables(), d = r.Dashboard.getAllVariables() ? r.Dashboard.getAllVariables() : Dashboard.getAllVariables();
            for (l in d)hasProp.call(d, l) && (n = d[l], "" === n || "object" == typeof n && 0 === n.length || (i[l] = n));
            return i = i ? JSON.stringify(i) : "", o.filebrowser.reLoader(i)
        }), t("[data-refreshCurrent]").on("click", function (e) {
            var a;
            return a = t(this), !a.hasClass("disabled") && !a.closest("li").hasClass("disabled") && o.filebrowser.reLoadCurrent()
        }), t("body").on("report.opened.hdi", function () {
            t(".disabled[data-refresh]").removeClass("disabled"), t(".disabled > [data-refresh]").closest(".disabled").removeClass("disabled")
        }), t("body").on("report.opened.hdi", function () {
            t(".disabled[data-refreshCurrent]").removeClass("disabled"), t(".disabled > [data-refreshCurrent]").closest(".disabled").removeClass("disabled")
        }), t("[data-save]").on("click", function (a) {
            var r, s;
            return a.preventDefault(), r = t(this), !r.hasClass("disabled") && !r.closest("li").hasClass("disabled") && (!!(s = r.data("format")) && void("true" === e.DashboardGlobals.enableReportSave ? (o.filebrowser.fetchData().done(function () {
                return o.filebrowser.show({title: "Download Report", footer: '<div class="form" id="hideDownloadReportTemp"> <div class="form-group has-error hidden"  id="error-folder"> <br> <span class="error-msg">Please select a location</span> </div> <br> <div class="form-group"> <label class="control-label" for="download-report-name">Download Report Name</label> <input type="text" class="form-control" maxlength="60" id="download-report-name" name="download-report-name" data-validation-rules="required|filename|length(3)" data-validation-on="focusout" data-validation-msg="Please enter valid report name, minimum length 3."> </div> <div class="form-group text-right"> <button class="btn btn-primary" id="download-report" data-validate="#download-report-name" data-validate-on="click">Download Report</button> <button class="btn btn-danger" id="hide-file-explorer" data-dismiss="modal">Cancel</button> </div> </div>', hideFiles: !0, hideProperties: !0, hideFilters: !0, hideContext: !1, hideNonWritable: !0})
            }), o.filebrowser.selectedPath = null, o.filebrowser.updateBreadCrumb(""), o.filebrowser.filetree.collapseAll(), o.filebrowser.$("#hdi-file-properties").html(""), o.filebrowser.$("#error-folder").addClass("hidden"), o.filebrowser.$el.off("click.saveAs", "hdi-file-filter", "#hideSaveReport").on("click.saveAs", "hdi-file-filter", "#hideSaveReport", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), o.filebrowser.$el.off("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer").on("click.hideFileExp", "hdi-file-filter", "#hide-file-explorer", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), o.filebrowser.$el.off("click.saveAs", "#download-report").on("click.saveAs", "#download-report", function (e) {
                var a, r;
                return o.filebrowser.$("#error-folder").addClass("hidden"), (r = o.filebrowser.getSelected()) ? !!t("#download-report-name").validate() && (a = o.filebrowser.$("#download-report-name").val(), o.filebrowser.hide(), o.saveAs(s, a, r.actual), o.filebrowser.fetchData()) : (o.filebrowser.$("#error-folder").removeClass("hidden"), !1)
            })) : o.saveAs(s)))
        }), t("body").on("report.opened.hdi", function () {
            t(".disabled[data-save]").removeClass("disabled"), t(".disabled > [data-save]").closest(".disabled").removeClass("disabled")
        }), t("[data-saveReport]").on("click", function (e) {
            var a;
            return e.preventDefault(), a = t(this), !a.hasClass("disabled") && !a.closest("li").hasClass("disabled") && (o.filebrowser.fetchData().done(function () {
                return o.filebrowser.show({title: "Save report", footer: '<div class="form" id="hideSaveReportTemp"> <div class="form-group has-error hidden"  id="error-folder"> <br> <span class="error-msg">Please select a location</span> </div> <br> <div class="form-group"> <label class="control-label" for="save-report-name">Report name</label> <input type="text" class="form-control" maxlength="60" id="save-report-name" name="save-report-name" data-validation-rules="required|filename|length(3)" data-validation-on="focusout" data-validation-msg="Please enter valid report name, minimum length 3."> </div> <div class="form-group text-right"> <button class="btn btn-primary" id="save-report" data-validate="#save-report-name" data-validate-on="click">Save Report</button> <button class="btn btn-danger" id="hide-file-explorer" data-dismiss="modal">Cancel</button> </div> </div>', hideFiles: !0, hideProperties: !0, hideFilters: !0, hideContext: !1, hideNonWritable: !0})
            }), o.filebrowser.selectedPath = null, o.filebrowser.updateBreadCrumb(""), o.filebrowser.filetree.collapseAll(), o.filebrowser.$("#hdi-file-properties").html(""), o.filebrowser.$("#error-folder").addClass("hidden"), o.filebrowser.$el.off("click.saveReport", "#filter", "#hideSaveReport").on("click.saveReport", "#filter", "#hideSaveReport", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), o.filebrowser.$el.off("click.hideFileExp", "#filter", "#hide-file-explorer").on("click.hideFileExp", "#filter", "#hide-file-explorer", function (e) {
                return o.filebrowser.hide(), o.filebrowser.filetree.collapseAll(), o.filebrowser.clearFileBrowser()
            }), void o.filebrowser.$el.off("click.saveReport", "#save-report").on("click.saveReport", "#save-report", function (e) {
                var a, r;
                return o.filebrowser.$("#error-folder").addClass("hidden"), (r = o.filebrowser.getSelected()) ? !!t("#save-report-name").validate() && (a = o.filebrowser.$("#save-report-name").val(), o.filebrowser.hide(), o.saveReport(a, r.actual), o.filebrowser.fetchData()) : (o.filebrowser.$("#error-folder").removeClass("hidden"), !1)
            }))
        }), t("body").on("report.opened.hdi", function () {
            "efw" === e.DashboardGlobals.extension ? (t(".disabled[data-saveReport]").removeClass("disabled"), t(".disabled > [data-saveReport]").closest(".disabled").removeClass("disabled")) : (t("[data-saveReport]").addClass("disabled"), t("[data-saveReport]").closest("li").addClass("disabled"))
        }), v = new n({dateFormat: "DD/MM/YYYY hh:mm A"}), g = new d({id: "#hdi-scheduling", model: v}), g.$el.appendTo("body"), o.scheduleWindow = g, t("[data-schedule]").click(function (e) {
            var a;
            return a = t(this), !a.hasClass("disabled") && !a.closest("li").hasClass("disabled") && (e.preventDefault(), g.show(t(this).data("schedule")), o.scheduleWindow.$("#email-btn").off("click").on("click", function () {
                if (!t("#formatValidation,#email-to,#email-subject").validate())return!1
            }), o.scheduleWindow.$("#data-step-3").off("click").on("click", function () {
                if (!t("#formatValidation,#email-to,#email-subject,#save-report-name").validate())return!1
            }), void o.scheduleWindow.$("#schedule-btn").off("click").on("click", function () {
                if (!t("#weekMonthSelector,#endsAfterOccurrences").validate())return!1
            }))
        }), t("body").on("report.opened.hdi", function () {
            "efwsr" === e.DashboardGlobals.extension ? (t(".disabled[data-schedule=email]").removeClass("disabled"), t(".disabled > [data-schedule=email]").closest(".disabled").removeClass("disabled"), t("[data-schedule=report]").addClass("disabled"), t("[data-schedule=report]").closest("li").addClass("disabled")) : "report" === e.DashboardGlobals.extension ? (t(".disabled[data-schedule=email]").removeClass("disabled"), t(".disabled > [data-schedule=email]").closest(".disabled").removeClass("disabled"), t("[data-schedule=report]").addClass("disabled"), t("[data-schedule=report]").closest("li").addClass("disabled"), t(".disabled[data-schedule]").removeClass("disabled"), t(".disabled > [data-schedule]").closest(".disabled").removeClass("disabled")) : (t(".disabled[data-schedule]").removeClass("disabled"), t(".disabled > [data-schedule]").closest(".disabled").removeClass("disabled"))
        }), m = function () {
            var a, t, r, o;
            r = e.innerWidth, a = e.innerHeight, t = Math.round(1 * r) / 100, o = Math.round(2 * a) / 100, s && (s.style.marginLeft = "2px", s.style.marginRight = "2px", s.style.marginTop = "2px", s.style.marginBottom = "2px", s.style.height = .99 * (a - s.offsetTop) - 20 + "px", s.style.width = r - 4 + "px")
        }, e.onresize = m, t("#license-notify").on("closed.bs.alert", function () {
            m()
        }), m(), t("[data-tooltip]").tooltip({title: function () {
            return t(this).attr("data-tooltip")
        }, container: "body", trigger: "hover", html: !0, placement: "right"}), e.location.pathname.indexOf("hi/adhoc") === -1 && (t(a).on("show.bs.modal", function (e) {
            var a;
            return a = 1040 + 10 * t(".modal:visible").length, t(e.target).css("z-index", a), setTimeout(function () {
                t(".modal-backdrop:not(.modal-stack)").css("z-index", a - 1).addClass("modal-stack")
            }, 0)
        }), t(a).on("hidden.bs.modal", function () {
            var e;
            return e = t(".modal:visible"), e.length < 2 ? (e.each(function () {
                var e, a;
                if (e = t(this), a = e.zIndex(), a > 1040)return e.zIndex(a - 10)
            }), t(".modal-backdrop.modal-stack").each(function () {
                var e, a;
                if (e = t(this), a = e.zIndex(), a > 1040)return e.zIndex(a - 10)
            })) : e.length >= 2 ? (e.each(function () {
                var e, a;
                if (e = t(this), a = e.zIndex())return e.zIndex(a - 10)
            }), t(".modal-backdrop.modal-stack").each(function () {
                var e, a;
                if (t(".modal-backdrop:not(modal-stack)") && t(".modal-backdrop").addClass("modal-stack"), e = t(this), a = e.zIndex())return e.zIndex(a - 10)
            }), t(a.body).addClass("modal-open")) : (t(".modal-backdrop").remove(), t(a.body).removeClass("modal-open"))
        }))
    }), e.HDIUI = o
}(this, document, jQuery || $HDI, _ || _HDI, Backbone || BackboneHDI);
//# sourceMappingURL=hdiui.js.map
