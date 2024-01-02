variable "name" {
  description = "The name of the EC2 instance"
  type        = string
}

variable "instance_type" {
  description = "The type of instance to start"
  type        = string
}

variable "key_name" {
  description = "The key name to use for the instance"
  type        = string
}

variable "subnet_id" {
  description = "The VPC Subnet ID to launch in"
  type        = string
}

variable "security_group_id" {
  description = "The ID of the security group"
  type        = string
}

variable "volume_size" {
  description = "The size of the volume in gigabytes"
  type        = number
}

variable "volume_type" {
  description = "The type of volume. Can be 'standard', 'gp2', or 'io1'"
  type        = string
}

variable "associate_public_ip_address" {
  description = "If true, the EC2 instance will have associated public IP address"
  type        = bool
}

variable "user_data" {
  description = "For post-install scripts"
  type        = string
}