#!/bin/sh
# install java
sudo apt update
sudo apt install openjdk-21-jdk -y
java -version

# install maven
sudo apt update && sudo apt upgrade -y
sudo apt install maven -y
mvn -version

# install git
sudo apt-get update
sudo apt-get install git-all -y
git version

# install jenkin
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt-get update
sudo apt-get install jenkins -y

#get password:
sudo cat /var/lib/jenkins/secrets/initialAdminPassword