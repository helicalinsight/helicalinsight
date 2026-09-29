window.renderResult = function renderResult() {
      var INITIAL_WAIT = 5000;  
      var MAX_WAIT = 10000; 
      var INTERVAL = 100;

      return new Promise(function (resolve) {
        setTimeout(function () {
          var start = Date.now();

          function check() {

            var containerEl = document.getElementById(instanceData.id);
            var canvasList = containerEl ? containerEl.getElementsByTagName('canvas') : [];
            var canvas = canvasList.length > 0 ? canvasList[0] : null;

            console.log('check canvas', canvas, canvas ? canvas.width : 'no canvas');

            if (canvas && canvas.clientWidth > 0 && canvas.clientHeight > 0) {
              console.log('canvas ready');
              try {
                var base64CanvasData =   canvas.toDataURL('image/png');
                resolve(base64CanvasData);
              } catch (e) {
                console.error('Error processing canvas data:', e);
                resolve();
              }

            } else if (Date.now() - start < MAX_WAIT) {
              setTimeout(check, INTERVAL);
            } else {
              console.warn('Canvas not ready within timeout. Exiting.');
              resolve();
            }
          }

          check();
        }, INITIAL_WAIT);
      });
    };