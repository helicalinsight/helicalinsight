# Helical Insight Docker image build

This document describes how `docker/Dockerfile` builds the Helical Insight runtime image (`hiee/helicalinsight:nitrogen-j25t11`). It is the image Compose uses for the `hiee` service. The same Dockerfile is copied into the Docker ZIP by `cicd/jenkins/HI7DockerGenerator/pipeline.groovy`.

## Prerequisites

- Docker
- `hi/hi-ee.war` in the build context (same folder as the Dockerfile)

From a git checkout, after Maven has produced the WAR:

```bash
cp server/presentation/target/hi-ee.war docker/hi/hi-ee.war
cd docker
docker build -t hiee/helicalinsight:nitrogen-j25t11 .
```

From an unzipped Docker package, run `docker build` in the folder that contains `Dockerfile` and `hi/hi-ee.war`.

## What the Dockerfile does

Two stages.

**1. Builder** (`eclipse-temurin:25-jdk-noble`)

- Downloads Apache Tomcat **11.0.25** into `/opt/tomcat11`.
- Copies `hi/hi-ee.war` and unpacks it.
- Runs `jdeps` on `WEB-INF/classes` + `WEB-INF/lib` to list the Java modules the application actually uses (`deps.info`).
- Runs `jlink` to produce a stripped custom JRE at `/myjre` (debug symbols, man pages, and headers removed; zip-9 compression).

**2. Runtime** (`debian:bookworm-slim`)

- Copies the custom JRE to `JAVA_HOME` and Tomcat to `/usr/local/tomcat`.
- Installs libraries needed for Chrome-based PDF/export.
- Removes Tomcat sample apps (`examples`, `docs`, `manager`, `host-manager`).
- Health-checks `http://localhost:8080/` and starts via `entrypoint.sh` / `catalina.sh run`.

The WAR itself is not baked into the runtime image. Compose bind-mounts `hi/hi-ee.war` (and `hi-repository`) at run time.

## Custom JRE modules (CH-9370)

`jlink` is invoked as:

```text
--add-modules $(cat deps.info),jdk.management,jdk.charsets
```

| Module | Why it is listed |
|--------|------------------|
| `$(cat deps.info)` | Modules `jdeps` discovered from the WAR. |
| `jdk.management` | JMX / JVM management APIs. Not always pulled in by `jdeps`. |
| `jdk.charsets` | **CH-9370** (`ch-9370-docker-charsets`). Extra character-set providers. |

### Why `jdk.charsets` is required

`jdeps --print-module-deps` only reports modules that classes **statically** depend on. Charset providers are loaded **at runtime** (JDBC drivers, file imports, exports, email, non-UTF-8 report data). Those providers live in `jdk.charsets`, which is **not** part of `java.base`.

Without it, the custom JRE only has the few encodings in `java.base` (UTF-8, ISO-8859-1, US-ASCII, and similar). Anything else — Windows-1252, Shift_JIS, GBK, IBM code pages, and so on — fails with `UnsupportedCharsetException` or corrupted text.

CH-9370 therefore **always** adds `jdk.charsets` next to the `jdeps` list. Do not drop this module when editing the `jlink` line.

## Packaging

The Jenkins job `HI7DockerGenerator` does not rebuild the image. It assembles the runnable ZIP (Compose files, `hi-repository`, WAR, Instant BI, **and this Dockerfile**) and uploads it for QA.
