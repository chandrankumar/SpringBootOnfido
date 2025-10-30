package com.onfido.idv.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.onfido.idv.config.OnfidoConstant;
import com.onfido.idv.exception.MicroserviceException;

/**
 * JWTTokenHolder class will generate the JWT Token
 * 
 */

@Component
public class JWTTokenHolder {

	private static final Logger logger = LoggerFactory.getLogger(JWTTokenHolder.class);

	@Value("${onfido.jwt.secret.key}")
	private String SECRET_KEY;

	@Value("${onfido.jwt.expiry.time}")
	private String expirationTime;

	public String generateJWTToken(String jwtToken, UUID applicantId) {

		if (jwtToken == null) {

			SignedJWT signedJWT = null;
			try {
				return createToken(applicantId, signedJWT);
			} catch (JOSEException e) {
				logger.error("Error in JWT token Generation:", e.getMessage());
				throw new MicroserviceException(OnfidoConstant.ERR_CODE_JWT001, OnfidoConstant.ERR_MESSAGE_JWT_TOKEN);
			}

		} else {
			try {

				JWSVerifier verifier = new MACVerifier(SECRET_KEY);
				SignedJWT signedJWT = SignedJWT.parse(jwtToken);
				JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

				if (new Date().after(claimsSet.getExpirationTime())) {
					logger.error("Error in parsing existing JWT Token:");
					throw new MicroserviceException(OnfidoConstant.ERR_CODE_JWT001,
							OnfidoConstant.ERR_MESSAGE_JWT_TOKEN_EXPIRY);
				}
				String applicantID = claimsSet.getStringClaim("applicantId");
				createToken(applicantId, signedJWT);

			} catch (JOSEException | ParseException e) {
				logger.error("Error in parsing existing JWT Token:", e.getMessage());
				throw new MicroserviceException(OnfidoConstant.ERR_CODE_JWT001, OnfidoConstant.ERR_MESSAGE_JWT_TOKEN);
			}

			return null;
		}

	}

	private String createToken(UUID applicantId, SignedJWT signedJWT) throws KeyLengthException, JOSEException {
		JWSSigner signer;
		signer = new MACSigner(SECRET_KEY);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject("onfido")
				.expirationTime(new Date(System.currentTimeMillis() + Integer.parseInt(expirationTime)))
				.claim("applicantId", applicantId).build();

		signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		signedJWT.sign(signer);
		return signedJWT.serialize();
	}

}
