#!/bin/sh
# install java
sudo apt update
sudo apt install openjdk-21-jdk -y
java -version

# install maven
sudo apt update && sudo apt upgrade -y
sudo apt install maven -y
mvn -version

# install nvm and nodejs
sudo apt install curl
curl https://raw.githubusercontent.com/creationix/nvm/master/install.sh | bash
source ~/.bashrc
nvm install 20.2.0

# install nextjs
npx create-next-app@latest nextjs-blog --use-npm --example "https://github.com/vercel/next-learn/tree/main/basics/learn-starter"

# install pm2
npm install pm2 -g
pm2 status

# install git
sudo apt-get update
sudo apt-get install git-all -y
git version

# build
#git clone [https://github.com/trantrongdai?tab=repositories](https://github.com/trantrongdai/shorted-link-pro.git)
#cd limits-service
#mvn clean package install
#cd target
#nohup java -jar limits-service-0.0.1-SNAPSHOT.jar &
#
#cd ..
#cd ..
#
#cd shorted-fe/
#npm install
#npm run build
#pm2 start npm -- start
#pm2 logs

