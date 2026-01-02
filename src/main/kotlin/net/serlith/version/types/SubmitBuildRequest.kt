package net.serlith.version.types

data class SubmitBuildRequest (
    var software: String,
    var version: String,
    var build: Int,
)