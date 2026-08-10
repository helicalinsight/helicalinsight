#!/bin/bash
set -e


downloadChrome() {
    if [ ! -d "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/chrome" ]; then 
        unzip  -q -o /tmp/chrome-linux64.zip -d "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/"
        mv "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/chrome-linux64" "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/chrome"
        unzip  -q -o /tmp/chromedriver-linux64.zip -d "/tmp/"
        echo "Moving chrome driver to ${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports"
        mv /tmp/chromedriver-linux64/chromedriver "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/linux_chromedriver"
        chmod +x "${INSTALLATION_LOCATION}/hi/hi-repository/System/Reports/linux_chromedriver"
    fi
}

echo "Preparing Environment ..."

INSTALLATION_LOCATION="/usr/local/Helical Insight"
TOMCAT_LOCATION="/usr/local/tomcat"
LOCKFILE="/usr/local/Helical Insight/hi/hi-repository/hi.lock"
CHROME_VERSION="128.0.6613.119"
CHROME_DRIVER_VERSION="128.0.6613.119"


if [ -f "${LOCKFILE}" ]; then
  echo "Lock file exists. Exiting."
else
  touch "$LOCKFILE"
  echo "Lock file created. Continuing..."

if [ -f "/tmp/chrome-linux64.zip" ]; then  
echo "Already downloaded drivers"
else 
  curl -kSL "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_VERSION}/linux64/chrome-linux64.zip" -o /tmp/chrome-linux64.zip
  curl -kSL "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROME_DRIVER_VERSION}/linux64/chromedriver-linux64.zip" -o /tmp/chromedriver-linux64.zip
fi


unzip -o /usr/local/tomcat/webapps/hi-ee.war -d usr/local/tomcat/webapps/hi-ee

sed -i "s|<efwSolution>.*</efwSolution>|<efwSolution>${INSTALLATION_LOCATION}/hi/hi-repository</efwSolution>|; \
            s|<BaseUrl>.*</BaseUrl>|<BaseUrl>https://${HOST_IP}/hi-ee/hi.html</BaseUrl>|; \
            s|<defaultBaseurl>true</defaultBaseurl>|<defaultBaseurl>false</defaultBaseurl>|; \
            " "${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/setting.xml"

sed -i "s|<url>jdbc:derby:\${INSTALL_PATH}\${FILE_SEPARATOR}db\${FILE_SEPARATOR}SampleTravelData</url>|<url>jdbc:derby:${INSTALLATION_LOCATION}/hi/db/SampleTravelData</url>|;" "${INSTALLATION_LOCATION}/hi/hi-repository/System/Admin/globalConnections.xml"



if [ "$INSTALL_CHROME" = "true" ]; then
    downloadChrome
else
    echo "Skipping chrome installation"
fi 

echo "Environment setup completed. Starting application server."
fi

exec "$@"
