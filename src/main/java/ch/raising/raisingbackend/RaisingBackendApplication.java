package ch.raising.raisingbackend;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.api.client.json.jackson2.JacksonFactory;

@SpringBootApplication(scanBasePackages = {
	"ch.raising.filters", 
	"ch.raising.utils", 
	"ch.raising.config", 
	"ch.raising.services", 
	"ch.raising.models", 
	"ch.raising.controllers", 
	"ch.raising.raisingbackend", 
	"ch.raising.data"
})
public class RaisingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaisingBackendApplication.class, args);
	}
	
	@Bean("org.springframework.security.crypto.password.PasswordEncoder")
    public PasswordEncoder getEncoder() {
    	return new BCryptPasswordEncoder();
    }
	@Bean
	public JacksonFactory getJacksonFactory() {
		return new JacksonFactory();
	}
}
