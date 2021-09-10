package com.amayorov.hostel.exception;

import com.amayorov.hostel.controller.CategoryController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = CategoryController.class)
@Slf4j
public class CategoryExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)   // for addNewCategory() method, when inserted categoryName is not listed in CategoryEnum
	public ResponseEntity<String> handleException(HttpMessageNotReadableException e) {
		log.error("HttpMessageNotReadableException, addNewCategory() method, {}", e.getMessage());
		return new ResponseEntity<>("This category-name doesn`t exist! \n" +
				"Please check the spelling and choose from: Apartment, Business, Deluxe, Duplex, Superior, Standard.",
				HttpStatus.BAD_REQUEST);
	}
}
