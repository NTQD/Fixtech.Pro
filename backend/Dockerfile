FROM node:18-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

ARG SERVICE_NAME
RUN npx nest build ${SERVICE_NAME}

EXPOSE 3000

CMD ["node", "dist/apps/api-gateway/main"]
