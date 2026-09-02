package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) {
        return imageService.getById(id)
                .map(image -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
