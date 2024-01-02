variable "name" {
  description = "The name of the security group"
  type        = string
}

variable "description" {
  description = "The description of the security group"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the VPC where the security group will be created"
  type        = string
}

variable "ingress_protocol" {
  description = "The protocol to allow (e.g., tcp, udp, icmp)"
  type        = string
}

variable "ingress_cidr_blocks" {
  description = "List of CIDR blocks to allow for ingress traffic"
  type        = list(string)
}

variable "ingress_ports" {
  description = "List of maps specifying ingress ports"
  type        = list(object({
    from_port = number
    to_port   = number
  }))
}

variable "egress_cidr_blocks" {
  description = "List of CIDR blocks to allow for egress traffic"
  type        = list(string)
}

variable "egress_from_port" {
  description = "The start port for egress traffic"
  type        = number
}

variable "egress_to_port" {
  description = "The end port for egress traffic"
  type        = number
}

variable "egress_protocol" {
  description = "The protocol to allow (e.g., tcp, udp, icmp)"
  type        = string
}