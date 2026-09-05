package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Override
    public Optional<Image> getById(Long id) {
        return imageDao.getById(id);
    }

    @Override
    @Transactional
    public Image create(String filename, String alt, String contentType, byte[] data) {
        return imageDao.create(filename, alt, contentType, data);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        imageDao.delete(id);
    }
}
