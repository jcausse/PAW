package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidImagesValidator implements ConstraintValidator<ValidImages, List<MultipartFile>> {

    private long maxSizeBytes;

    @Override
    public void initialize(ValidImages constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.maxSizeBytes();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            if (file.getSize() > maxSizeBytes) {
                return false;
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return false;
            }
        }

        return true;
    }
}
