package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProofOfPaymentUploadForm {

    private MultipartFile file;
}