data "aws_ami" "latest_amazon_linux" {
  most_recent = true

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["amazon"]
}

resource "aws_instance" "ec2" {
  ami           = data.aws_ami.latest_amazon_linux.id
  instance_type = var.instance_type
  key_name      = var.key_name
  subnet_id     = var.subnet_id

  vpc_security_group_ids = [var.security_group_id]

  root_block_device {
    volume_size = var.volume_size
    volume_type = var.volume_type
    delete_on_termination = true
  }

  tags = {
    Name = var.name
  }

  associate_public_ip_address = var.associate_public_ip_address
  user_data     = var.user_data
}