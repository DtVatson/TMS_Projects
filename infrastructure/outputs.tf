output "vpc_id" {
  value = module.network.vpc_id
}

output "public_subnet_ids" {
  value = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  value = module.network.private_subnet_ids
}

output "public_route_table_id" {
  value = module.network.public_route_table_id
}

output "private_route_table_id" {
  value = module.network.private_route_table_id
}

output "internet_gateway_id" {
  value = module.network.internet_gateway_id
}

output "jenkins_master_security_group_id" {
  value       = module.jenkins_master_sg.security_group_id
  description = "The ID of the Jenkins Master security group"
}

output "jenkins_slave_security_group_id" {
  value       = module.jenkins_slave_sg.security_group_id
  description = "The ID of the Jenkins Slave security group"
}

output "prod_stage_group_id" {
  value       = module.prod_sg.security_group_id
  description = "The ID of the Production stage security group"
}

output "jenkins_master_instance_id" {
  value       = module.jenkins_master.instance_id
  description = "The ID of the Jenkins Master instance"
}

output "jenkins_master_public_ip" {
  value       = module.jenkins_master.public_ip
  description = "The public IP address of the Jenkins Master instance"
}

output "jenkins_slave_instance_id" {
  value       = module.jenkins_slave.instance_id
  description = "The ID of the Jenkins Slave instance"
}

output "jenkins_slave_public_ip" {
  value       = module.jenkins_slave.public_ip
  description = "The public IP address of the Jenkins Slave instance"
}

output "prod_stage_instance_id" {
  value       = module.prod_stage.instance_id
  description = "The ID of the Production Stage instance"
}

output "prod_stage_public_ip" {
  value       = module.prod_stage.public_ip
  description = "The public IP address of the Production Stage instance"
}