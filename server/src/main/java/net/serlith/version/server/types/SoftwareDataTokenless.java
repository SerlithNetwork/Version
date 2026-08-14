package net.serlith.version.server.types;

import net.serlith.version.server.schema.tables.records.VersionSoftwareRecord;

public record SoftwareDataTokenless(long id, String name, String display) {

    public static SoftwareDataTokenless from(final VersionSoftwareRecord record) {
        return new SoftwareDataTokenless(record.getId(), record.getName(), record.getDisplay());
    }

}
