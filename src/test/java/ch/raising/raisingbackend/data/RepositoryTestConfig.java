package ch.raising.raisingbackend.data;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.raising.data.BoardmemberRepository;


@Configuration
@Profile("RepositoryTest")
public class RepositoryTestConfig {
	
	
	@Bean("javax.sql.DataSource")
	public DataSource datasource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName("org.h2.Driver");
		datasource.setUrl("jdbc:h2:mem:arbitrarydbname;DB_CLOSE_DELAY=-1");
		datasource.setUsername("sa");
		datasource.setPassword("");
		return datasource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}

}
