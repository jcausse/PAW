package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// Validates a single uploaded image (e.g. a user's profile picture).
public class ValidImageValidator implements ConstraintValidator<ValidImages, MultipartFile> {

    private long maxSizeBytes;

    @Override
    public void initialize(ValidImages constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.maxSizeBytes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return ImageFileValidation.isValidImage(file, maxSizeBytes);
    }
}
