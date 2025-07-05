#!/bin/sh
# 0. install git
sudo apt-get install git

# 1. Set up Docker's apt repository.
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# 2. Install the Docker packages.
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin docker-compose -y

# 3. Verify that the Docker Engine installation is successful by running the hello-world image.
sudo docker run hello-world

# 6. open sudo permission for docker in jenkin:
sudo chmod 666 /var/run/docker.sock

# 7. install ngix and firewall
sudo apt update
sudo apt install nginx -y
sudo systemctl enable nginx
sudo apt install ufw
sudo ufw enable
sudo ufw allow ssh -y
sudo ufw allow 80
sudo ufw allow 443