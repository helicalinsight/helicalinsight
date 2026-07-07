const HILicenseExpiredPage = () => (
  <div className="license-expired-page">
    <p className="error-message">Oops!</p>
    <p>
      <img src="images/hi-license-expired/pencil.png" />
    </p>
    <p className="error-message">An error has occurred. Please see your system administrator</p>
    <a
      href="https://www.helicalinsight.com/contact-us/"
      target="_blank"
      className="renew-now-button"
    >
      RENEW LICENSE
    </a>
    <p className="license-error">
      Your license file consists of invalid data or is expired. Please contact your system
      administrator
    </p>
  </div>
);

export { HILicenseExpiredPage };
