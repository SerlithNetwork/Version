FROM node:lts AS build

RUN apt-get update && apt-get install -y python3 g++ make

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build



FROM node:lts-slim

WORKDIR /app
COPY --from=build /app/.output ./.output

CMD ["node", ".output/server/index.mjs"]
EXPOSE 3000
