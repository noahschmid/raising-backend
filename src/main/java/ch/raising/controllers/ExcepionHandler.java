package ch.raising.controllers;

import java.io.IOException;

import java.sql.SQLException;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
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
import ch.raising.utils.MediaNotAddedException;
import ch.raising.utils.NotAuthorizedException;

@ControllerAdvice
public class ExcepionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("Foreign Key not found, are your tables up to date?", e.getMessage()));
	}
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handle(DataAccessException e){

		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("There is a Problem with the associated SQL statement, or nothing was found", e.getMessage()));
	}
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handle(SQLException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("There is a Problem with the associated SQL statement", e.getMessage()));
	}
	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity<ErrorResponse> handle(DatabaseOperationException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage(), e));
	}
	@ExceptionHandler(InvalidProfileException.class)
	public ResponseEntity<ErrorResponse> handle(InvalidProfileException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage(), e.getAccount()));
	}
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<ErrorResponse> handle(EmailNotFoundException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("Email was not found", e.getMessage()));
	}
	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> handle(NotAuthorizedException e){
		e.printStackTrace();
		return ResponseEntity.status(403).body(new ErrorResponse("You are not allowed to do that operation: ", e.getMessage()));
		
	}
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("Something with the parameter went wrong", e.getMessage()));
		
	}
	@ExceptionHandler(SizeLimitExceededException.class)
	public ResponseEntity<?> handle(SizeLimitExceededException e){
		e.printStackTrace();
		return ResponseEntity.status(413).body(new ErrorResponse("Maximum uploadsize exceeded", e.getMessage()));
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handle(Exception e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("An unexpected Exception : ." + e.getMessage(),  e));
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handle(IOException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("File malformed", e.getMessage()));
	}
	@ExceptionHandler(MediaNotAddedException.class)
	public ResponseEntity<?> handle(MediaNotAddedException e){
		e.printStackTrace();
		return ResponseEntity.status(500).body(new ErrorResponse("Media could not be added", e.getMessage()));
	}
	
}
