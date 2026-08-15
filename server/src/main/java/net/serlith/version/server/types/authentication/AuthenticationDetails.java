package net.serlith.version.server.types.authentication;

import net.serlith.version.server.types.management.PrivilegedUserDetails;
import net.serlith.version.server.types.token.ExpirableToken;

public record AuthenticationDetails(PrivilegedUserDetails user, ExpirableToken access, ExpirableToken refresh) {
}
