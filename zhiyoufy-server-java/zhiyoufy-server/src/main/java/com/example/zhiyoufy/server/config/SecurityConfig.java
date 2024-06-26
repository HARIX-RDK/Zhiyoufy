package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.server.security.UmsBearerTokenFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.logout().disable();
		http.requestCache().disable();
		http.securityContext().disable();
		http.sessionManagement().disable();

		http.removeConfigurer(DefaultLoginPageConfigurer.class);

		http.addFilterBefore(
				umsBearerTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		http.cors(withDefaults());

		http.authorizeHttpRequests()
				.mvcMatchers(HttpMethod.POST,
						"/zhiyoufy-api/v1/user/request-id-code",
						"/zhiyoufy-api/v1/user/register",
						"/zhiyoufy-api/v1/user/form-login",
						"/zhiyoufy-api/v1/user/virtual-login").permitAll()
				.mvcMatchers(HttpMethod.GET,
						"/zhiyoufy-api/v1/websocket-stomp").permitAll()
				.mvcMatchers("/actuator/**").permitAll()
				.anyRequest().authenticated();
	}

	@Bean
	public UmsBearerTokenFilter umsBearerTokenFilter() {
		return new UmsBearerTokenFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
