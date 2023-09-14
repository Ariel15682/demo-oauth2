package com.example.security.config;

import com.example.security.services.CustomOAuth2UserService;
import com.example.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class WebSecurityConfig {

//	private static final String[] AUTH_WHITE_LIST = {
//		"/", "/index/**", "index**",  "/login/**", "/login**", "login?continue", "/login?error=1", "/logout/**",
//		"/error/**", "/error**", "/error/401", "/error/403", "/error/404", "/error/500", "/401.html", "/403.html",
//		"/404.html", "/500.html", "/resources/**", "/static/**", "/templates/**", "/thymeleaf", "/webjars/**"
//    };

	@Value("${spring.h2.console.path}")
	private String h2ConsolePath;

	@Autowired
	private CustomOAuth2UserService userService;

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//				.inMemoryAuthentication()
//				.withUser("user1")
//				.password(passwordEncoder().encode("user1Pass"))
//				.authorities("ROLE_USER");
//	}

	@Bean
	protected SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf ->
                csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
			.authorizeHttpRequests((authorize) ->
				authorize
				  //.requestMatchers(AUTH_WHITE_LIST).permitAll()
				  //.requestMatchers(HttpMethod.GET, "/login" ).permitAll()
					.requestMatchers("/", "/index**", "/login**", "/login/**", "/error**", "/error/**",
							"/logout**", "/fragments.html", "/403").permitAll()
					.requestMatchers(antMatcher(h2ConsolePath + "/**")).permitAll()
					.anyRequest().authenticated())
			.formLogin((formLogin) ->
				formLogin
					.loginPage("/login").permitAll()
					.usernameParameter("email")
					.passwordParameter("pass")
					.defaultSuccessUrl("/page1"))
			.oauth2Login((oauthLogin) ->
				oauthLogin
					.loginPage("/login").permitAll()
					.userInfoEndpoint((userInfoEndpoint) ->
						userInfoEndpoint
							.userService(userService))
					.defaultSuccessUrl("/page1"))
		  //.rememberMe(Customizer.withDefaults())
			.logout(logout ->
				logout
					.logoutSuccessUrl("/")
				    .deleteCookies("jsessionid").permitAll())
		    .httpBasic(Customizer.withDefaults())
//			.exceptionHandling(exception -> exception
//				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
			.exceptionHandling((exception) ->
				exception
					.accessDeniedPage("/403"))
		  //.authenticationProvider(authenticationProvider());
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		return http.build();
	}

}
