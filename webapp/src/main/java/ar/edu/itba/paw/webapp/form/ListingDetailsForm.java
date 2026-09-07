package ar.edu.itba.paw.webapp.form;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@NoArgsConstructor
@Getter
@Setter
public class ListingDetailsForm {
    private Long productId;
    private String title;
    private BigDecimal price;
    private String condition;
    private boolean acceptsTrade;
    private String description;
    private List<MultipartFile> images;
}
