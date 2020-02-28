package ch.raising.data;

import java.util.List;

import org.springframework.stereotype.Repository;

import ch.raising.models.User;

@Repository
public interface IJdbcRepository {
	List<User> findALL();
}
