package ar.edu.itba.paw.persistence.schema;

public final class RatingSchema {
    private RatingSchema() {}

    public static final String TABLE_NAME = "ratings";
    public static final String ID = "rating_id";
    public static final String CREATOR_ID = "creator_id";
    public static final String RATED_ID = "rated_id";
    public static final String OFFER_ID = "offer_id";
    public static final String ROLE = "role";
    public static final String TYPE = "type";
    public static final String REVIEW_TEXT = "review_text";
    public static final String CREATED_AT = "created_at";
}