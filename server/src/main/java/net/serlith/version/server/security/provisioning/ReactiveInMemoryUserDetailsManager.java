package net.serlith.version.server.security.provisioning;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@NullMarked
public class ReactiveInMemoryUserDetailsManager implements ReactiveUserDetailsService {

    private final ConcurrentMap<String, UserDetails> users = new ConcurrentHashMap<>();

    public ReactiveInMemoryUserDetailsManager() {
    }

    public ReactiveInMemoryUserDetailsManager(Collection<UserDetails> users) {
        for (UserDetails user : users) {
            this.createUser(user);
        }
    }

    public ReactiveInMemoryUserDetailsManager(UserDetails... users) {
        for (UserDetails user : users) {
            this.createUser(user);
        }
    }

    public ReactiveInMemoryUserDetailsManager(Properties users) {
        Enumeration<?> names = users.propertyNames();
        UserAttributeEditor editor = new UserAttributeEditor();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            editor.setAsText(users.getProperty(name));
            UserAttribute attr = (UserAttribute) editor.getValue();
            Assert.notNull(attr,
                    () -> "The entry with username '" + name + "' could not be converted to an UserDetails");
            this.createUser(createUserDetails(name, attr));
        }
    }

    private User createUserDetails(String name, UserAttribute attr) {
        return new User(name, attr.getPassword(), attr.isEnabled(), true, true, true, attr.getAuthorities());
    }

    public void createUser(UserDetails user) {
        Assert.isTrue(!this.userExists(user.getUsername()), "user should not exist");
        this.users.put(user.getUsername().toLowerCase(Locale.ROOT), user);
    }

    public boolean userExists(String username) {
        return this.users.containsKey(username.toLowerCase(Locale.ROOT));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserDetails user  = this.users.get(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            return Mono.error(UsernameNotFoundException.fromUsername(username));
        }
        if (user instanceof CredentialsContainer) {
            return Mono.just(user);
        }
        return Mono.just(new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities()));
    }

}
