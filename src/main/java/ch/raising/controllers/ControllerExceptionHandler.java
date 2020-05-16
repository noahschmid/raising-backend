package ch.raising.controllers;

import java.io.IOException;

import java.sql.SQLException;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidInteractionException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.MediaException;
import ch.raising.utils.NotAuthorizedException;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Foreign Key not found, are your tables up to date?", e.getMessage()));
	}
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handle(DataAccessException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("There is a Problem with the associated SQL statement, or nothing was found", e.getMessage()));
	}
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handle(SQLException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("There is a Problem with the associated SQL statement", e.getMessage()));
	}
	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity<ErrorResponse> handle(DatabaseOperationException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("There was a problem with the request on the database", e.getMessage()));
	}
	@ExceptionHandler(InvalidProfileException.class)
	public ResponseEntity<ErrorResponse> handle(InvalidProfileException e){
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse(e.getMessage(), e.getAccount()));
	}
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<ErrorResponse> handle(EmailNotFoundException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Email was not found", e.getMessage()));
	}
	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> handle(NotAuthorizedException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("You are not allowed to do that operation: ", e.getMessage()));
		
	}
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Something with the parameter went wrong", e.getMessage()));
		
	}
	@ExceptionHandler(SizeLimitExceededException.class)
	public ResponseEntity<?> handle(SizeLimitExceededException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(413).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Maximum uploadsize exceeded", e.getMessage()));
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handle(Exception e){
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("An unexpected Exception : " + e.getMessage(),  e));
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handle(IOException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("File malformed", e.getMessage()));
	}
	@ExceptionHandler(MediaException.class)
	public ResponseEntity<?> handle(MediaException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Media could not be added", e.getMessage()));
	}
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handle(BadCredentialsException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Login failed", e.getMessage()));
	}
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<?> handle(EmptyResultDataAccessException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("No Element found for specified id", e.getStackTrace()));
	}
	@ExceptionHandler(InvalidInteractionException.class)
	public ResponseEntity<?> handle(InvalidInteractionException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Your interactionrequest seems malformed", e.getMessage()));
	}
	@ExceptionHandler(Error.class)
	public ResponseEntity<?> handle(Error e){
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Threre was an Error: " + e.getMessage(), e));
	}
	@ExceptionHandler(IncorrectResultSizeDataAccessException.class)
	public ResponseEntity<?> handle(IncorrectResultSizeDataAccessException e){
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("There were more results than anticipated: " + e.getMessage(), e));
	}
	@ExceptionHandler(ClientAbortException.class)
	public ResponseEntity<?> handle(ClientAbortException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Connection reseted by peer: " + e.getMessage(), e));
	}
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handle(HttpRequestMethodNotSupportedException e){
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(405).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse("Method not supported: " + e.getMessage(), e));
	}
	
}
