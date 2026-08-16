package net.serlith.version.server.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.serlith.version.server.schema.tables.records.VersionSoftwareRecord;

import java.time.LocalDateTime;

public record SoftwareDataTokenless(
        long id,
        String name,

        @JsonProperty("display_name")
        String display,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {

    public static SoftwareDataTokenless from(final VersionSoftwareRecord record) {
        return new SoftwareDataTokenless(record.getId(), record.getName(), record.getDisplay(), record.getCreatedAt());
    }

}
