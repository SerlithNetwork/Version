package net.serlith.version.server.types;

import net.serlith.version.server.schema.tables.records.VersionServerRecord;
import net.serlith.version.server.schema.tables.records.VersionVersionRecord;
import org.jspecify.annotations.NullMarked;
import reactor.util.function.Tuple2;

@NullMarked
public record ServerVersionData(String software, String version, long build) {

    public static ServerVersionData from(final Tuple2<VersionServerRecord, VersionVersionRecord> tuple) {
        return new ServerVersionData(tuple.getT1().getName(), tuple.getT2().getVersion(), tuple.getT2().getBuild());
    }

}
