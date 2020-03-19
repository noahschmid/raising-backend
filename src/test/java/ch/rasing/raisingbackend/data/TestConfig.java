//package ch.rasing.raisingbackend.data;
//
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import ch.raising.data.BoardmemberRepository;
//
//
//@Configuration
//public class TestConfig {
//	
//	@Bean
//	@Profile("test")
//	public DataSource datasource() {
//		DriverManagerDataSource datasource = new DriverManagerDataSource();
//		datasource.setDriverClassName("org.postgresql.Driver");
//		datasource.setUrl("jdbc:postgresql://33384.hostserv.eu:5432/raising_test");
//		datasource.setUsername("raising");
//		datasource.setPassword("1209danu10k?");
//		return datasource;
//	}
//	
//	@Bean
//	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//	    return new JdbcTemplate(dataSource);
//	}
//	
//	@Bean
//	public BCryptPasswordEncoder encoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//}
