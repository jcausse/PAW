package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class ListingJdbcDao implements ListingDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ListingJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(ListingSchema.ID)
            .withTableName(ListingSchema.TABLE_NAME);
    }

    @Override
    public Optional<Listing> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Listing create(String title, Price price, Long creatorId) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ListingSchema.TITLE, title);
        values.put(ListingSchema.CREATOR_ID, creatorId);
        values.put(ListingSchema.PRICE, price);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Listing.builder()
            .id(key)
            .title(title)
            .creator(null) // TODO
            .price(price)
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Listing> ROW_MAPPER = (rs, rowNum) ->
        Listing.builder()
            .id(rs.getLong(ListingSchema.ID))
            .title(rs.getString(ListingSchema.TITLE))
            .price(new Price(rs.getLong(ListingSchema.PRICE)))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ListingSchema.ID,
            ListingSchema.TITLE,
            ListingSchema.PRICE
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            ListingSchema.TABLE_NAME +
            " WHERE " +
            ListingSchema.ID +
            " = ?";
    }
}
