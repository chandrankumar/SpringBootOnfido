package com.onfido.idv.model;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IdentityCheckRequest class
 * 
 */

@Data
@AllArgsConstructor
@Builder
public class IdentityCheckRequest {

	@JsonProperty(value = "applicant_id")
	private UUID applicantId;
	
	@JsonProperty(value = "check_id")
	private UUID checkId;
}