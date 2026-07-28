import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
const React = require('react');
const crypto = require('crypto');

beforeEach(() => {
    delete window.matchMedia;
    Object.defineProperty(window, 'matchMedia', {
        writable: true,
        configurable: true,
        value: jest.fn().mockImplementation((query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(),
            removeListener: jest.fn(),
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        })),
    });

    Object.defineProperty(window, 'console', {
        writable: true,
        configurable: true,
        value: {
            error: jest.fn(),
            log: jest.fn(),
            warn: jest.fn(),
            info: jest.fn(),
            debug: jest.fn(),
        },
    })

    window.HTMLElement.prototype.scrollBy = jest.fn();
    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    Object.defineProperty(window.URL, 'createObjectURL', {
        writable: true,
        value: jest.fn(),
    });
    Object.defineProperty(window.URL, 'revokeObjectURL', {
        writable: true,
        value: jest.fn(),
    });

    window.crypto = crypto || {};
    window.crypto.getRandomValues = (arr) => require('crypto').randomBytes(arr.length);
})


afterEach(() => {
    cleanup();
});

if (typeof window.HTMLElement !== 'undefined') {
    window.HTMLElement.prototype.scrollBy = jest.fn();
    window.HTMLElement.prototype.scrollIntoView = jest.fn();
}

Object.defineProperty(window, 'Worker', {
    writable: true,
    configurable: true,
    value: class Worker {
        constructor(stringUrl) {
            this.url = stringUrl;
            this.onmessage = () => { };
        }

        postMessage(msg) {
            this.onmessage(msg);
        }
    },
});

if (typeof global.setImmediate === 'undefined') {
    global.setImmediate = (fn, ...args) => setTimeout(fn, 0, ...args);
    global.clearImmediate = (id) => clearTimeout(id);
}