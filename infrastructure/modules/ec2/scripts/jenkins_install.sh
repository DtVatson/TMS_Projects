#!/bin/bash

sudo sed -i 's/#Port\s22/Port 57385/' /etc/ssh/sshd_config || exit 1
sudo systemctl restart sshd.service || exit 1
sleep 5

sudo yum update -y || exit 1

sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo || exit 1
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key || exit 1
sudo yum upgrade -y || exit 1

sudo yum install java-17-amazon-corretto -y || exit 1
sudo yum install jenkins -y || exit 1

sudo sed -i 's/Environment="JENKINS_PORT=8080"/Environment="JENKINS_PORT=57386"/' /usr/lib/systemd/system/jenkins.service || exit 1
sudo systemctl daemon-reload || exit 1

sudo systemctl enable jenkins || exit 1
sudo systemctl restart jenkins || exit 1
sleep 30