package ch.raising.raisingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}
	
	@Bean("org.springframework.security.crypto.password.PasswordEncoder")
    public PasswordEncoder getEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
