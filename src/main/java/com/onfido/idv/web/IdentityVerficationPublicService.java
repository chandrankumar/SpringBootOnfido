package com.onfido.idv.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.onfido.idv.exception.MicroserviceException;
import com.onfido.idv.model.IdentityApplicantRequest;
import com.onfido.idv.model.IdentityApplicantResponse;
import com.onfido.idv.model.IdentityCheckRequest;
import com.onfido.idv.model.IdentityCheckResponse;
import com.onfido.idv.model.IdentityReportResponse;

/**
 * IdentityVerficationPublicService creating identity document verification API
 * request
 */
public interface IdentityVerficationPublicService {

	@PostMapping(value = "/api/identity-verification/initiate")
	public ResponseEntity<IdentityApplicantResponse> identityVerificationInitiate(
			@RequestBody IdentityApplicantRequest applicantRequest) throws MicroserviceException;

	@PostMapping(value = "/api/identity-verification/check")
	public ResponseEntity<IdentityCheckResponse> identityVerificationCheck(
			@RequestBody IdentityCheckRequest identityCheckRequest) throws MicroserviceException;

	@PostMapping(value = "/api/identity-verification/result")
	public ResponseEntity<IdentityReportResponse> evaluateResult(@RequestBody IdentityCheckRequest identityCheckRequest)
			throws MicroserviceException;

}
