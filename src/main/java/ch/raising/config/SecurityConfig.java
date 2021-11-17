package ch.raising.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ch.raising.services.AccountService;
import ch.raising.utils.JwtRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 
 * @author noahs, manus
 * This class contains the configuration for Spring Security. 
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService);
    }

    /**
     * Configures the Filters inserted before a request is handled by the controllers @see ch.raising.controllers
     */
    @Override 
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
        .disable()
        .cors()
        .configurationSource(corsConfigurationSource())
        .and()
        .authorizeRequests()
        .antMatchers("/profile/*/*")
        .permitAll()
        .antMatchers("/public/*")
        .permitAll()
        .antMatchers("/investor/register")
        .permitAll()
        .antMatchers("/startup/register")
        .permitAll()
        .antMatchers("/account/login")
        .permitAll()
        .antMatchers("/account/valid")
        .permitAll()
        .antMatchers("/account/register")
        .permitAll()
        .antMatchers("/account/forgot")
        .permitAll()
        .antMatchers("/account/reset")
        .permitAll()
        .antMatchers("/media/**")
        .permitAll()
        .antMatchers("/subscription/android/notify")
        .permitAll()
        .antMatchers("/subscription/ios/notify")
        .permitAll()
        .antMatchers("/admin/login")
        .permitAll()
        .antMatchers("/admin/*")
        .hasRole("ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http
        	.requiresChannel()
        	.anyRequest();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowCredentials(true);
        //the below three lines will add the relevant CORS response headers
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}