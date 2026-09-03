package ar.edu.itba.paw.webapp.form;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListingForm {

    private Integer step = 1;

    private Long categoryId;
    private Long subcategoryId;

    private Long existingProductId;
    private String newProductBrand;
    private String newProductModel;
    private Integer newProductYear;

    private Boolean isAutoSubmit = false;

    private Long previousCategoryId;
    private Long previousSubcategoryId;

    private String title;
    private BigDecimal price;

    public boolean isCategoryChanged() {
        return categoryId != null && !categoryId.equals(previousCategoryId);
    }

    public boolean isSubcategoryChanged() {
        return subcategoryId != null && !subcategoryId.equals(previousSubcategoryId);
    }

    public void updatePreviousValues() {
        this.previousCategoryId = this.categoryId;
        this.previousSubcategoryId = this.subcategoryId;
    }
}