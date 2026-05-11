# 1. Crear el Bucket S3 para almacenamiento de archivos
resource "aws_s3_bucket" "multimedia_storage" {
  # El nombre debe ser UNICO en todo AWS. Sugiero agregarle un sufijo aleatorio o tu nombre.
  bucket = "cuidado-eterno-multimedia-storage-tu-nombre" 

  # Fuerza la eliminacion del bucket aunque tenga archivos dentro al hacer 'terraform destroy'
  force_destroy = true

  tags = {
    Name        = "S3-Multimedia-Cuidado-Eterno"
    Environment = "Dev"
  }
}

# 2. Bloquear acceso publico total al Bucket (Seguridad ISO 27001)
# Esto asegura que nadie pueda ver las fotos desde fuera sin permiso del Backend
resource "aws_s3_bucket_public_access_block" "block_public" {
  bucket = aws_s3_bucket.multimedia_storage.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 3. VPC Endpoint para S3 (Tipo Gateway)
# Permite que la EC2 se conecte al S3 de forma privada y GRATUITA
resource "aws_vpc_endpoint" "s3_endpoint" {
  vpc_id       = aws_vpc.main.id
  service_name = "com.amazonaws.us-east-1.s3"
  # Tipo Gateway es el que no tiene costo por hora
  vpc_endpoint_type = "Gateway"

  tags = { Name = "vpce-s3-cuidado-eterno" }
}

# 4. Asociar el Endpoint a la Tabla de Ruteo de la Subred Publica
# (Donde esta tu EC2 de Backend)
resource "aws_vpc_endpoint_route_table_association" "s3_assoc" {
  route_table_id  = aws_route_table.public_rt.id
  vpc_endpoint_id = aws_vpc_endpoint.s3_endpoint.id
}