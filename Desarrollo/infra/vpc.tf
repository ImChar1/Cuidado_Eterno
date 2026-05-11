# 1. Crear la Red Virtual Privada (VPC)
resource "aws_vpc" "main" {
  # Rango de IPs privadas para la nube (65,536 direcciones posibles)
  cidr_block           = "10.0.0.0/16"
  # Permite que las instancias tengan nombres de dominio (ej: rds.amazonaws.com)
  enable_dns_hostnames = true
  # Etiqueta para identificar el recurso en la consola de AWS
  tags = { Name = "vpc-cuidado-eterno" }
}

# 2. Internet Gateway: La puerta de entrada y salida a Internet
resource "aws_internet_gateway" "igw" {
  # Se vincula a la VPC que creamos arriba
  vpc_id = aws_vpc.main.id
  tags   = { Name = "igw-cuidado-eterno" }
}

# 3. Subred Pública: Aquí vivirá tu EC2 (Backend)
resource "aws_subnet" "public_1" {
  vpc_id            = aws_vpc.main.id
  # Rango de IPs para esta subred (254 direcciones)
  cidr_block        = "10.0.1.0/24"
  # Zona física en el norte de Virginia
  availability_zone = "us-east-1a"
  # Importante: Esto le da una IP pública automática a lo que pongas aquí
  map_public_ip_on_launch = true
  tags = { Name = "subnet-public-backend" }
}

# 4. Subred Privada: Aquí vivirá tu RDS (Base de Datos)
resource "aws_subnet" "private_1" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "us-east-1a"
  # No queremos que la base de datos tenga IP pública
  map_public_ip_on_launch = false
  tags = { Name = "subnet-private-db" }
}

# 5. Tabla de Ruteo: El mapa de tráfico para salir a internet
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id
  # Define que todo el tráfico (0.0.0.0/0) vaya hacia el Internet Gateway
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = { Name = "public-route-table" }
}

# 6. Asociación: Conectar la tabla de ruteo con la subred pública
resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public_rt.id
}