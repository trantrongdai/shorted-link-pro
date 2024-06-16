FROM node:18-alpine

WORKDIR /app
COPY package*.json ./
RUN npm install next@latest react@latest react-dom@latest
COPY . .
EXPOSE 3000
CMD npm run dev
