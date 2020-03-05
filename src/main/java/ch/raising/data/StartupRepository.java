package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StartupRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public StartupRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // TODO: implement repository logic
}