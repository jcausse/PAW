package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.persistence.schema.OfferSchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class OfferJdbcDao implements OfferDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public OfferJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(OfferSchema.ID)
            .withTableName(OfferSchema.TABLE_NAME);
    }

    @Override
    public Optional<Offer> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public List<Offer> getByListingId(Long listingId) {
        return jdbcTemplate.query(Queries.GET_BY_LISTING_ID, ROW_MAPPER, listingId);
    }

    @Override
    public List<Offer> getByBuyerId(Long buyerId) {
        return jdbcTemplate.query(Queries.GET_BY_BUYER_ID, ROW_MAPPER, buyerId);
    }

    @Override
    public Offer create(Long listingId, Long buyerId, BigDecimal amount, Boolean isFullPrice, OfferStatus status, String message) {
        final Map<String, Object> values = new java.util.HashMap<>();
        values.put(OfferSchema.LISTING_ID, listingId);
        values.put(OfferSchema.BUYER_ID, buyerId);
        values.put(OfferSchema.AMOUNT, amount);
        values.put(OfferSchema.IS_FULL_PRICE, isFullPrice);
        values.put(OfferSchema.STATUS, status.getStatus());
        values.put(OfferSchema.MESSAGE, message);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return Offer.builder()
            .id(key)
            .listingId(listingId)
            .buyerId(buyerId)
            .amount(amount)
            .isFullPrice(isFullPrice)
            .status(status)
            .message(message)
            .build();
    }

    private static final RowMapper<Offer> ROW_MAPPER = (rs, rowNum) -> Offer.builder()
        .id(rs.getLong(OfferSchema.ID))
        .listingId(rs.getLong(OfferSchema.LISTING_ID))
        .buyerId(rs.getLong(OfferSchema.BUYER_ID))
        .amount(rs.getBigDecimal(OfferSchema.AMOUNT))
        .isFullPrice(rs.getBoolean(OfferSchema.IS_FULL_PRICE))
        .status(OfferStatus.fromString(rs.getString(OfferSchema.STATUS)))
        .buyerUsername(rs.getString(UserSchema.USERNAME))
        .buyerDisplayName(rs.getString(UserSchema.DISPLAY_NAME))
        .buyerImageId(Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
                        .map(Integer::longValue)
                        .orElse(null))
        .message(rs.getString(OfferSchema.MESSAGE))
        .build();

    private static final class Queries {
        private static final String BASE_SELECT =
            "SELECT " + OfferSchema.ID + ", " + OfferSchema.LISTING_ID + ", " + OfferSchema.BUYER_ID +
            ", " + OfferSchema.AMOUNT + ", " + OfferSchema.IS_FULL_PRICE + ", " + OfferSchema.STATUS +
            ", " + OfferSchema.MESSAGE +
            ", u." + UserSchema.USERNAME + ", u." + UserSchema.DISPLAY_NAME + ", u." + UserSchema.IMAGE_ID +
            " FROM " + OfferSchema.TABLE_NAME + " o" +
            " JOIN " + UserSchema.TABLE_NAME + " u ON u." + UserSchema.ID + " = o." + OfferSchema.BUYER_ID;

        private static final String GET_BY_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.ID + " = ?";

        private static final String GET_BY_LISTING_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.LISTING_ID + " = ?" +
            " ORDER BY o." + OfferSchema.ID + " DESC";

        private static final String GET_BY_BUYER_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.BUYER_ID + " = ?" +
            " ORDER BY o." + OfferSchema.ID + " DESC";

        private static final String UPDATE_STATUS =
            "UPDATE " + OfferSchema.TABLE_NAME +
            " SET " + OfferSchema.STATUS + " = ?" +
            " WHERE " + OfferSchema.ID + " = ?";
    }

    @Override
    public boolean updateStatus(Long offerId, OfferStatus status) {
        return jdbcTemplate.update(Queries.UPDATE_STATUS, status.getStatus(), offerId) > 0;
    }
}