package net.serlith.version.server.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.serlith.version.server.schema.tables.records.VersionSoftwareRecord;

import java.time.LocalDateTime;

public record SoftwareDataTokenized(
        long id,
        String name,

        @JsonProperty("display_name")
        String display,
        String token,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {

    public static SoftwareDataTokenized from(final VersionSoftwareRecord record, final String token) {
        return new SoftwareDataTokenized(
                record.getId(),
                record.getName(),
                record.getDisplay(),
                token,
                record.getCreatedAt()
        );
    }

}
