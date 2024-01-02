resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = var.enable_dns_support
  enable_dns_hostnames = var.enable_dns_hostnames

  tags = {
    Name = var.vpc_name
  }
}

resource "aws_subnet" "public" {
  count                  = length(var.availability_zones)
  vpc_id                 = aws_vpc.main.id
  cidr_block            = element(var.public_subnet_cidrs, count.index)
  availability_zone      = element(var.availability_zones, count.index)

  tags = {
    Name = "${var.vpc_name}-public-${count.index + 1}"
  }
}

resource "aws_subnet" "private" {
  count                  = length(var.availability_zones)
  vpc_id                 = aws_vpc.main.id
  cidr_block            = element(var.private_subnet_cidrs, count.index)
  availability_zone      = element(var.availability_zones, count.index)

  tags = {
    Name = "${var.vpc_name}-private-${count.index + 1}"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.vpc_name}-igw"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.vpc_name}-public-rt"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.vpc_name}-private-rt"
  }
}

resource "aws_route_table_association" "public" {
  count         = length(aws_subnet.public[*].id)
  subnet_id     = element(aws_subnet.public[*].id, count.index)
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  count         = length(aws_subnet.private[*].id)
  subnet_id     = element(aws_subnet.private[*].id, count.index)
  route_table_id = aws_route_table.private.id
}