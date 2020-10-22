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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

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
	    .antMatchers(HttpMethod.GET,"/v1/questions","/v1/question/{questionId}","/v1/user/{id}","/v1/question/{questionId}/answer/{answerId}").permitAll()
	    //.antMatchers(HttpMethod.GET,).permitAll()
	    //.antMatchers(HttpMethod.GET,).permitAll()
	    //.antMatchers(HttpMethod.GET,).permitAll()
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
		//.antMatchers(HttpMethod.POST,"/v1/user")
	    .antMatchers(HttpMethod.GET,"/v1/questions","/v1/question/{questionId}","/v1/user/{id}","/v1/question/{questionId}/answer/{answerId}");
	    //.antMatchers(HttpMethod.GET,)
	    //.antMatchers(HttpMethod.GET,)
	    //.antMatchers(HttpMethod.GET,);
	}
	
	@Bean 
	public DaoAuthenticationProvider authProvider() {
		 DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		 provider.setPasswordEncoder( new BCryptPasswordEncoder() );
		 provider.setUserDetailsService(userDetailsService); 
		 return provider; 
	}
}
