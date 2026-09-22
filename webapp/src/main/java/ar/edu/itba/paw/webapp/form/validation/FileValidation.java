package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/** Shared validation logic for file uploads. */
final class FileValidation {

    private FileValidation() {
    }

    /**
     * A file is valid when it is not empty, within the size limit, and has an allowed content type.
     */
    static boolean isValidFile(final MultipartFile file, final long maxSizeBytes, final String[] allowedTypes) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        if (file.getSize() > maxSizeBytes) {
            return false;
        }
        final String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        return Arrays.stream(allowedTypes).anyMatch(type -> {
            if (type.endsWith("/*")) {
                return contentType.startsWith(type.substring(0, type.length() - 1));
            }
            return type.equals(contentType);
        });
    }
}