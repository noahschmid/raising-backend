
package com.weekly.weekly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.weekly.weekly", "com.weekly.controllers", "com.weekly.data"})
public class WeeklyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeeklyApplication.class, args);
	}

}
