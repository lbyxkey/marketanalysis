package indi.lby.marketanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigure{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //httpSecurity.authorizeRequests().antMatchers("concept/concepts").permitAll().anyRequest().authenticated()
        // .and();
        httpSecurity.authorizeRequests().antMatchers("/concept/**").permitAll();
        //httpSecurity.authorizeRequests().antMatchers("/login").permitAll();
        httpSecurity.authorizeRequests().anyRequest().authenticated();
        return httpSecurity.build();
    }
}
