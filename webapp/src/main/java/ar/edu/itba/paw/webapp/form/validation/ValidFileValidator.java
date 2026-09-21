package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private long maxSizeBytes;
    private String[] allowedTypes;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.maxSizeBytes();
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
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