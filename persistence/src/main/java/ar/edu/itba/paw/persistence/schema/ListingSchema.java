package ar.edu.itba.paw.persistence.schema;

public final class ListingSchema {

    private ListingSchema() {}

    public static final String TABLE_NAME = "listings";

    public static final String ID = "listing_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CREATOR_ID = "creator_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRICE = "price";
    public static final String STATUS = "status";
    public static final String CONDITION = "condition";
    public static final String ACCEPTS_TRADE = "accepts_trade";
}
