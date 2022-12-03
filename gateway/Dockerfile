FROM node:18.12.1-alpine3.15 AS builder

# Add wget dependencie and create app directory
RUN apk update &&  \
    apk add wget &&  \
    rm -rf /var/cache/apk/* && \
    mkdir -p /app

# Install node-prune (https://github.com/tj/node-prune)
RUN wget -qO- https://gobinaries.com/tj/node-prune | ash -s

WORKDIR /app

# Set-up Yarn
RUN corepack enable && corepack prepare yarn@3.3.0

# Install dependencies (including dev dependencies because we need to build the app)
COPY . .
RUN yarn install
RUN yarn build

# Optimize node_modules
RUN npm prune --omit=dev
RUN /usr/local/bin/node-prune

FROM node:18.12.1-alpine3.15

RUN mkdir -p /app

WORKDIR /app

COPY --from=builder /app/dist/ .
COPY --from=builder /app/node_modules/ ./node_modules/

CMD exec node $NODE_OPTS main.js
