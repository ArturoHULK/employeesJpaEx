package com.jarmdev.employeesjpa.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    //we can use in-memory users or database source: switch between these option by changing @Bean tag depending on selection

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        //Note: Spring Security has a default Database schema:
        // table users(username,password,enabled) and authorities(username,authority) check official doc for details
        // https://docs.spring.io/spring-security/reference/
        //return new JdbcUserDetailsManager(dataSource);

        //or we can use our own custom tables
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        //define query to retrieve a user by username
        manager.setUsersByUsernameQuery("select user_id, password, active from system_users where user_id=?");

        //define query to retrieve the authorities/roles by username
        manager.setAuthoritiesByUsernameQuery("select user_id, role from roles where user_id=?");

        return manager;
    }

    //@Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        // this is the format spring security uses for handling password {encodingAlgorithmId}encodedPassword
        //in this case {noop} means no operation so the password doesn't need to be encoded
        UserDetails john = User.builder()
                .username("john")
                .password("{noop}test123")
                .roles("EMPLOYEE")
                .build();

        UserDetails mary = User.builder()
                .username("mary")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER")
                .build();

        UserDetails susan = User.builder()
                .username("susan")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(john, mary, susan);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configure ->
                configure
                        .requestMatchers(HttpMethod.GET,"/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/h2-console/**").permitAll()
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() //for the swagger auto generated documentation
                        .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN"));

        //disable some basic rules of basic auth so swagger about those rules
        http.httpBasic(AbstractHttpConfigurer::disable);

        //use HTTP basic authentication
        http.httpBasic(Customizer.withDefaults());

        //disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()));

        //since H2 db console uses frames
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            //send 401 unauthorized status without triggering a basic auth
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            //removes the  WWW-Authenticate header to prevent browser popup
            response.setHeader("WWW-Authenticate", "");
            response.getWriter().write("{\"error\":\"Unauthorized access.\"}");
        };
    }
}
