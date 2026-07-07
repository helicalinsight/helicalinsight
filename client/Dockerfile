# ============================
# Stage 1: Build React App
# ============================
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN --mount=type=cache,target=/root/.npm \
    npm ci --legacy-peer-deps

# Copy application source
COPY . .

# Build React application
ENV NODE_OPTIONS="--max_old_space_size=5096 --openssl-legacy-provider"

RUN npm run build

# ============================
# Stage 2: Serve using Nginx
# ============================
FROM nginx:stable-alpine


COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

RUN mkdir -p /usr/share/nginx/html/static
# Copy build output
COPY --from=builder /app/build /usr/share/nginx/html/static
COPY --from=builder /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
