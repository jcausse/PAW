package ar.edu.itba.paw.persistence.schema;

public final class OneTimePasswordSchema {
    private OneTimePasswordSchema() {}

    public static final String TABLE_NAME = "one_time_passwords";
    public static final String REQUESTER_ID = "requester_id";
    public static final String OTP_VALUE = "otp_value";
    public static final String CREATED_AT = "created_at";
}
