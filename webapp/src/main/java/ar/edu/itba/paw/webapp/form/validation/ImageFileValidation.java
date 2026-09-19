package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

/** Shared validation logic for image uploads, used by the single- and multi-file image validators. */
final class ImageFileValidation {

    private ImageFileValidation() {
    }

    /**
     * A file is valid when it is empty/absent (optional upload), or when it is within the
     * size limit and has an {@code image/*} content type.
     */
    static boolean isValidImage(final MultipartFile file, final long maxSizeBytes) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        if (file.getSize() > maxSizeBytes) {
            return false;
        }
        final String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
