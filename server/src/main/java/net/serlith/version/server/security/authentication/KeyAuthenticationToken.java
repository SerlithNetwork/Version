package net.serlith.version.server.security.authentication;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

@NullMarked
public class KeyAuthenticationToken extends AbstractAuthenticationToken {

    private final @Nullable String principal;
    private final String token;

    public KeyAuthenticationToken(String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = null;
        this.token = token;
        this.setAuthenticated(false);
    }

    public KeyAuthenticationToken(final String principal, final String token) {
        super(List.of());
        this.principal = principal;
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public @Nullable String getPrincipal() {
        return this.principal;
    }

    @Override
    public String getCredentials() {
        return this.token;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public boolean allowsSoftware(final String software) {
        return this.principal != null && this.principal.equalsIgnoreCase(software);
    }

}
