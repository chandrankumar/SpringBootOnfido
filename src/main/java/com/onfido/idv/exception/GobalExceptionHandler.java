package com.onfido.idv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GobalExceptionHandler {

	@ExceptionHandler(exception = MicroserviceException.class)
	public ResponseEntity<ErrorResponse> handleException(MicroserviceException ex) {

		ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

	}
}
