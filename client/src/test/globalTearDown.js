function GlobalTeardown () {
    global.gc && global.gc();
  }
  export default GlobalTeardown;