"use strict";var $=window.$;$(document).ready(function(){$("#hi-loginForm").on("submit",function(n){return $("#hi-login-userName,#hi-login-passWord,#hi-login-org").validate()?void 0:(n.preventDefault(),!1)}),history.pushState(null,null),window.addEventListener("popstate",function(n){history.pushState(null,null),n.preventDefault()},!1)});
//# sourceMappingURL=loginValidation.js.map
