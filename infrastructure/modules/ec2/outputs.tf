output "instance_id" {
  description = "The ID of the instance"
  value       = aws_instance.ec2.id
}

output "public_ip" {
  description = "The public IP address assigned to the instance"
  value       = aws_instance.ec2.public_ip
}