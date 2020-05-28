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

/**
 * This class is used by SpringMVC to filter the exceptions that get propagated
 * through to the controllers and return custom {@link ErrorResponse} objects
 * and status codes depending on the error/exception. This class is used solely
 * by spring for any exception resolving at the end points.
 * 
 * @author manus
 *
 */
@ControllerAdvice
public class ControllerExceptionHandler {
	/**
	 * 
	 * @param DataIntegrityViolationException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Foreign Key not found, are your tables up to date?", e.getMessage()));
	}

	/**
	 * 
	 * @param DataAccessException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handle(DataAccessException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(new ErrorResponse(
				"There is a Problem with the associated SQL statement, or nothing was found", e.getMessage()));
	}

	/**
	 * 
	 * @param SQLException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handle(SQLException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("There is a Problem with the associated SQL statement", e.getMessage()));
	}

	/**
	 * 
	 * @param DatabaseOperationException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity<ErrorResponse> handle(DatabaseOperationException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("There was a problem with the request on the database", e.getMessage()));
	}

	/**
	 * 
	 * @param InvalidProfileException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(InvalidProfileException.class)
	public ResponseEntity<ErrorResponse> handle(InvalidProfileException e) {
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse(e.getMessage(), e.getAccount()));
	}

	/**
	 * 
	 * @param EmailNotFoundException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<ErrorResponse> handle(EmailNotFoundException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Email was not found", e.getMessage()));
	}

	/**
	 * 
	 * @param NotAuthorizedException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> handle(NotAuthorizedException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("You are not allowed to do that operation: ", e.getMessage()));

	}

	/**
	 * 
	 * @param MethodArgumentTypeMismatchException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Something with the parameter went wrong", e.getMessage()));

	}

	/**
	 * 
	 * @param SizeLimitExceededException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(SizeLimitExceededException.class)
	public ResponseEntity<?> handle(SizeLimitExceededException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(413).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Maximum uploadsize exceeded", e.getMessage()));
	}

	/**
	 * 
	 * @param Exception
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handle(Exception e) {
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("An unexpected Exception : " + e.getMessage(), e));
	}

	/**
	 * 
	 * @param IOException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handle(IOException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("File malformed", e.getMessage()));
	}

	/**
	 * 
	 * @param MediaException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(MediaException.class)
	public ResponseEntity<?> handle(MediaException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Media could not be added", e.getMessage()));
	}

	/**
	 * 
	 * @param BadCredentialsException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handle(BadCredentialsException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Login failed", e.getMessage()));
	}

	/**
	 * 
	 * @param EmptyResultDataAccessException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<?> handle(EmptyResultDataAccessException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("No Element found for specified id", e.getStackTrace()));
	}

	/**
	 * 
	 * @param InvalidInteractionException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(InvalidInteractionException.class)
	public ResponseEntity<?> handle(InvalidInteractionException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Your interactionrequest seems malformed", e.getMessage()));
	}

	/**
	 * 
	 * @param Error
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(Error.class)
	public ResponseEntity<?> handle(Error e) {
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Threre was an Error: " + e.getMessage(), e));
	}

	/**
	 * Is returned if a datatbase query is returns an unexpected result.
	 * 
	 * @param IncorrectResultSizeDataAccessException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(IncorrectResultSizeDataAccessException.class)
	public ResponseEntity<?> handle(IncorrectResultSizeDataAccessException e) {
		e.printStackTrace();
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("There were more results than anticipated: " + e.getMessage(), e));
	}

	/**
	 * This is not a fault of any code we wrote but of the client.
	 * 
	 * @param ClientAbortException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(ClientAbortException.class)
	public ResponseEntity<?> handle(ClientAbortException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Connection reseted by peer: " + e.getMessage(), e));
	}

	/**
	 * This is not a fault of any code we wrote but of the client.
	 * 
	 * @param HttpRequestMethodNotSupportedException
	 * @return {@link ErrorResponse}
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handle(HttpRequestMethodNotSupportedException e) {
		LoggerFactory.getILoggerFactory().getLogger(e.getClass().toString()).error(e.getMessage());
		return ResponseEntity.status(405).contentType(MediaType.APPLICATION_JSON)
				.body(new ErrorResponse("Method not supported: " + e.getMessage(), e));
	}

}
