package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.persistence.schema.OfferSchema;
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
    public Offer create(Long listingId, Long buyerId, BigDecimal amount, Boolean isFullPrice, OfferStatus status) {
        final Map<String, Object> values = new java.util.HashMap<>();
        values.put(OfferSchema.LISTING_ID, listingId);
        values.put(OfferSchema.BUYER_ID, buyerId);
        values.put(OfferSchema.AMOUNT, amount);
        values.put(OfferSchema.IS_FULL_PRICE, isFullPrice);
        values.put(OfferSchema.STATUS, status.getStatus());

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return Offer.builder()
            .id(key)
            .listingId(listingId)
            .buyerId(buyerId)
            .amount(amount)
            .isFullPrice(isFullPrice)
            .status(status)
            .build();
    }

    private static final RowMapper<Offer> ROW_MAPPER = (rs, rowNum) -> Offer.builder()
        .id(rs.getLong(OfferSchema.ID))
        .listingId(rs.getLong(OfferSchema.LISTING_ID))
        .buyerId(rs.getLong(OfferSchema.BUYER_ID))
        .amount(rs.getBigDecimal(OfferSchema.AMOUNT))
        .isFullPrice(rs.getBoolean(OfferSchema.IS_FULL_PRICE))
        .status(OfferStatus.fromString(rs.getString(OfferSchema.STATUS)))
        .build();

    private static final class Queries {
        private static final String GET_BY_ID =
            "SELECT " + OfferSchema.ID + ", " + OfferSchema.LISTING_ID + ", " + OfferSchema.BUYER_ID +
            ", " + OfferSchema.AMOUNT + ", " + OfferSchema.IS_FULL_PRICE + ", " + OfferSchema.STATUS +
            " FROM " + OfferSchema.TABLE_NAME +
            " WHERE " + OfferSchema.ID + " = ?";

        private static final String GET_BY_LISTING_ID =
            "SELECT " + OfferSchema.ID + ", " + OfferSchema.LISTING_ID + ", " + OfferSchema.BUYER_ID +
            ", " + OfferSchema.AMOUNT + ", " + OfferSchema.IS_FULL_PRICE + ", " + OfferSchema.STATUS +
            " FROM " + OfferSchema.TABLE_NAME +
            " WHERE " + OfferSchema.LISTING_ID + " = ?" +
            " ORDER BY " + OfferSchema.ID + " DESC";

        private static final String GET_BY_BUYER_ID =
            "SELECT " + OfferSchema.ID + ", " + OfferSchema.LISTING_ID + ", " + OfferSchema.BUYER_ID +
            ", " + OfferSchema.AMOUNT + ", " + OfferSchema.IS_FULL_PRICE + ", " + OfferSchema.STATUS +
            " FROM " + OfferSchema.TABLE_NAME +
            " WHERE " + OfferSchema.BUYER_ID + " = ?" +
            " ORDER BY " + OfferSchema.ID + " DESC";
    }
}