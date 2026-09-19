package ar.edu.itba.paw.persistence.schema;

public final class UserSchema {
    private UserSchema() {}

    public static final String TABLE_NAME = "users";
    public static final String ID = "user_id";
    public static final String USERNAME = "username";
    public static final String DISPLAY_NAME = "display_name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String IMAGE_ID = "image_id";
    public static final String JOINED_AT = "joined_at";
    public static final String EMAIL_VERIFIED_AT = "email_verified_at";
}
