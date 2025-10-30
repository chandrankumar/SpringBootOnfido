package com.onfido.idv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.onfido.ApiClient;
import com.onfido.api.DefaultApi;

/**
 * IdentityVerificationConfiguration configuration
 * 
 */
@Configuration
public class IdentityVerificationConfiguration {

	@Value("${onfido.api.token}")
	private String apiToken;

	@Value("${onfido.api.path}")
	private String basePath;

	@Bean
	public ApiClient apiClient() {

		ApiClient client = com.onfido.Configuration.getDefaultApiClient();
		client.setApiToken(apiToken);
		client.setBasePath(basePath);
		return client;
	}

	@Bean
	public DefaultApi defaultApi(ApiClient apiClient) {
		return new DefaultApi(apiClient);
	}

}
