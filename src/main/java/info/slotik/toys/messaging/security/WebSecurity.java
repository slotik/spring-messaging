package info.slotik.toys.messaging.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter
{
    public static final String AUTHORIZATION_TYPE = "Silly";

    @Value("${messages.base.path}")
    private String basePath;

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new SillyAuthenticationFilter(authenticationManager()))
            .authorizeRequests()
            .antMatchers(HttpMethod.GET).permitAll()
            .anyRequest().authenticated();
    }
}
