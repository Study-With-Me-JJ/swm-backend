alter table business_verification_request
    add constraint business_verification_request_business_number_unique unique (business_number);
