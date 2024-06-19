package com.alexportfolio.webFace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // Configure HttpSecurity to only be applied to URLs that start       // with /
                .authorizeRequests(authz ->
                        authz
                                .requestMatchers(HttpMethod.POST,"/settings").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET,"/settings").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/").hasRole("ADMIN")

                                .anyRequest().permitAll()
                ).formLogin(form->{
                    form
                        .loginPage("/login")
                        .loginProcessingUrl("/login");
                });
        return http.formLogin(withDefaults()).csrf(c->c.disable()).build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

@Bean
PasswordEncoder passwordENcoder(){
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    return new BCryptPasswordEncoder(10) ;
}

}
