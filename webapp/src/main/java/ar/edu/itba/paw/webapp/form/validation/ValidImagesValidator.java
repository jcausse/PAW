package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

// Validates a list of uploaded images (e.g. the photos of a listing).
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
            if (!ImageFileValidation.isValidImage(file, maxSizeBytes)) {
                return false;
            }
        }
        return true;
    }
}
