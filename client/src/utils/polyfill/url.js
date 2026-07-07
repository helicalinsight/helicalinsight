
if(!URL.createObjectURL){
    Object.defineProperty(URL, 'createObjectURL', {
        writable: true,
        value: function(){

        }
    })
}
if(!window.Worker){
    Object.defineProperty(window, 'Worker', {
        writable: true,
        value: class Worker {
            constructor(stringUrl) {
                this.url = stringUrl;
                this.onmessage = () => { };
            }
    
            postMessage(msg) {
                this.onmessage(msg);
            }
        }
    })
}

