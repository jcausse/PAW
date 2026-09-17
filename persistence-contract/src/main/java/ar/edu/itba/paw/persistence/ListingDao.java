package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingFilter;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import java.util.List;
import java.util.Optional;

public interface ListingDao {

    Optional<Listing> getById(Long id);

    Listing create(Listing listing);
    Page<Listing> search(ListingFilter filter);
    ListingStatus purchase(Long id, Long buyerId);
    Listing update(Long id, String title, Price price, Product product,
                   Condition condition, boolean acceptsTrade, String description);
    void cancel(Long id);
}
