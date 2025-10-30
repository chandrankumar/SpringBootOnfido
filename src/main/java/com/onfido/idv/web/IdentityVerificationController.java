package com.onfido.idv.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.onfido.idv.exception.MicroserviceException;
import com.onfido.idv.jwt.JWTTokenHolder;
import com.onfido.idv.model.IdentityApplicantRequest;
import com.onfido.idv.model.IdentityApplicantResponse;
import com.onfido.idv.model.IdentityCheckRequest;
import com.onfido.idv.model.IdentityCheckResponse;
import com.onfido.idv.model.IdentityReportResponse;
import com.onfido.idv.service.IdentityVerificationService;

/**
 * IdentityVerificationController used to call onfido API
 * 
 */
@RestController
public class IdentityVerificationController implements IdentityVerficationPublicService {

	private static final Logger logger = LoggerFactory.getLogger(IdentityVerificationController.class);

	@Autowired
	private IdentityVerificationService identityVerificationService;

	@Autowired
	private JWTTokenHolder jWTTokenHolder;

	@Override
	public ResponseEntity<IdentityApplicantResponse> identityVerificationInitiate(
			IdentityApplicantRequest applicantRequest) throws MicroserviceException {

		logger.info("IdentityVerificationController - identityVerificationInitiate:: start");
		IdentityApplicantResponse applicantResponse = identityVerificationService.createApplicant(applicantRequest);
		String sdkToken = identityVerificationService.generateSdkToken(applicantResponse.getApplicantId());
		applicantResponse.setSdkToken(sdkToken);
		String jwtToken = jWTTokenHolder.generateJWTToken(null, applicantResponse.getApplicantId());
		HttpHeaders header = new HttpHeaders();
		header.set("jwtToken", jwtToken);
		logger.info("IdentityVerificationController - identityVerificationInitiate:: start");
		return new ResponseEntity<IdentityApplicantResponse>(applicantResponse, header, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<IdentityCheckResponse> identityVerificationCheck(IdentityCheckRequest identityCheckRequest)
			throws MicroserviceException {

		logger.info("IdentityVerificationController - identityVerificationCheck:: start");
		IdentityCheckResponse identityCheckResponse = identityVerificationService.createCheck(identityCheckRequest);
		logger.info("IdentityVerificationController - identityVerificationCheck:: end");
		return new ResponseEntity<IdentityCheckResponse>(identityCheckResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<IdentityReportResponse> evaluateResult(IdentityCheckRequest identityCheckRequest)
			throws MicroserviceException {

		logger.info("IdentityVerificationController - evaluateResult:: start");
		IdentityReportResponse identityReportResponse = identityVerificationService.findReport(identityCheckRequest);
		logger.info("IdentityVerificationController - evaluateResult:: end");
		return new ResponseEntity<IdentityReportResponse>(identityReportResponse, HttpStatus.OK);
	}

}
