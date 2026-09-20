package ar.edu.itba.paw.persistence.schema;

public final class ProofOfPaymentSchema {

    private ProofOfPaymentSchema() {}

    public static final String TABLE_NAME = "proof_of_payments";
    public static final String ID = "proof_of_payment_id";
    public static final String FILENAME = "filename";
    public static final String ALT = "alt";
    public static final String CONTENT_TYPE = "content_type";
    public static final String DATA = "data";
}