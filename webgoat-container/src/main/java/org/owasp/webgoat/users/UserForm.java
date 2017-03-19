package org.owasp.webgoat.users;

import lombok.Getter;
import lombok.Setter;

/**
 * @author nbaars
 * @since 3/19/17.
 */
@Getter
@Setter
public class UserForm {

    private String username;
    private String password;
    private String matchingPassword;
    private String agree;
}
