package com.brocoding.stocks.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Created by Dave van Hooijdonk on 19-3-2018.
 *
 * Just a very basic security setup
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String admin;
    private final String adminPassword;
    private final String adminRole;
    private final String user;
    private final String userPassword;
    private final String userRole;

    public SecurityConfig(@Value("${stockapp.security.admin.name}") final String admin,
                          @Value("${stockapp.security.admin.password}") final String adminPassword,
                          @Value("${stockapp.security.admin.roles}") final String adminRole,
                          @Value("${stockapp.security.user.name}") final String user,
                          @Value("${stockapp.security.user.password}") final String userPassword,
                          @Value("${stockapp.security.user.roles}") final String userRole) {
        this.admin = admin;
        this.adminPassword = adminPassword;
        this.adminRole = adminRole;
        this.user = user;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(adminRole)
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/**").hasRole(adminRole)
                .antMatchers(HttpMethod.POST, "/**").hasRole(adminRole)
                .antMatchers(HttpMethod.GET, "/**").authenticated()
                .and()
                .httpBasic();
    }

    /**
     * withDefaultPasswordEncoder is deprecated and should not be used in real production code, use for example BCryptPasswordEncoder
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username(user).password(userPassword).roles(userRole).build());
        manager.createUser(User.withDefaultPasswordEncoder().username(admin).password(adminPassword).roles(adminRole).build());
        return manager;
    }
}
