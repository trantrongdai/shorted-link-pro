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
sudo apt update
sudo apt install git-all -y
git version

# install jenkin
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update
sudo apt install jenkins -y

#get password:
sudo cat /var/lib/jenkins/secrets/initialAdminPassword

## create jenkins folder and ssh-key for username app ()
#sudo su - jenkins
#ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -C "app@jenkin" -N ""
#chmod 600 /var/lib/jenkins/.ssh/id_rsa
#exit