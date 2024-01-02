resource "aws_security_group" "sg" {
  name        = var.name
  description = var.description
  vpc_id      = var.vpc_id

  dynamic "ingress" {
    for_each = var.ingress_ports

    content {
      from_port   = ingress.value.from_port
      to_port     = ingress.value.to_port
      protocol    = var.ingress_protocol
      cidr_blocks = var.ingress_cidr_blocks
    }
  }

  egress {
    from_port   = var.egress_from_port
    to_port     = var.egress_to_port
    protocol    = var.egress_protocol
    cidr_blocks = var.egress_cidr_blocks
  }

  tags = {
    Name = var.name
  }
}