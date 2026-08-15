package net.serlith.version.server.types.management;

import jakarta.validation.constraints.Size;

public record PrivilegedUserCreateRequest(
        @Size(min = 4, max = 64, message = "Username must be at least 4 characters long")
        String username,

        @Size(min = 4, max = 64, message = "Username must be at least 4 characters long")
        String password
) {
}
