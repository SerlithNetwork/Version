package net.serlith.version.server.types;

import net.serlith.version.server.schema.tables.records.VersionVersionRecord;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record VersionData(String version, long build) {

    public static VersionData from(final VersionVersionRecord record) {
        return new VersionData(record.getVersion(), record.getBuild());
    }

}
