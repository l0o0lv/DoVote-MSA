package com.example.configserver.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean //비밀번호 암호화
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf((auth) -> auth.disable()); //csrf 비활성화
        http
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated()); //모든 요청에 대해 인증 필요
        http
                .httpBasic(Customizer.withDefaults()); //httpBasic 인증 사용
        return http.build();
    }

    //내부에 접근할 수 있는 아이디와 비밀번호를 생성해야함. security 영속성을 통해서.
    @Bean //인메모리 저장방식 사용
    public UserDetailsService userDetailsService(){
        UserDetails user = User.builder()
                .username("admin")
                .password(bCryptPasswordEncoder().encode("1234"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
