variable "aws_region" {
  default = "us-east-1"
}

variable "project_name" {
  default = "cuidado-eterno"
}

variable "key_pair_name" {
  description = "Nombre de tu archivo .pem en AWS"
  type        = string
}