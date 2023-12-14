package pt.isec.pd.eventsManager.api.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import pt.isec.pd.eventsManager.api.repository.Data;
import pt.isec.pd.eventsManager.api.models.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (Data.getInstance().checkIfUserExists(username)) {
            System.out.println("user exists");
            User userAuth = Data.getInstance().authenticate(username, password);
            if (userAuth != null) {
                System.out.println("user authenticated");
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (userAuth.isAdmin()) authorities.add(new SimpleGrantedAuthority("ADMIN"));
                else authorities.add(new SimpleGrantedAuthority("USER"));
                return new UsernamePasswordAuthenticationToken(username, password, authorities);
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
