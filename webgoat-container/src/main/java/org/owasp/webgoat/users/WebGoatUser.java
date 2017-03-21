package org.owasp.webgoat.users;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

/**
 * @author nbaars
 * @since 3/19/17.
 */
@Getter
@Entity
public class WebGoatUser {

    public static final String ROLE_USER = "WEBGOAT_USER";
    public static final String ROLE_ADMIN = "WEBGOAT_ADMIN";

    @Id
    private String username;
    private String password;
    private String role = ROLE_USER;

    protected WebGoatUser() {

    }

    public WebGoatUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(getRole()));

    }


}


