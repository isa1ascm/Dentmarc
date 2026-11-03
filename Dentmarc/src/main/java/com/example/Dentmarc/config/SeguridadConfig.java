package com.example.Dentmarc.config;

import com.example.Dentmarc.service.AdminUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// El panel (/admin/**) solo lo ve un administrador con sesion activa; el resto de la tienda sigue publica.
// CSRF queda habilitado (por defecto), asi que los formularios deben usar th:action.
@Configuration
public class SeguridadConfig {

	@Autowired
	private AdminUserDetailsService adminUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filtro(HttpSecurity http) throws Exception {
		http.userDetailsService(adminUserDetailsService);

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/admin/login").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
				.anyRequest().permitAll());

		http.formLogin(form -> form
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.defaultSuccessUrl("/admin/resumen", true)
				.failureUrl("/admin/login?error")
				.permitAll());

		http.logout(logout -> logout
				.logoutUrl("/admin/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessUrl("/admin/login?logout"));

		return http.build();
	}

}
