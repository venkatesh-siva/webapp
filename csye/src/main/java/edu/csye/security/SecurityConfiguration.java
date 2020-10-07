package edu.csye.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired 
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.
	     authorizeRequests()
	    .antMatchers(HttpMethod.POST,"/v1/user").permitAll()
	    .antMatchers(HttpMethod.GET,"/v1/questions").permitAll()
	    .antMatchers(HttpMethod.GET,"/v1/question/{questionId}").permitAll()
	    .antMatchers(HttpMethod.GET,"/v1/user/{id}").permitAll()
	    .antMatchers(HttpMethod.GET,"/v1/question/{questionId}/answer/{answerId}").permitAll()
	    .anyRequest().authenticated()
	    .and()
	    .csrf().disable()
	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    .httpBasic();
	    
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().antMatchers(HttpMethod.POST,"/v1/user")
		.antMatchers(HttpMethod.POST,"/v1/user")
	    .antMatchers(HttpMethod.GET,"/v1/questions")
	    .antMatchers(HttpMethod.GET,"/v1/question/{questionId}")
	    .antMatchers(HttpMethod.GET,"/v1/user/{id}")
	    .antMatchers(HttpMethod.GET,"/v1/question/{questionId}/answer/{answerId}");
	}
	
	@Bean 
	public DaoAuthenticationProvider authProvider() {
		 DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		 provider.setPasswordEncoder( new BCryptPasswordEncoder() );
		 provider.setUserDetailsService(userDetailsService); 
		 return provider; 
	}
}
