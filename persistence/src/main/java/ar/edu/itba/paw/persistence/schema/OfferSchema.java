package ar.edu.itba.paw.persistence.schema;

public final class OfferSchema {

    private OfferSchema() {}

    public static final String TABLE_NAME = "offers";

    public static final String ID = "offer_id";
    public static final String LISTING_ID = "listing_id";
    public static final String BUYER_ID = "buyer_id";
    public static final String AMOUNT = "amount";
    public static final String IS_FULL_PRICE = "is_full_price";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String CREATED_AT = "created_at";
    public static final String PROOF_OF_PAYMENT_ID = "proof_of_payment_id";
    public static final String PROOF_OF_PAYMENT_FILENAME = "proof_of_payment_filename";
    public static final String PROOF_OF_PAYMENT_CONTENT_TYPE = "proof_of_payment_content_type";
    public static final String PROOF_OF_PAYMENT_SIZE = "proof_of_payment_size";
    public static final String PROOF_OF_SHIPPING_ID = "proof_of_shipping_id";
    public static final String PROOF_OF_SHIPPING_FILENAME = "proof_of_shipping_filename";
    public static final String PROOF_OF_SHIPPING_CONTENT_TYPE = "proof_of_shipping_content_type";
    public static final String PROOF_OF_SHIPPING_SIZE = "proof_of_shipping_size";
    public static final String TRACKING_NUMBER = "tracking_number";
    public static final String ACCEPTED_AT = "accepted_at";
    public static final String BUYER_RATING = "buyer_rating";
    public static final String SELLER_RATING = "seller_rating";
    public static final String OFFERED_LISTING_ID = "offered_listing_id";
}