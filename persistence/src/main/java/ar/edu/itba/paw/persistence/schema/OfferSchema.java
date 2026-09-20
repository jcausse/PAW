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
}