package com.amayorov.hostel.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class NullEmptyExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)  // for any field from DTOs except those that can be null in some requests
	public ResponseEntity<String> handleException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException, {}", e.getMessage());
		return new ResponseEntity<>("One of the fields is null or empty, pls check!", HttpStatus.BAD_REQUEST);
	}
}
