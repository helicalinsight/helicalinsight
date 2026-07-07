Object.defineProperty(URL, 'createObjectURL', {
    writable: true,
    value: jest.fn()
})
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

