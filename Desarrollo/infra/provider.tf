# Bloque de configuración de Terraform
terraform {
  # Define los proveedores necesarios para este proyecto
  required_providers {
    # Nombre del proveedor (aws)
    aws = {
      # Origen oficial del proveedor en el registro de Hashicorp
      source  = "hashicorp/aws"
      # Versión del proveedor (el ~> 5.0 permite actualizaciones menores de la v5)
      version = "~> 5.0"
    }
  }
}

# Configuración específica del proveedor AWS
provider "aws" {
  # Región de AWS donde se desplegará toda la infraestructura
  region = "us-east-1" 
}