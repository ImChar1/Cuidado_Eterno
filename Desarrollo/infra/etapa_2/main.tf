terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.project_name}-ec2-profile"
  role = data.aws_iam_role.lab_role.name
}

# ================= REDES (VPC) =================
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  tags = { Name = "${var.project_name}-vpc" }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags = { Name = "${var.project_name}-igw" }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  tags = { Name = "${var.project_name}-subnet-public" }
}

resource "aws_subnet" "private" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  map_public_ip_on_launch = false
  tags = { Name = "${var.project_name}-subnet-private" }
}

# ================= ENRUTAMIENTO =================
# NAT Gateway para que la subred privada pueda descargar Docker/Imágenes
resource "aws_eip" "nat" {
  domain = "vpc"
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public.id
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
}
resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }
}
resource "aws_route_table_association" "private_assoc" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.private_rt.id
}

# ================= SEGURIDAD =================
resource "aws_security_group" "sg_proxy" {
  name        = "${var.project_name}-sg-proxy"
  description = "Permitir trafico web desde la app Android"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Idealmente restringir a tu IP
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "sg_backend" {
  name        = "${var.project_name}-sg-backend"
  description = "Permitir trafico solo desde el Proxy"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Spring Boot desde Proxy"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.sg_proxy.id]
  }
  ingress {
    description     = "SSH desde Proxy (Bastion)"
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.sg_proxy.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

#Security Group para la Base de Datos
resource "aws_security_group" "sg_database" {
  name        = "${var.project_name}-sg-database"
  description = "Permitir trafico de BD solo desde el Backend"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "MariaDB desde Backend"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.sg_backend.id]
  }
  ingress {
    description     = "SSH desde Proxy (Bastion)"
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.sg_proxy.id]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }
}

# ================= SERVIDORES (EC2) =================

# Servidor 1: El Proxy Público (Nginx)
resource "aws_instance" "proxy" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.sg_proxy.id]
  key_name               = var.key_pair_name

  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y nginx
    systemctl start nginx
    systemctl enable nginx
    # Aqui luego se configura el proxy_pass apuntando a la IP privada del Backend
  EOF

  tags = { Name = "${var.project_name}-ec2-proxy" }
}

# Servidor 2: El Backend (Privado)
resource "aws_instance" "backend" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t3.small" # t3.small recomendado para correr Java + MariaDB
  subnet_id              = aws_subnet.private.id
  vpc_security_group_ids = [aws_security_group.sg_backend.id]
  key_name               = var.key_pair_name
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name

  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y docker aws-cli
    systemctl start docker
    systemctl enable docker
    usermod -aG docker ec2-user
    
    # Instalar Docker Compose v2
    curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    mkdir -p /home/ec2-user/app
    chown -R ec2-user:ec2-user /home/ec2-user/app
  EOF

  tags = { Name = "${var.project_name}-ec2-backend" }
}

# Servidor 3: La Base de Datos MariaDB (Privado)
resource "aws_instance" "database" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t3.small" 
  subnet_id              = aws_subnet.private.id
  vpc_security_group_ids = [aws_security_group.sg_database.id]
  key_name               = var.key_pair_name
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name

  # PROTECCIÓN CONTRA BORRADO DESDE LA CONSOLA DE AWS
  # COLOCAR TRUE PARA ACTIVAR Y NO BORRAR LOS DATOS DE LA BD
  disable_api_termination = false 

  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y docker aws-cli cronie
    systemctl start docker
    systemctl enable docker
    systemctl start crond
    systemctl enable crond
    usermod -aG docker ec2-user
    
    curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    mkdir -p /home/ec2-user/app/backups
    chown -R ec2-user:ec2-user /home/ec2-user/app

    # CREACIÓN AUTOMÁTICA DEL CRONJOB DE RESPALDO (A LAS 2 AM)
    echo "0 2 * * * root docker exec cuidado_eterno_db mariadb-dump -u ce_user -pce_pass cuidado_eterno > /home/ec2-user/app/backups/cuidado_eterno_\$(date +\%Y\%m\%d).sql" > /etc/cron.d/db_backup
    chmod 0644 /etc/cron.d/db_backup
  EOF

  tags = { Name = "${var.project_name}-ec2-database" }

  # PROTECCIÓN CONTRA TERRAFORM DESTROY
  #lifecycle {
  #  prevent_destroy = true 
  #}
}