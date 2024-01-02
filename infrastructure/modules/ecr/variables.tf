variable "name" {
  type = string
  description = "Name of the repository."
}

variable "image_tag_mutability" {
  type = string
  description = "The tag mutability setting for the repository. Must be one of: MUTABLE or IMMUTABLE. Defaults to MUTABLE"
  default = "MUTABLE"
}

variable "scan_on_push" {
  type = bool
  description = "Indicates whether images are scanned after being pushed to the repository (true) or not scanned (false)."
  default = true
}

variable "tags" {
  type = map(string)
  description = "(Optional) A map of tags to assign to the resource."
  default = {}
}
