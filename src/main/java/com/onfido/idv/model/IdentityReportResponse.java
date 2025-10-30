package com.onfido.idv.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IdentityReportResponse class
 * 
 */
@Data
@AllArgsConstructor
@Builder
public class IdentityReportResponse {

	@JsonProperty(value = "report_id")
	private UUID reportId;
	
	@JsonProperty(value = "report_status")
	private String reportStatus;
}
