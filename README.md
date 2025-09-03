# SpringBootOnfido
Spring boot project integrated with Onfido for Identity verification document (KYC)

Payload details:
1. POST: /api/identity-verification/initiate
{
    "first_name":"chan",
    "last_name":"sri",
    "email":"chansri@in.com"
}

2. POST: /api/identity-verification/check
{
    "applicant_id":"{{applicant_id}}"
}

3. POST: /api/identity-verification/result
{
    "check_id":"{{check_id}}"
}

Step1: Hit the first API then will receive the applicantId and sdkToken in the response payload then edit the onfido.html and replace the sdkToken in onfidoApiToken field then hit the onfido.html file for KYC verification upload the dummy driving license and scan the face motion upload.

Step2: Hit the second API for creating the check
Step3: Hit the third API for finding the report result of uploaded documents.
