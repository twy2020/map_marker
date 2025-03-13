package com.map.map_marker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 配置授权请求
                .authorizeHttpRequests(authorize -> authorize
                        // 使用 requestMatchers 替代 antMatchers
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 配置自定义登录页面
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/map", true)
                        .permitAll()
                )
                // 配置注销
                .logout(LogoutConfigurer::permitAll
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 定义一个内存中的用户，生产环境请使用加密密码存储
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345678")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
