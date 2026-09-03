package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import java.util.Optional;

public interface ImageService {
    Optional<Image> getById(Long id);
    
    Image create(String filename, String alt, String contentType, byte[] data);
    
    void delete(Long id);
}
