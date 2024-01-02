output "vpc_id" {
  value = aws_vpc.main.id
}

output "public_subnet_ids" {
  value = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  value = aws_subnet.private[*].id
}

output "public_route_table_id" {
  value = aws_route_table.public.id
}

output "private_route_table_id" {
  value = aws_route_table.private.id
}

output "internet_gateway_id" {
  value = aws_internet_gateway.igw.id
}

output "vpc_name" {
  value = var.vpc_name
}

output "vpc_cidr" {
  value = var.vpc_cidr
}