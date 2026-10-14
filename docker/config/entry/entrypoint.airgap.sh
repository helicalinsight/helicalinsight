#!/bin/bash
# Airgap entrypoint — uses fixed /tmp/chrome.zip + /tmp/chromedriver.zip
# (baked by Dockerfile.airgap). Not used by the slim image.
set -e

# Map host/container arch to Chrome for Testing platform id (download URL only)
chromePlatform() {
  case "$(uname -m)" in
    x86_64|amd64) echo "linux64" ;;
    aarch64|arm64) echo "linux-arm64" ;;
    *)
      return 1
      ;;
  esac
}

# App expects System/Reports/chrome/chrome and System/Reports/linux_chromedriver
downloadChrome() {
    local reports="${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports"

    if [ ! -f /tmp/chrome.zip ] || [ ! -f /tmp/chromedriver.zip ]; then
        echo "WARNING: Chrome zips missing under /tmp; skipping Chrome install (PDF/export unavailable)."
        return 0
    fi

    if [ ! -d "${reports}/chrome" ]; then
        unzip -q -o /tmp/chrome.zip -d "${reports}/"
        mv "${reports}"/chrome-linux* "${reports}/chrome"
        unzip -q -o /tmp/chromedriver.zip -d /tmp/
        echo "Moving chrome driver to ${reports}"
        mv /tmp/chromedriver-linux*/chromedriver "${reports}/linux_chromedriver"
        chmod +x "${reports}/chrome/chrome" "${reports}/linux_chromedriver"
    fi
}

echo "Preparing Environment (airgap) ..."

INSTALLATION_LOCATION="/usr/local/Helical Insight"
TOMCAT_LOCATION="/usr/local/tomcat"
LOCKFILE="/usr/local/Helical Insight/hi/hi-repository/hi.lock"
CHROME_VERSION="${CHROME_VERSION:-154.0.8037.92}"
SETTING_XML="${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/setting.xml"
EFFECTIVE_BASE_URL="http://${HOST_IP:-localhost:8080}/hi-ee/hi.html"

echo "BaseUrl: ${EFFECTIVE_BASE_URL}"
sed -i "s|<BaseUrl>.*</BaseUrl>|<BaseUrl>${EFFECTIVE_BASE_URL}</BaseUrl>|" "${SETTING_XML}"

if [ -f "${LOCKFILE}" ]; then
  echo "Lock file exists. Skipping one-time setup."
else
  touch "$LOCKFILE"
  echo "Lock file created. Continuing..."

if [ -f /tmp/chrome.zip ]; then
  echo "Chrome zips already present (airgap bake)"
else
  if CHROME_PLATFORM="$(chromePlatform)"; then
    echo "Chrome zips not present. Pulling from Google storage (online) for arch $(uname -m)"
    CHROME_BASE="https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/${CHROME_PLATFORM}"
    curl -fSL "${CHROME_BASE}/chrome-${CHROME_PLATFORM}.zip" -o /tmp/chrome.zip
    curl -fSL "${CHROME_BASE}/chromedriver-${CHROME_PLATFORM}.zip" -o /tmp/chromedriver.zip
  else
    echo "WARNING: Chrome for Testing has no build for arch $(uname -m); skipping Chrome download."
  fi
fi

unzip -o /usr/local/tomcat/webapps/hi-ee.war -d /usr/local/tomcat/webapps/hi-ee

sed -i "s|<efwSolution>.*</efwSolution>|<efwSolution>${INSTALLATION_LOCATION}/hi/hi-repository</efwSolution>|; \
            s|<defaultBaseurl>false</defaultBaseurl>|<defaultBaseurl>true</defaultBaseurl>|; \
            " "${SETTING_XML}"

sed -i "s|<url>jdbc:derby:\${INSTALL_PATH}\${FILE_SEPARATOR}db\${FILE_SEPARATOR}SampleTravelData</url>|<url>jdbc:derby:${INSTALLATION_LOCATION}/hi/db/SampleTravelData</url>|;" "${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/globalConnections.xml"

if [ "$INSTALL_CHROME" = "true" ]; then
    downloadChrome
else
    echo "Skipping chrome installation"
fi

echo "Environment setup completed. Starting application server."
fi

exec "$@"
