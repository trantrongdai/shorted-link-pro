FROM node:18-alpine

WORKDIR /app
COPY package*.json ./
RUN npm install next@14.2.3 react@18.2.0 react-dom@18.2.0
COPY . .
EXPOSE 3000
CMD npm run dev
