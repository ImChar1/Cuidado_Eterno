# 1. Obtener la ID de la imagen de Amazon Linux mas reciente (Ahorra tiempo de busqueda)
data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-kernel-6.1-x86_64"] # Version estable y moderna
  }
}

# 2. Crear un par de llaves para entrar por SSH (Importante para administrar la EC2)
# Nota: Debes crear una llave en la consola de AWS llamada "cuidado-eterno-key" 
# o cambiar este nombre por una que ya tengas.
resource "aws_key_pair" "deployer" {
  key_name   = "cuidado-eterno-key"
  public_key = "ssh-rsa AAAAB3Nza...tu_llave_publica..." # Aqui pegarias tu llave publica .pub
}

# 3. Instancia EC2 para el Backend
resource "aws_instance" "backend_server" {
  # Usa la imagen que buscamos arriba
  ami           = data.aws_ami.amazon_linux_2023.id
  # Tipo de instancia economica (Capa gratuita)
  instance_type = "t3.micro"

  # Ubicacion: Subred publica para que Android la vea
  subnet_id                   = aws_subnet.public_1.id
  # Seguridad: Le asignamos el Security Group que creamos antes
  vpc_security_group_ids      = [aws_security_group.backend_sg.id]
  # Llave SSH
  key_name                    = aws_key_pair.deployer.key_name
  # Le pedimos que nos de una IP publica para conectar la App
  associate_public_ip_address = true

  # Script de inicio (User Data): Esto se ejecuta apenas se prende la maquina
  user_data = <<-EOF
              #!/bin/bash
              # Actualizar el sistema
              dnf update -y
              # Instalar Java 17 (el que elegimos en Spring Initializr)
              dnf install java-17-amazon-corretto-devel -y
              EOF

  tags = {
    Name = "backend-cuidado-eterno"
  }
}

# 4. Output: Para que Terraform nos diga la IP publica al terminar
output "backend_public_ip" {
  description = "Direccion IP publica del servidor backend"
  value       = aws_instance.backend_server.public_ip
}