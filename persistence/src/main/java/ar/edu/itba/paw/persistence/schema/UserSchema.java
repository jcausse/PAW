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
    public static final String SELLER_POSITIVE_RATINGS = "seller_positive_ratings";
    public static final String SELLER_NEUTRAL_RATINGS = "seller_neutral_ratings";
    public static final String SELLER_NEGATIVE_RATINGS = "seller_negative_ratings";
    public static final String BUYER_POSITIVE_RATINGS = "buyer_positive_ratings";
    public static final String BUYER_NEUTRAL_RATINGS = "buyer_neutral_ratings";
    public static final String BUYER_NEGATIVE_RATINGS = "buyer_negative_ratings";
    public static final String PROVINCE_ID = "province_id";
    public static final String LOCATION_DETAIL = "location_detail";
    public static final String PREFERRED_LANGUAGE = "preferred_language";
    public static final String SUSPENDED_AT = "suspended_at";
}
