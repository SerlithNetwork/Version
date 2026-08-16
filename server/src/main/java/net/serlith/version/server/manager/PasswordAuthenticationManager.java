package net.serlith.version.server.manager;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

@NullMarked
public class PasswordAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    public PasswordAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

}
