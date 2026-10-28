package com.example.Dentmarc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Por ahora solo se muestran las paginas (primer avance), asi que se permite el acceso a todo.
@Configuration
public class SeguridadConfig {

	@Bean
	public SecurityFilterChain filtro(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		http.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
