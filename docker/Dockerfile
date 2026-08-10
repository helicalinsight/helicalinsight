FROM eclipse-temurin:25-jdk-noble  AS builder

RUN apt-get -qq update && apt-get install -y curl tar unzip

ARG TOMCAT_VERSION=11.0.24

#Put the updated war file for build purpose only
COPY hi/hi-ee.war /tmp/hi-ee.war

RUN curl -SL "https://archive.apache.org/dist/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" -o /tmp/tomcat11.tar.gz \
    && mkdir -p /opt/tomcat11 \
    && tar -xzf /tmp/tomcat11.tar.gz -C /opt/tomcat11 --strip-components=1 \
    && rm /tmp/tomcat11.tar.gz 
	
RUN mkdir -p extracted && \
	cd extracted && \
    jar -xf  /tmp/hi-ee.war && \
    jdeps --ignore-missing-deps -q \
        --recursive \
        --multi-release 25 \
        --print-module-deps \
        --class-path "WEB-INF/lib/*" \
        WEB-INF/classes > deps.info && \
    cat deps.info && \
    jlink \
        --add-modules $(cat deps.info),jdk.management \
        --strip-debug \
        --compress zip-9 \
        --no-header-files \
        --no-man-pages \
        --output /myjre && rm /tmp/hi-ee.war

FROM debian:bookworm-slim AS runtime

ENV  INSTALLATION_LOCATION="/usr/local/Helical Insight" \
     INSTALL_CHROME=true \
     HOST_IP="localhost" \
     TOMCAT_LOCATION="/usr/local/tomcat" 

RUN mkdir -p "${INSTALLATION_LOCATION}"

ENV JAVA_HOME=/usr/lib/jvm/java-25-openjdk 
ENV PATH=${JAVA_HOME}/bin:${PATH} 
ENV CATALINA_HOME=${TOMCAT_LOCATION}

COPY --from=builder /myjre $JAVA_HOME
COPY --from=builder /opt/tomcat11 ${TOMCAT_LOCATION}

RUN apt-get update && apt-get install -y --no-install-recommends curl unzip libglib2.0-0 libnss3 libdbus-1-3 libatk1.0-0 libatk-bridge2.0-0 libcups2 libdrm-dev libxkbcommon-x11-0 libxcomposite1  libxdamage1 libxfixes3 libxrandr2 libgbm1 libpango-1.0-0 libcairo2 libasound2 \
    && apt-get clean -y  && rm -rf /var/lib/apt/lists/*

# Tomcat cleanup
RUN rm -rf /usr/local/tomcat/logs/* /usr/local/tomcat/temp/* /usr/local/tomcat/work/* \
    /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/docs \
    /usr/local/tomcat/webapps/manager /usr/local/tomcat/webapps/host-manager


HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

ENTRYPOINT ["/bin/bash","./entrypoint.sh"]
EXPOSE 8080
CMD ["/bin/bash", "-c", "$CATALINA_HOME/bin/catalina.sh run"]
