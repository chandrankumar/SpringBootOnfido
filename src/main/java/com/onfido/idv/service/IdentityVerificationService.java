package com.onfido.idv.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.onfido.ApiException;
import com.onfido.api.DefaultApi;
import com.onfido.idv.config.OnfidoConstant;
import com.onfido.idv.exception.MicroserviceException;
import com.onfido.idv.jwt.JWTTokenHolder;
import com.onfido.idv.model.IdentityApplicantRequest;
import com.onfido.idv.model.IdentityApplicantResponse;
import com.onfido.idv.model.IdentityCheckRequest;
import com.onfido.idv.model.IdentityCheckResponse;
import com.onfido.idv.model.IdentityReportResponse;
import com.onfido.model.Applicant;
import com.onfido.model.ApplicantBuilder;
import com.onfido.model.Check;
import com.onfido.model.CheckBuilder;
import com.onfido.model.CheckStatus;
import com.onfido.model.Report;
import com.onfido.model.ReportName;
import com.onfido.model.SdkToken;
import com.onfido.model.SdkTokenBuilder;

@Service
public class IdentityVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(IdentityVerificationService.class);

	@Value("${onfido.report.name}")
	private List<ReportName> onfidoReportNames;

	@Value("${onfido.poll.delay.seconds}")
	private String delaySeconds;

	@Value("${onfido.poll.maxlimit}")
	private Integer maxlimitInSeconds;

	@Autowired
	private DefaultApi defaultApi;

	public IdentityApplicantResponse createApplicant(IdentityApplicantRequest applicantRequest)
			throws MicroserviceException {

		logger.info("IdentityVerificationService - createApplicant::start");
		ApplicantBuilder builder = new ApplicantBuilder();
		Applicant applicant = null;
		try {
			builder.firstName(applicantRequest.getFirstName()).lastName(applicantRequest.getLastName())
					.email(applicantRequest.getEmail());
			applicant = defaultApi.createApplicant(builder);
			logger.info("IdentityVerificationService - createApplicant:: applicantId: {}", applicant.getId());
			logger.info("IdentityVerificationService - createApplicant::end");
		} catch (ApiException e) {
			logger.error("Error in createApplicant:", e.getMessage());
			throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF001, OnfidoConstant.ERR_MESSAGE);
		}
		return IdentityApplicantResponse.builder().applicantId(applicant.getId()).sdkToken("token").build();
	}

	public IdentityCheckResponse createCheck(IdentityCheckRequest identityCheckRequest) throws MicroserviceException {

		logger.info("IdentityVerificationService - createCheck::start");
		logger.info("IdentityVerificationService - createCheck::onfidoReportNames: {}", onfidoReportNames);
		Check check = null;
		try {
			check = defaultApi.createCheck(new CheckBuilder().applicantId(identityCheckRequest.getApplicantId())
					.reportNames(onfidoReportNames));
			logger.info("IdentityVerificationService - createCheck:: check_id: {}", check.getId());
			logger.info("IdentityVerificationService - createCheck::end");
		} catch (ApiException e) {
			logger.error("Error in createCheck:", e.getMessage());
			throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF002, OnfidoConstant.ERR_MESSAGE);
		}
		return IdentityCheckResponse.builder().checkId(check.getId()).applicantId(check.getApplicantId())
				.createdDate(check.getCreatedAt()).build();
	}

	public IdentityReportResponse findReport(IdentityCheckRequest identityCheckRequest) throws MicroserviceException {

		logger.info("IdentityVerificationService - findReport::start");

		Check check;
		Optional<Report> result = java.util.Optional.empty();
		try {
			check = pollingRetrieveCheck(identityCheckRequest.getCheckId());
			if ("unknown_default_open_api".equalsIgnoreCase(check.getStatus().toString())) {
				logger.error("Polling shutdown throwing exception");
				throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF003, OnfidoConstant.ERR_MESSAGE);
			}
			List<Report> reports = check.getReportIds().stream().map(reportId -> {
				return retriveReport(reportId);
			}).collect(Collectors.toList());
			result = reports.stream().filter(report -> "complete".equalsIgnoreCase(report.getStatus().toString()))
					.findFirst();
			logger.info("IdentityVerificationService - findReport::status: {}", result.get().getStatus());
			logger.info("IdentityVerificationService - findReport:end");
		} catch (InterruptedException | ApiException e) {
			logger.error("Error in findReport by ApiException", e);
			throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF003, OnfidoConstant.ERR_MESSAGE);
		}

		return IdentityReportResponse.builder().reportId(result.get().getId())
				.reportStatus(result.get().getStatus().toString()).build();
	}

	private Check pollingRetrieveCheck(UUID checkId) throws ApiException, InterruptedException {

		Check check = new Check();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
		logger.info("Polling started retrive check API " + java.time.LocalTime.now());
		Runnable appTask = () -> {
			try {
				Check checkRes = defaultApi.findCheck(checkId);
				logger.info("Polling retrive check API delaySeconds " + java.time.LocalTime.now());
				// Polling will happen when changing status as complete to consider in below
				// condition
				if ("consider".equalsIgnoreCase(String.valueOf(checkRes.getStatus()))) {
					logger.info("Stopped polling retrieveCheck API successfully " + java.time.LocalTime.now());
					check.setId(checkRes.getId());
					check.setReportIds(checkRes.getReportIds());
					check.setResult(checkRes.getResult());
					scheduler.shutdownNow();
				}
			} catch (ApiException e) {
				logger.error("Error in pollingRetrieveCheck - check:", e.getMessage());
				throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF003, OnfidoConstant.ERR_MESSAGE);
			}
		};
		scheduler.scheduleWithFixedDelay(appTask, 0, Integer.valueOf(delaySeconds), TimeUnit.SECONDS);

		Runnable shutdownTask = () -> {
			scheduler.shutdown();
			check.status(CheckStatus.UNKNOWN_DEFAULT_OPEN_API);
			logger.error("polling shutdown successfully...");
		};
		scheduler.schedule(shutdownTask, Integer.valueOf(maxlimitInSeconds), TimeUnit.SECONDS);
		scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		return check;

	}

	public String generateSdkToken(UUID applicantId) throws MicroserviceException {

		SdkTokenBuilder builder = new SdkTokenBuilder();
		SdkToken sdkToken = null;
		try {
			logger.info("IdentityVerificationService - generateSdkToken::start");
			builder.applicantId(applicantId);
			sdkToken = defaultApi.generateSdkToken(builder);
			logger.info("IdentityVerificationService - generateSdkToken:: token: {}", sdkToken.getToken());
			logger.info("IdentityVerificationService - generateSdkToken::end");
		} catch (ApiException e) {
			logger.error("Error in generateSdkToken ", e.getMessage());
			throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF001, OnfidoConstant.ERR_MESSAGE);
		}
		return sdkToken.getToken();
	}

	private Report retriveReport(UUID reportId) throws MicroserviceException {
		logger.info("IdentityVerificationService - retriveReport::start");
		Report report = null;
		try {
			report = defaultApi.findReport(reportId);
			logger.info("IdentityVerificationService - retriveReport::reportId: {}", report.getId());
			logger.info("IdentityVerificationService - retriveReport::end");
		} catch (ApiException e) {
			logger.info("Error in IdentityVerificationService - retriveReport: ", e.getMessage());
			throw new MicroserviceException(OnfidoConstant.ERR_CODE_OF003, OnfidoConstant.ERR_MESSAGE);
		}
		return report;
	}

}
