package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.ValidFile;
import ar.edu.itba.paw.webapp.form.validator.ValidProofOfShippingUploadForm;
import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ValidProofOfShippingUploadForm
@NoArgsConstructor
@Getter
@Setter
public class ProofOfShippingUploadForm {

    private String trackingNumber;

    @ValidFile
    private MultipartFile file;
}