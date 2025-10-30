package com.onfido.idv.model;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IdentityApplicantRequest class
 */
@Data
@AllArgsConstructor
@Builder
public class IdentityApplicantRequest {

	@NotNull(value = "Firstname cannot be null")
	@JsonProperty(value = "first_name")
	private String firstName;
	
	@NotNull(value = "Lastname cannot be null")
	@JsonProperty(value = "last_name")
	private String lastName;
	
	@NotNull(value = "Email cannot be null")
	@JsonProperty(value = "email")
	private String email;
	
}
