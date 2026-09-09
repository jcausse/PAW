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
}