package com.mosltyai.mostlytracker.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@ConfigurationProperties(prefix = "tracker")
@EnableWebSecurity
@Data
public class MostlyTrackerSecurityConfig {

  private Security security;
  private Swagger swagger;

  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(final PasswordEncoder encoder) {
    final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(
        User.withUsername(security.username)
            .password(encoder.encode(security.password))
            .roles(security.role)
            .build());

    manager.createUser(
        User.withUsername(swagger.username)
            .password(encoder.encode(swagger.getPassword()))
            .roles(swagger.getRole())
            .build());

    return manager;
  }

  @Configuration
  @ConfigurationProperties("tracker.security")
  @EqualsAndHashCode(callSuper = true)
  @RequiredArgsConstructor
  @Data
  @Order(1)
  static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      http.requestMatchers()
          .antMatchers("/project/**")
          .and()
          .httpBasic()
          .and()
          .csrf()
          .disable()
          .cors()
          .disable()
          .authorizeRequests()
          .anyRequest()
          .hasAnyRole("USER", "SWAGGER");
    }
  }

  @Configuration
  @ConfigurationProperties("tracker.swagger")
  @EqualsAndHashCode(callSuper = true)
  @RequiredArgsConstructor
  @Data
  @Order(2)
  static class SwaggerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      http.requestMatchers()
          .antMatchers("/swagger-ui/**")
          .and()
          .httpBasic()
          .and()
          .csrf()
          .disable()
          .cors()
          .disable()
          .authorizeRequests()
          .anyRequest()
          .hasRole("SWAGGER");
    }
  }

  @Data
  static class Security {
    private String username;
    private String password;
    private String role;
  }

  @Data
  static class Swagger {
    private String username;
    private String password;
    private String role;
  }
}
