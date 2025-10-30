package com.onfido.idv.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IdentityApplicantResponse class
 * 
 */
@Data
@AllArgsConstructor
@Builder
public class IdentityApplicantResponse {

	@JsonProperty(value = "applicant_id")
	private UUID applicantId;
	
	@JsonProperty(value = "sdk_token")
	private String sdkToken;
	
}
