package net.serlith.version.server.types.software;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SoftwareUpdateRequest(
        String name,

        @JsonProperty("display_name")
        String displayName
) {
}
