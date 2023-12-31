terraform {
  backend "s3" {
    bucket         = "dos-15-kepets-s3"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "dos-15-kepets-dynamodb"
  }
}