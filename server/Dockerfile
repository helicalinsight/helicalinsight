FROM eclipse-temurin:25-jdk AS tomcat

ARG TOMCAT_VERSION=11.0.22

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
        -o /tmp/tomcat.tar.gz \
    && mkdir /opt/tomcat11 \
    && tar -xzf /tmp/tomcat.tar.gz \
        -C /opt/tomcat11 \
        --strip-components=1 \
    && rm -rf /tmp/tomcat.tar.gz /var/lib/apt/lists/*


FROM eclipse-temurin:25-jdk AS builder

WORKDIR /build

RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt \
	apt-get -qq update && apt-get install -y --no-install-recommends curl maven\
    && rm -rf /var/lib/apt/lists/*

COPY . .

ARG MAVEN_OPTS="-Xmx2g"
RUN --mount=type=cache,target=/root/.m2  mvn clean package -DskipTests -Denv=docker -Dedition=hice -q 



COPY --from=tomcat /opt/tomcat11 /opt/tomcat11

RUN cp /build/presentation/target/hi-ee-*.war /opt/tomcat11/webapps/hi-ee.war

RUN mkdir -p extracted && cd extracted \
    && jar -xf /opt/tomcat11/webapps/hi-ee.war \
    && jdeps --ignore-missing-deps -q --recursive --multi-release 25 \
        --print-module-deps --class-path "WEB-INF/lib/*" WEB-INF/classes > deps.info \
    && jlink --add-modules $(cat deps.info),jdk.management,jdk.crypto.ec \
        --strip-debug --compress zip-9 --no-header-files --no-man-pages \
        --output /myjre

FROM debian:bookworm-slim AS runtime

ENV INSTALLATION_LOCATION="/usr/local/Helical Insight" \
    INSTALL_CHROME=false \
    HOST_IP=localhost \
    CATALINA_HOME=/usr/local/tomcat

RUN mkdir -p "${INSTALLATION_LOCATION}/hi" \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl unzip \
        libglib2.0-0 libnss3 libdbus-1-3 libatk1.0-0 libatk-bridge2.0-0 libcups2 \
        libdrm2 libxkbcommon0 libxcomposite1 libxdamage1 libxfixes3 libxrandr2 \
        libgbm1 libpango-1.0-0 libcairo2 libasound2 \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-custom
ENV PATH=${JAVA_HOME}/bin:${PATH}

COPY --from=builder /myjre ${JAVA_HOME}
COPY --from=builder /opt/tomcat11 ${CATALINA_HOME}
COPY docker/entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh \
    && rm -rf ${CATALINA_HOME}/webapps/examples \
        ${CATALINA_HOME}/webapps/docs \
        ${CATALINA_HOME}/webapps/manager \
        ${CATALINA_HOME}/webapps/host-manager \
        ${CATALINA_HOME}/logs/* \
        ${CATALINA_HOME}/temp/* \
        ${CATALINA_HOME}/work/*

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=5 \
  CMD curl -f http://localhost:8080/hi-ee/applicationSettings || exit 1

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
