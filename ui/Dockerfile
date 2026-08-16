FROM oven/bun:1-debian AS build

RUN apt-get update && apt-get install -y python3 g++ make

WORKDIR /app
COPY package.json bun.lock ./
RUN bun install --frozen-lockfile --production

COPY . ./
RUN bun run build

CMD ["bun", ".output/server/index.mjs"]
EXPOSE 3000
