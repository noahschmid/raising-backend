package ch.raising.raisingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

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
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RaisingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaisingBackendApplication.class, args);
	}
}
