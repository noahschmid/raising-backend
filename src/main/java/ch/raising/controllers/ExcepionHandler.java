package ch.raising.controllers;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ch.raising.models.responses.ErrorResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.NotAuthorizedException;

@ControllerAdvice
public class ExcepionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException e){
		return ResponseEntity.status(500).body(new ErrorResponse("Foreign Key not found, are your tables up to date?", e));
	}
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handle(DataAccessException e){
		return ResponseEntity.status(500).body(new ErrorResponse("The connection to The database might be faulty", e));
	}
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handle(SQLException e){
		return ResponseEntity.status(500).body(new ErrorResponse("There is a Problem with the associated SQL Statement", e));
	}
	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity<ErrorResponse> handle(DatabaseOperationException e){
		return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage(), e));
	}
	@ExceptionHandler(InvalidProfileException.class)
	public ResponseEntity<ErrorResponse> handle(InvalidProfileException e){
		return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage(), e));
	}
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<ErrorResponse> handle(EmailNotFoundException e){
		return ResponseEntity.status(500).body(new ErrorResponse("Email was not found", e));
	}
	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> handle(NotAuthorizedException e){
		return ResponseEntity.status(403).body(new ErrorResponse("You are not allowed to do that operation: " + e.getMessage(), e));
		
	}
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e){
		return ResponseEntity.status(500).body(new ErrorResponse("Something with the parameter went wrong", e));
		
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handle(Exception e){
		return ResponseEntity.status(500).body(new ErrorResponse("An unexpected Exception occured.", e));
	}
	
}
