package indi.lby.marketanalysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfigure {

    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder getPW() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //httpSecurity.authorizeRequests().antMatchers("concept/concepts").permitAll().anyRequest().authenticated()
        // .and();
        //httpSecurity.authorizeRequests().antMatchers("/concept/**").permitAll();
        //httpSecurity.authorizeRequests().antMatchers("/stocks/**").permitAll();
        //httpSecurity.csrf().disable();
        httpSecurity.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        PasswordEncoder passwordEncoder= getPW();
        String password=passwordEncoder.encode("123456");
        log.info("123456加密结果为"+password);
        httpSecurity.apply(new DefaultLoginPageConfigurer<>());
//        httpSecurity.authorizeRequests().antMatchers("/login.html").permitAll();
        httpSecurity.authorizeRequests().anyRequest().authenticated();
        httpSecurity.formLogin().successHandler(myAuthenticationSuccessHandler).failureHandler(myAuthenticationFailureHandler);
        return httpSecurity.build();
    }
}
