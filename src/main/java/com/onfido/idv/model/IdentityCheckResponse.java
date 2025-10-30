package com.onfido.idv.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IdentityCheckResponse class
 * 
 */
@Data
@AllArgsConstructor
@Builder
public class IdentityCheckResponse {

	@JsonProperty(value = "check_id")
	private UUID checkId;
	
	@JsonProperty(value = "applicant_id")
	private UUID applicantId;
	
	@JsonProperty(value = "created_date")
	private OffsetDateTime createdDate;
}
