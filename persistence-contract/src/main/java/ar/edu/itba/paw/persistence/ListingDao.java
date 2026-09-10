package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingFilter;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import java.util.List;
import java.util.Optional;

public interface ListingDao {

    Optional<Listing> getById(Long id);

    Listing create(String title, Price price, User creator, Product product,
                    Condition condition, boolean acceptsTrade, String description, List<Long> imageIds);
    List<Listing> search(ListingFilter filter);
    ListingStatus purchase(Long id, Long buyerId);

    List<Listing> getByCreatorId(Long creatorId);
}
