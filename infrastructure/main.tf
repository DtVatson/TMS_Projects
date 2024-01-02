module "network" {
  source                = "./modules/network"
  vpc_name              = "dos-15-kepets-vpc"
  vpc_cidr              = "10.0.0.0/16"
  availability_zones    = ["us-east-1a"]
  public_subnet_cidrs   = ["10.0.1.0/24"]
  private_subnet_cidrs  = ["10.0.2.0/24"]
  enable_dns_hostnames  = true
  enable_dns_support    = true
}

module "jenkins_master_sg" {
  source = "./modules/security"

  name                 = "dos_15_kepets_jenkins_master_sg"
  description          = "Security Group for Jenkins Master."
  vpc_id               = module.network.vpc_id

  ingress_ports        = [
    { from_port = 57385, to_port = 57385 },
    { from_port = 57386, to_port = 57386 }
  ]
  ingress_protocol     = "tcp"
  ingress_cidr_blocks  = ["0.0.0.0/0"]

  egress_from_port    = 0
  egress_to_port      = 0
  egress_protocol     = "-1"
  egress_cidr_blocks   = ["0.0.0.0/0"]
}

module "jenkins_slave_sg" {
  source = "./modules/security"

  name                 = "dos_15_kepets_jenkins_slave_sg"
  description          = "Security Group for Jenkins Slave."
  vpc_id               = module.network.vpc_id

  ingress_ports        = [
    { from_port = 57387, to_port = 57387 },
    { from_port = 57388, to_port = 57388 }
  ]
  ingress_protocol     = "tcp"
  ingress_cidr_blocks  = ["0.0.0.0/0"]

  egress_from_port    = 0
  egress_to_port      = 0
  egress_protocol     = "-1"
  egress_cidr_blocks   = ["0.0.0.0/0"]
}

module "prod_sg" {
  source = "./modules/security"

  name                 = "dos_15_kepets_ec2_prod_sg"
  description          = "Security Group for prod stage."
  vpc_id               = module.network.vpc_id

  ingress_ports        = [
    { from_port = 22, to_port = 22 },
    { from_port = 3000, to_port = 3000 }
  ]
  ingress_protocol     = "tcp"
  ingress_cidr_blocks  = ["0.0.0.0/0"]

  egress_from_port    = 0
  egress_to_port      = 0
  egress_protocol     = "-1"
  egress_cidr_blocks   = ["0.0.0.0/0"]
}

module "jenkins_master" {
  source = "./modules/ec2"

  name                  = "dos_15_kepets_jenkins_master"
  instance_type         = "t2.micro"
  key_name              = "DOS_15_kepets"
  subnet_id             = element(module.network.public_subnet_ids, 0)
  security_group_id     = module.jenkins_master_sg.security_group_id
  volume_size           = 8
  volume_type           = "gp2"
  associate_public_ip_address = true
  user_data             = "${file("./modules/ec2/scripts/jenkins_install.sh")}"
}

module "jenkins_slave" {
  source = "./modules/ec2"

  name                  = "dos_15_kepets_jenkins_slave"
  instance_type         = "t2.micro"
  key_name              = "DOS_15_kepets"
  subnet_id             = element(module.network.public_subnet_ids, 0)
  security_group_id     = module.jenkins_slave_sg.security_group_id
  volume_size           = 8
  volume_type           = "gp2"
  associate_public_ip_address = true
  user_data             = <<-EOF
    #!/bin/bash

    sudo sed -i 's/#Port\s22/Port 57387/' /etc/ssh/sshd_config || exit 1
    sudo systemctl restart sshd.service || exit 1
    sleep 5

    sudo yum update -y || exit 1
    sudo yum install java-17-amazon-corretto -y || exit 1
  EOF
}

module "prod_stage" {
  source = "./modules/ec2"

  name                  = "dos_15_kepets_prod_stage"
  instance_type         = "t2.micro"
  key_name              = "DOS_15_kepets"
  subnet_id             = element(module.network.public_subnet_ids, 0)
  security_group_id     = module.prod_sg.security_group_id
  volume_size           = 8
  volume_type           = "gp2"
  associate_public_ip_address = true
  user_data             = <<-EOF
    #!/bin/bash
    sudo yum update -y || exit 1
    sudo amazon-linux-extras install docker -y || exit 1
    sudo service docker start || exit 1
    sudo service docker status || exit 1
    sudo groupadd docker || exit 1
    sudo usermod -a -G docker ec2-user || exit 1
    newgrp docker || exit 1
    docker --version || exit 1
    sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose || exit 1
    sudo chmod +x /usr/local/bin/docker-compose || exit 1
    docker-compose version || exit 1
  EOF
}

module "ecr_frontend" {
  source = "./modules/ecr"

  name                  = "frontend_dos_15_kepets"
  image_tag_mutability  = "IMMUTABLE"
  scan_on_push          = true

}

module "ecr_backend" {
  source = "./modules/ecr"

  name                  = "backend_dos_15_kepets"
  image_tag_mutability  = "IMMUTABLE"
  scan_on_push          = true

}