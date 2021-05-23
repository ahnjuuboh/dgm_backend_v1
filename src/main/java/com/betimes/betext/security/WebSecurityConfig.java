package com.betimes.betext.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/v1.0/users/**").permitAll()
                .antMatchers("/v1.0/schedule").hasRole("USER")
                .antMatchers("/v1.0/schedule/**").hasRole("USER")
                .antMatchers("/v1.0/fb/page/search").hasRole("USER")
                .antMatchers("/v1.0/source").hasRole("USER")
                .antMatchers("/v1.0/source/**").hasRole("USER")
                .antMatchers("/v1.0/topic").hasRole("USER")
                .antMatchers("/v1.0/topic/**").hasRole("USER")
                .antMatchers("/v1.0/fetch/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new FilterCheck(),
                        UsernamePasswordAuthenticationFilter.class);
    }
}
