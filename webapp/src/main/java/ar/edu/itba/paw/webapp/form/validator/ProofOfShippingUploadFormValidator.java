package ar.edu.itba.paw.webapp.form.validator;

import ar.edu.itba.paw.webapp.form.ProofOfShippingUploadForm;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProofOfShippingUploadFormValidator implements ConstraintValidator<ValidProofOfShippingUploadForm, ProofOfShippingUploadForm> {

    @Override
    public boolean isValid(ProofOfShippingUploadForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return false;
        }
        
        boolean hasTrackingNumber = form.getTrackingNumber() != null && !form.getTrackingNumber().isBlank();
        boolean hasFile = form.getFile() != null && !form.getFile().isEmpty();
        
        return hasTrackingNumber || hasFile;
    }
}