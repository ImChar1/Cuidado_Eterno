output "proxy_public_ip" {
  value       = aws_instance.proxy.public_ip
  description = "IP pública del Proxy. Tu app Android debe apuntar sus peticiones HTTP aquí."
}

output "backend_private_ip" {
  value       = aws_instance.backend.private_ip
  description = "IP privada del Backend. Úsala en la configuración de Nginx del Proxy."
}

output "database_private_ip" {
  value       = aws_instance.database.private_ip
  description = "IP privada de la Base de Datos. Úsala en tu variable DB_URL."
}