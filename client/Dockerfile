FROM node:16.13.0-alpine as builder
WORKDIR /user/src/app
ADD package.json ./
RUN npm install
RUN npm install -g @angular/cli@13.0.3
COPY . .
RUN npm run build

FROM nginx:alpine
COPY client-certs/hearts-certificate.crt /etc/ssl/certs/hearts-certificate.crt
COPY client-certs/hearts-key.key /etc/ssl/private/hearts-key.key
COPY default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /user/src/app/dist/client /usr/share/nginx/html
