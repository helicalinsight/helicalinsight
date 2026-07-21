# Helical Insight — Frontend (Client)

The Helical Insight frontend is a React 17 single-page application that provides the report designer, dashboard builder, metadata explorer, administration console, and all end-user BI workflows. It communicates with the Java backend (`hi-ee` WAR) over HTTP/HTTPS.

## Table of contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project setup](#project-setup)
- [Connecting to the backend](#connecting-to-the-backend)
- [Running the development server](#running-the-development-server)
- [Production build](#production-build)
- [Docker](#docker)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## Architecture

| Layer | Technology |
|-------|------------|
| UI framework | React 17 |
| State management | Redux (+ Redux Toolkit) |
| Component library | Ant Design 4 |
| Charts / visualization | AntV S2, Ant Design Plots, Muze, Syncfusion Pivot |
| Routing | React Router 5 |
| HTTP client | Axios |
| Build tooling | Create React App (customized Webpack 4) |

The dev server runs on **port 3000** by default. API requests are proxied to the backend (see [Connecting to the backend](#connecting-to-the-backend)).

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 18.x (LTS recommended) |
| npm | 9+ |

Verify your installation:

```bash
node -v    # v18.x.x
npm -v
```

> **Node 18+:** If you encounter OpenSSL errors during `npm start`, use the `start18` script (see [Running the development server](#running-the-development-server)).

## Project setup

### 1. Install dependencies

```bash
cd client
npm ci --legacy-peer-deps
```

Requires `package-lock.json` in the `client/` directory. The `--legacy-peer-deps` flag is required due to peer dependency constraints in the dependency tree.

> **Quick start:** From the repo root, run `docker compose -f docker-compose.dev.yml up --build` to start backend + frontend together without a local Tomcat install.

### 2. Ensure the backend is running

The frontend requires a running Helical Insight backend deployed on Tomcat. See **[server/README.md](../server/README.md)** to build and start the backend before continuing.

Default backend URL (Tomcat):

```
http://localhost:8080/hi-ee/
```

## Connecting to the backend

During development, API calls are forwarded to the backend via a proxy configured in `package.json`.

### Option A — Direct proxy (simplest, default)

The `proxy` field in `package.json` defaults to `http://localhost:8080`. Start the dev server:

```bash
npm run start18
```

To point at a different backend, edit `package.json`:

```json
"proxy": "http://localhost:8080"
```

Or use `npm start` on Node 16/17.

### Option B — Local proxy server (flexible)

For switching between environments without editing `package.json` on every change:

1. Edit `proxy.js` and set the target to your backend:

   ```javascript
   proxy.web(req, res, { target: "http://localhost:8080" }); // LOCAL
   ```

2. Start the proxy in one terminal:

   ```bash
   npm run proxy
   # Listens on http://localhost:5050
   ```

3. Point `package.json` at the proxy (or use the helper script):

   ```bash
   node proxy-helper.js   # sets proxy to http://localhost:5050/
   ```

4. Start the frontend in a second terminal:

   ```bash
   npm start
   ```

### Backend URL in production

In production, the React app is served as static files. Configure your reverse proxy (Nginx, Apache, or the included `nginx.conf`) to forward API requests to the Tomcat backend. See [Docker](#docker) for an Nginx example.

## Running the development server

```bash
# Standard start (Node 16/17)
npm start

# Node 18+ (adds --openssl-legacy-provider)
npm run start18
```

Open [http://localhost:3000](http://localhost:3000) in your browser. The page reloads automatically when you save source files.

### Default login

Use the backend's default credentials (created on first startup):

| Username | Password |
|----------|----------|
| `hiadmin` | `hiadmin` |
| `hiuser` | `hiuser` |

## Production build

```bash
npm run build
```

Output is written to `build/`. Deploy the contents of `build/` to any static file server or serve them via the Docker image below.

Analyze bundle size after building:

```bash
npm run analyze
```

## Docker

The frontend includes a multi-stage `Dockerfile` that builds the React app with Node 18 and serves it with Nginx. Pair it with the [backend Docker image](../server/README.md#docker) or a Tomcat deployment on the host.

### Prerequisites for Docker build

The Dockerfile uses `npm ci`, which requires `package-lock.json` to be present in `client/`. Install dependencies before building:

```bash
cd client
npm ci --legacy-peer-deps
```

### Configure Nginx for your backend

The included `nginx.conf` proxies `/hi-ee/` to the backend service (`http://backend:8080/hi-ee/`) when using `docker-compose.dev.yml`. For other deployments, update the `proxy_pass` target:

```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    # Proxy API calls to the Helical Insight backend (Tomcat)
    location /hi-ee/ {
        proxy_pass http://backend:8080/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Serve the React SPA
    location / {
        try_files $uri $uri/ /index.html;
    }

    location /static/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

If the backend runs in a Docker container on the same network, use the service or container name:

```nginx
proxy_pass http://hi-backend:8080/hi-ee/;
```

If Tomcat runs on the host machine and the frontend container uses Docker's default bridge network:

- **Linux:** use `http://host.docker.internal:8080` (Docker 20.10+) or the host's LAN IP.
- **Windows / macOS:** use `http://host.docker.internal:8080`.

### Build the frontend image

```bash
cd client
docker build -t helicalinsight/frontend:latest .
```

### Run the frontend container

```bash
docker run -d \
  --name hi-frontend \
  -p 3000:80 \
  helicalinsight/frontend:latest
```

Open [http://localhost:3000](http://localhost:3000).

### Production layout

A typical containerized production setup:

```
┌─────────────────┐     /hi-ee/*      ┌──────────────────────┐
│  Nginx (client  │ ────────────────► │  Tomcat (server      │
│  Docker image)  │                   │  Docker image)       │
│  port 80/443    │                   │  port 8080           │
└─────────────────┘                   └──────────────────────┘
```

1. Build and run the backend image ([server README](../server/README.md#docker)).
2. Build and run the frontend image with `nginx.conf` pointing at `http://hi-backend:8080/hi-ee/`.
3. Users access the app through the frontend URL; API traffic is proxied to `/hi-ee/`.

## Testing

```bash
# Run all tests once (CI mode)
npm test

# Interactive watch mode
npm run test:dev

# Debug mode with heap logging
npm run test:debug
```

Test results are also written to `jest-stare/` when using the default Jest reporters.

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `ECONNREFUSED` on API calls | Backend not running or wrong proxy target | Verify backend at `http://localhost:8080/hi-ee` and update `proxy` in `package.json` |
| `error:0308010C:digital envelope routines::unsupported` | Node 17+ OpenSSL change | Use `npm run start18` |
| `npm ci` peer dependency errors | Strict peer resolution | Use `npm ci --legacy-peer-deps` |
| Out of memory during build | Large bundle | Build already sets `--max_old_space_size=5096`; increase if needed |
| Blank page after Docker deploy | Nginx not serving SPA routes | Add `try_files $uri $uri/ /index.html` to `nginx.conf` |
| Docker build fails at `npm ci` | Missing `package-lock.json` | Ensure `package-lock.json` is present in `client/` |
| Container cannot reach backend | Wrong backend host in `nginx.conf` | Use `http://hi-backend:8080` on a shared Docker network, or `host.docker.internal` for host Tomcat |

## Project scripts

| Script | Description |
|--------|-------------|
| `npm start` | Dev server on port 3000 |
| `npm run start18` | Dev server with OpenSSL legacy provider (Node 18+) |
| `npm run build` | Production build to `build/` |
| `npm test` | Run Jest tests (single run) |
| `npm run test:dev` | Jest in watch mode |
| `npm run analyze` | Bundle size analysis (requires build) |
| `npm run proxy` | Start local API proxy on port 5050 |

## Related documentation

- [Backend README](../server/README.md)
- [Root README](../README.md)
