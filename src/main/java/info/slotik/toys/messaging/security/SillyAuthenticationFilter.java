package info.slotik.toys.messaging.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static info.slotik.toys.messaging.security.WebSecurity.AUTHORIZATION_TYPE;

public class SillyAuthenticationFilter extends BasicAuthenticationFilter
{
    SillyAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException
    {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(AUTHORIZATION_TYPE))
        {
            String userIdCandidate = header
                .substring(AUTHORIZATION_TYPE.length())
                .trim();
            if (userIdCandidate.matches("\\w+"))
            {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userIdCandidate,
                    null,
                    Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
