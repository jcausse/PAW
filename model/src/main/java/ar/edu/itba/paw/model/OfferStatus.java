package ar.edu.itba.paw.model;

public enum OfferStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String status;

    OfferStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static OfferStatus fromString(String status) {
        for (OfferStatus s : values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }
}