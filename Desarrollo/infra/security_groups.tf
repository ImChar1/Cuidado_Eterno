# 1. Security Group para el Backend (EC2)
resource "aws_security_group" "backend_sg" {
  name        = "backend-security-group"
  description = "Permite trafico para Spring Boot y SSH"
  vpc_id      = aws_vpc.main.id

  # Regla para que la App Android se conecte al Backend
  ingress {
    description = "Acceso REST API desde Android"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permite cualquier IP (Internet)
  }

  # Regla para que tu puedas entrar por terminal a la EC2
  ingress {
    description = "Acceso SSH para administracion"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Lo ideal seria poner tu IP aqui por seguridad
  }

  # Regla de salida: Permite que la EC2 descargue cosas (Java, Updates, S3)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1" # -1 significa todos los protocolos
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "sg-backend" }
}

# 2. Security Group para la Base de Datos (RDS)
resource "aws_security_group" "db_sg" {
  name        = "db-security-group"
  description = "Permite conexion solo desde el backend"
  vpc_id      = aws_vpc.main.id

  # Regla de ORO: Solo el Backend puede hablar con la DB
  ingress {
    description     = "MySQL desde el Backend"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    # IMPORTANTE: En lugar de IP, usamos el ID del Security Group del Backend
    security_groups = [aws_security_group.backend_sg.id]
  }

  # Regla de salida para la DB (usualmente no necesita salir a internet)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "sg-database" }
}