# === Build Stage ===
FROM node:24-alpine AS builder

# Working directory
WORKDIR /app

# Copy dependencies
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy rest code
COPY . .

# Build app for production
RUN npm run build

# === Runtime Stage ===
# Base image
FROM nginx:stable-alpine

# Copy builded app to Nginx
COPY --from=builder /app/dist/ /usr/share/nginx/html

# Copy configuration of Nginx
COPY default.conf.template /etc/nginx/templates/default.conf.template

# Expose
EXPOSE 80