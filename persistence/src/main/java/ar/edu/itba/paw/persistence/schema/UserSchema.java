package ar.edu.itba.paw.persistence.schema;

public final class UserSchema {
    private UserSchema() {}

    public static final String TABLE_NAME = "users";
    public static final String ID = "user_id";
    public static final String USERNAME = "username";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
}
