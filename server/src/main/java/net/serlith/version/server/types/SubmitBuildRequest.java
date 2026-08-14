package net.serlith.version.server.types;

public record SubmitBuildRequest(
        String software,
        String version,
        long build
) {
}
