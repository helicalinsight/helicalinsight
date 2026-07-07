/** Global variables **/
var fileSystem = require('fs'),
    server = require('webserver').create();

var system = require('system');


function validateRequest(request) {
    if(request.method && request.method.toLowerCase() != "post"){
        throw "Only Post Method is supported";
    }

}


/**
 *Accept only string
 *Phantom does not understand polymorphism. Avoid using it.
 **/
function _writeToFile(content) {
    var date = new Date();
    content =  date + "|"+content+"\n";
    console.log(content);
    var logFileName = date.toDateString().split(' ').join('_');
    var file = logLocation + "/Export_log_" + logFileName + ".log";
    fileSystem.write(file, content, 'a+');
}



/* Accept only string */
function writeToResponse(response, content, statusCode) {
    _writeToFile(JSON.stringify(content));
    response.statusCode = statusCode;
    response.headers = {
        'Cache': 'no-cache',
        'Content-Type': 'text/plain;charset=utf-8'
    };

    response.write(content);
    response.close();
}




var port = system.args[1];
var exportJs = system.args[2];
phantom.injectJs(exportJs);
var logLocation = system.args[3];

var service = server.listen(port, function (request, response) {
    try{
        validateRequest(request);
        doRun(request, response);
    } catch (e) {
        writeToResponse(response, e, 400);
    }


});



if (service !== true) {
    console.log('Export Service start failed');
    phantom.exit(1);
}

