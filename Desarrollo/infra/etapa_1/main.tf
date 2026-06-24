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

# ECR: Repositorio para la imagen de Spring Boot
resource "aws_ecr_repository" "backend" {
  name         = "${var.nombre_proyecto}-backend"
  force_delete = true
}

# S3: Bucket para almacenar las fotos de evidencias
resource "aws_s3_bucket" "evidencias" {
  bucket        = "cuidado-eterno-duoc-puente-bucket-s3"
  force_destroy = true
}

# Propiedad del bucket (ACL privada por defecto para seguridad)
resource "aws_s3_bucket_ownership_controls" "evidencias_acl" {
  bucket = aws_s3_bucket.evidencias.id
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}