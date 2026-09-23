package ar.edu.itba.paw.webapp.form.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private long maxSizeBytes;
    private String[] allowedTypes;
    private boolean optional;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.maxSizeBytes();
        this.allowedTypes = constraintAnnotation.allowedTypes();
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (optional && (file == null || file.isEmpty())) {
            return true;
        }
        return FileValidation.isValidFile(file, maxSizeBytes, allowedTypes);
    }
}