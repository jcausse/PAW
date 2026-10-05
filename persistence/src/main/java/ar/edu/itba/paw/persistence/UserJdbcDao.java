package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.Province;
import ar.edu.itba.paw.persistence.schema.ProvinceSchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(UserSchema.ID)
            .withTableName(UserSchema.TABLE_NAME);
    }

    @Override
    public Optional<User> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate
            .query(Queries.GET_BY_USERNAME, ROW_MAPPER, username)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate
            .query(Queries.GET_BY_EMAIL, ROW_MAPPER, email)
            .stream()
            .findFirst();
    }

    @Override
    public User create(
            String username,
            String displayName,
            String email,
            String password,
            Image image,
            Instant joinedAt,
            Language preferredLanguage
    ) {
        final Long imageId = image != null ? image.getId() : null;

        final Map<String, Object> values = new HashMap<>();
        values.put(UserSchema.USERNAME, username);
        values.put(UserSchema.DISPLAY_NAME, displayName);
        values.put(UserSchema.EMAIL, email);
        values.put(UserSchema.PASSWORD, password);
        values.put(UserSchema.IMAGE_ID, imageId);
        values.put(UserSchema.JOINED_AT, Timestamp.from(joinedAt));
        values.put(UserSchema.SELLER_POSITIVE_RATINGS, 0);
        values.put(UserSchema.SELLER_NEUTRAL_RATINGS, 0);
        values.put(UserSchema.SELLER_NEGATIVE_RATINGS, 0);
        values.put(UserSchema.BUYER_POSITIVE_RATINGS, 0);
        values.put(UserSchema.BUYER_NEUTRAL_RATINGS, 0);
        values.put(UserSchema.BUYER_NEGATIVE_RATINGS, 0);
        values.put(UserSchema.PREFERRED_LANGUAGE, preferredLanguage.getCode());

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return User.builder()
                .id(key)
                .username(username)
                .displayName(displayName)
                .email(email)
                .password(password)
                .imageId(imageId)
                .joinedAt(joinedAt)
                .emailVerifiedAt(null)
                .sellerPositiveRatings(0)
                .sellerNeutralRatings(0)
                .sellerNegativeRatings(0)
                .buyerPositiveRatings(0)
                .buyerNeutralRatings(0)
                .buyerNegativeRatings(0)
                .preferredLanguage(preferredLanguage)
                .build();
    }

    @Override
    public Optional<User> update(Long userId, String displayName, String email, String password, Long imageId) {
        var setClauses = new ArrayList<String>();
        var params = new ArrayList<>();

        if (displayName != null) {
            setClauses.add(UserSchema.DISPLAY_NAME + " = ?");
            params.add(displayName);
        }
        if (email != null) {
            setClauses.add(UserSchema.EMAIL + " = ?");
            params.add(email);
        }
        if (password != null) {
            setClauses.add(UserSchema.PASSWORD + " = ?");
            params.add(password);
        }
        if (imageId != null) {
            setClauses.add(UserSchema.IMAGE_ID + " = ?");
            params.add(imageId);
        }

        if (setClauses.isEmpty()) {
            return Optional.empty();
        }

        var sql = "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + String.join(", ", setClauses) +
            " WHERE " + UserSchema.ID + " = ?";
        params.add(userId);

        var rowsAffected = jdbcTemplate.update(sql, params.toArray());

        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public Optional<User> verifyEmail(Long userId, Instant verifiedAt) {
        var rowsAffected = jdbcTemplate.update(
                Queries.VERIFY_EMAIL,
                Timestamp.from(verifiedAt),
                userId
        );

        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public Optional<User> suspend(Long userId, Instant suspendedAt) {
        var rowsAffected = jdbcTemplate.update(
                Queries.SUSPEND,
                Timestamp.from(suspendedAt),
                userId
        );

        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public Optional<User> unsuspend(Long userId) {
        var rowsAffected = jdbcTemplate.update(
                Queries.UNSUSPEND,
                userId
        );

        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public Optional<User> updateLocation(Long userId, Long provinceId, String locationDetail) {
        final int rowsAffected = jdbcTemplate.update(Queries.UPDATE_LOCATION, provinceId, locationDetail, userId);
        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public Optional<User> updatePreferredLanguage(Long userId, Language preferredLanguage) {
        final int rowsAffected = jdbcTemplate.update(Queries.UPDATE_PREFERRED_LANGUAGE, preferredLanguage.getCode(), userId);
        return rowsAffected == 0 ? Optional.empty() : getById(userId);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return jdbcTemplate.queryForObject(
                Queries.IS_USERNAME_TAKEN,
                Boolean.class,
                username
        );
    }

    @Override
    public boolean isEmailTaken(String email) {
        return jdbcTemplate.queryForObject(
                Queries.IS_EMAIL_TAKEN,
                Boolean.class,
                email
        );
    }

    private static String sellerCounterColumn(OfferRating rating) {
        return switch (rating) {
            case POSITIVE -> UserSchema.SELLER_POSITIVE_RATINGS;
            case NEUTRAL -> UserSchema.SELLER_NEUTRAL_RATINGS;
            case NEGATIVE -> UserSchema.SELLER_NEGATIVE_RATINGS;
        };
    }

    private static String buyerCounterColumn(OfferRating rating) {
        return switch (rating) {
            case POSITIVE -> UserSchema.BUYER_POSITIVE_RATINGS;
            case NEUTRAL -> UserSchema.BUYER_NEUTRAL_RATINGS;
            case NEGATIVE -> UserSchema.BUYER_NEGATIVE_RATINGS;
        };
    }

    @Override
    public void incrementSellerRatingCounter(Long userId, OfferRating rating) {
        final String column = sellerCounterColumn(rating);
        jdbcTemplate.update(
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + column + " = " + column + " + 1" +
            " WHERE " + UserSchema.ID + " = ?",
            userId
        );
    }

    @Override
    public void incrementBuyerRatingCounter(Long userId, OfferRating rating) {
        final String column = buyerCounterColumn(rating);
        jdbcTemplate.update(
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + column + " = " + column + " + 1" +
            " WHERE " + UserSchema.ID + " = ?",
            userId
        );
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong(UserSchema.ID))
            .username(rs.getString(UserSchema.USERNAME))
            .displayName(rs.getString(UserSchema.DISPLAY_NAME))
            .email(rs.getString(UserSchema.EMAIL))
            .password(rs.getString(UserSchema.PASSWORD))
            .imageId(
                    Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
                            .map(Integer::longValue)
                            .orElse(null)
            )
            .joinedAt(rs.getTimestamp(UserSchema.JOINED_AT).toInstant())
            .emailVerifiedAt(
                    Optional.ofNullable(rs.getTimestamp(UserSchema.EMAIL_VERIFIED_AT))
                            .map(Timestamp::toInstant)
                            .orElse(null)
            )
            .suspendedAt(
                    Optional.ofNullable(rs.getTimestamp(UserSchema.SUSPENDED_AT))
                            .map(Timestamp::toInstant)
                            .orElse(null)
            )
            .sellerPositiveRatings(rs.getInt(UserSchema.SELLER_POSITIVE_RATINGS))
            .sellerNeutralRatings(rs.getInt(UserSchema.SELLER_NEUTRAL_RATINGS))
            .sellerNegativeRatings(rs.getInt(UserSchema.SELLER_NEGATIVE_RATINGS))
            .buyerPositiveRatings(rs.getInt(UserSchema.BUYER_POSITIVE_RATINGS))
            .buyerNeutralRatings(rs.getInt(UserSchema.BUYER_NEUTRAL_RATINGS))
            .buyerNegativeRatings(rs.getInt(UserSchema.BUYER_NEGATIVE_RATINGS))
            .province(mapProvince(rs))
            .locationDetail(rs.getString(UserSchema.LOCATION_DETAIL))
            .preferredLanguage(Language.fromCode(rs.getString(UserSchema.PREFERRED_LANGUAGE)))
            .build();

    private static Province mapProvince(final java.sql.ResultSet rs) throws java.sql.SQLException {
        final Integer provinceId = rs.getObject(UserSchema.PROVINCE_ID, Integer.class);
        if (provinceId == null) {
            return null;
        }
        return Province.builder()
            .id(provinceId.longValue())
            .name(rs.getString("province_name"))
            .build();
    }

    private static final class Queries {

        private static final String FIELDS = String.join(", ",
            "u." + UserSchema.ID,
            "u." + UserSchema.USERNAME,
            "u." + UserSchema.DISPLAY_NAME,
            "u." + UserSchema.EMAIL,
            "u." + UserSchema.PASSWORD,
            "u." + UserSchema.IMAGE_ID,
            "u." + UserSchema.JOINED_AT,
            "u." + UserSchema.EMAIL_VERIFIED_AT,
            "u." + UserSchema.SUSPENDED_AT,
            "u." + UserSchema.SELLER_POSITIVE_RATINGS,
            "u." + UserSchema.SELLER_NEUTRAL_RATINGS,
            "u." + UserSchema.SELLER_NEGATIVE_RATINGS,
            "u." + UserSchema.BUYER_POSITIVE_RATINGS,
            "u." + UserSchema.BUYER_NEUTRAL_RATINGS,
            "u." + UserSchema.BUYER_NEGATIVE_RATINGS,
            "u." + UserSchema.PROVINCE_ID,
            "u." + UserSchema.LOCATION_DETAIL,
            "u." + UserSchema.PREFERRED_LANGUAGE,
            "pr." + ProvinceSchema.NAME + " AS province_name"
        );

        private static final String BASE_FROM =
            " FROM " + UserSchema.TABLE_NAME + " AS u" +
            " LEFT JOIN " + ProvinceSchema.TABLE_NAME + " AS pr" +
            " ON pr." + ProvinceSchema.ID + " = u." + UserSchema.PROVINCE_ID;

        private static final String GET_BY_ID =
            "SELECT " + FIELDS + BASE_FROM +
            " WHERE u." + UserSchema.ID + " = ?";

        private static final String GET_BY_USERNAME =
            "SELECT " + FIELDS + BASE_FROM +
            " WHERE u." + UserSchema.USERNAME + " = ?";

        private static final String GET_BY_EMAIL =
            "SELECT " + FIELDS + BASE_FROM +
            " WHERE u." + UserSchema.EMAIL + " = ?";

        private static final String UPDATE_LOCATION =
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + UserSchema.PROVINCE_ID + " = ?, " + UserSchema.LOCATION_DETAIL + " = ?" +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String UPDATE_PREFERRED_LANGUAGE =
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + UserSchema.PREFERRED_LANGUAGE + " = ?" +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String VERIFY_EMAIL =
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + UserSchema.EMAIL_VERIFIED_AT + " = ?" +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String SUSPEND =
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + UserSchema.SUSPENDED_AT + " = ?" +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String UNSUSPEND =
            "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + UserSchema.SUSPENDED_AT + " = NULL" +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String IS_USERNAME_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.USERNAME + " = ?)";

        private static final String IS_EMAIL_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.EMAIL + " = ?)";
    }
}
