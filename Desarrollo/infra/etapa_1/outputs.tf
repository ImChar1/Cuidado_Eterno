output "backend_ecr_url" {
  value       = aws_ecr_repository.backend.repository_url
  description = "URL del repositorio ECR para subir tu imagen de Spring Boot"
}

output "s3_bucket_name" {
  value       = aws_s3_bucket.evidencias.bucket
  description = "Nombre del bucket S3 para conectar en tu application.yml"
}