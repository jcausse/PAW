package ar.edu.itba.paw.service.dto;
import ar.edu.itba.paw.model.Price;
import java.util.List;
public record ListingCreationDto(
    String title,
    Price price,
    Long creatorId,
    Long productId,
    String condition,
    boolean acceptsTrade,
    String description,
    List<ImageData> images
) {}
