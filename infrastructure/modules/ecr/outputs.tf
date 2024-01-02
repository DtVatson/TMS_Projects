output "repository_arn" {
  description = "Full ARN of the repository"
  value       = aws_ecr_repository.ecr_repository.arn
}

output "repository_registry_id" {
  description = "The registry ID where the repository was created"
  value       = aws_ecr_repository.ecr_repository.id
}

output "repository_url" {
  description = "The URL of the repository"
  value       = aws_ecr_repository.ecr_repository.repository_url
}