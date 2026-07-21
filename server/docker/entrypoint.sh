#!/bin/bash
set -e

INSTALLATION_LOCATION="${INSTALLATION_LOCATION:-/usr/local/Helical Insight}"
HOST_IP="${HOST_IP:-localhost}"
CATALINA_HOME="${CATALINA_HOME:-/usr/local/tomcat}"

SETTING_XML="${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/setting.xml"
GLOBAL_CONN="${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/globalConnections.xml"

mkdir -p "${INSTALLATION_LOCATION}/hi/db"

if [ -f "$SETTING_XML" ]; then
  sed -i "s|<efwSolution>.*</efwSolution>|<efwSolution>${INSTALLATION_LOCATION}/hi/hi-repository</efwSolution>|" "$SETTING_XML"
  sed -i "s|<BaseUrl>.*</BaseUrl>|<BaseUrl>http://${HOST_IP}:8080/hi-ee/hi.html</BaseUrl>|" "$SETTING_XML"
  sed -i "s|<defaultBaseurl>.*</defaultBaseurl>|<defaultBaseurl>false</defaultBaseurl>|" "$SETTING_XML"
fi

if [ -f "$GLOBAL_CONN" ]; then
  sed -i "s|<url>.*SampleTravelData</url>|<url>jdbc:derby:${INSTALLATION_LOCATION}/hi/db/SampleTravelData</url>|" "$GLOBAL_CONN"
fi

if [ "$INSTALL_CHROME" = "true" ]; then
  CHROME_VERSION="${CHROME_VERSION:-128.0.6613.119}"
  REPORTS_DIR="${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports"
  if [ ! -f "${REPORTS_DIR}/linux_chromedriver" ]; then
    curl -kSL "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}/linux64/chrome-linux64.zip" -o /tmp/chrome-linux64.zip
    curl -kSL "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}/linux64/chromedriver-linux64.zip" -o /tmp/chromedriver-linux64.zip
    unzip -q -o /tmp/chrome-linux64.zip -d "${REPORTS_DIR}/"
    mv "${REPORTS_DIR}/chrome-linux64" "${REPORTS_DIR}/chrome"
    unzip -q -o /tmp/chromedriver-linux64.zip -d /tmp/
    mv /tmp/chromedriver-linux64/chromedriver "${REPORTS_DIR}/linux_chromedriver"
    chmod +x "${REPORTS_DIR}/linux_chromedriver"
  fi
fi

exec "${CATALINA_HOME}/bin/catalina.sh" run
