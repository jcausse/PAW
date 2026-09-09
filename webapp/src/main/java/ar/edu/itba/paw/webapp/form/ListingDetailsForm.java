package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.ValidImages;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class ListingDetailsForm {

    @NotNull
    private Long productId;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal price;

    @ValidImages
    private List<MultipartFile> images;

    private String condition;
    private boolean acceptsTrade;
    private String description;
}
