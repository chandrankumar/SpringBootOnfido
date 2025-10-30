package com.onfido.idv.exception;

/**
 * Custom Microservice exception class
 * 
 */
public class MicroserviceException extends RuntimeException {

	private String errorCode;
	private String errorMessage;

	public MicroserviceException(String errorcode, String errorMessage) {
		this.errorCode = errorcode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	

}
