package net.serlith.version.server.types;

import net.serlith.version.server.schema.tables.records.VersionSoftwareRecord;

public record SoftwareData(long id, String name, String token) {

    public static SoftwareData from(final VersionSoftwareRecord record) {
        return new SoftwareData(record.getId(), record.getName(), record.getToken());
    }

}
