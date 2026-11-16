package com.example.Dentmarc.config;

import com.example.Dentmarc.service.AdminUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Dentmarc.api.JwtAuthenticationFilter;
import com.example.Dentmarc.api.ApiSecurityErrorHandler;

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
	@Order(1)
	public SecurityFilterChain apiFiltro(HttpSecurity http,
	                                     JwtAuthenticationFilter jwtAuthenticationFilter,
	                                     ApiSecurityErrorHandler apiSecurityErrorHandler) throws Exception {
		http.securityMatcher("/api/**")
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(apiSecurityErrorHandler)
						.accessDeniedHandler(apiSecurityErrorHandler))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/v1/auth/**", "/api/v1/products/**", "/api/v1/categories/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	@Order(2)
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
