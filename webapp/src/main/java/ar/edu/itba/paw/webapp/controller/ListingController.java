package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import ar.edu.itba.paw.webapp.form.ListingForm;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/listing")
public class ListingController {

    private final ListingService listingService;
    private final ProductService productService;

    @GetMapping("/{id}")
    public ModelAndView listing(@PathVariable Long id) {
        return new ModelAndView("listing/index")
                .addObject("listing", listingService.getById(id));
    }

    @GetMapping("/new")
    public ModelAndView listingNew(@ModelAttribute("listingForm") ListingForm form) {
        form.setStep(1);
        return new ModelAndView("listing/new")
                .addObject("categories", productService.getAllCategories());
    }

    @PostMapping("/new")
    public ModelAndView listingNewPost(@Valid @ModelAttribute("listingForm") ListingForm form, BindingResult bindingResult) {
        var mav = new ModelAndView("listing/new");

        // Skip validation for auto-submits (triggered by field changes during form filling)
        var isAutoSubmit = Boolean.TRUE.equals(form.getIsAutoSubmit());
        if (isAutoSubmit) form.setIsAutoSubmit(false); // Reset for next request

        // Reset downstream fields when user goes back and changes a previous field
        var step = form.getStep();
        if (step > 1 && form.isCategoryChanged()) {
            // Category was changed - reset subcategory and product fields
            form.setSubcategoryId(null);
            form.setNewProductBrand(null);
            form.setNewProductModel(null);
            form.setNewProductYear(null);
            form.setStep(2);
        } else if (step > 2 && form.isSubcategoryChanged()) {
            // Subcategory was changed - reset product fields
            form.setNewProductBrand(null);
            form.setNewProductModel(null);
            form.setNewProductYear(null);
            form.setStep(3);
        }

        // Reset model if brand is "Any" (empty string)
        if (form.getNewProductBrand() != null && form.getNewProductBrand().isBlank()) {
            form.setNewProductModel(null);
        }

        if (form.getStep() == 1) {
            var hasCategory = form.getCategoryId() != null;
            if (!isAutoSubmit && !hasCategory) {
                bindingResult.rejectValue("categoryId", "NotNull.listingForm.categoryId");
            }

            if (!bindingResult.hasErrors() && hasCategory) {
                form.setStep(2);
                form.setSubcategoryId(null);
            }
        } else if (form.getStep() == 2) {
            var hasSubcategory = form.getSubcategoryId() != null;
            if (!isAutoSubmit && !hasSubcategory) {
                bindingResult.rejectValue("subcategoryId", "NotNull.listingForm.subcategoryId");
            }

            if (!bindingResult.hasErrors() && hasSubcategory) {
                form.setStep(3);
                form.setNewProductBrand(null);
                form.setNewProductModel(null);
                form.setNewProductYear(null);
            }
        } else if (form.getStep() == 3) {
            var hasBrand = form.getNewProductBrand() != null && !form.getNewProductBrand().isBlank();
            var hasModel = form.getNewProductModel() != null && !form.getNewProductModel().isBlank();
            var hasYear = form.getNewProductYear() != null;

            if (!isAutoSubmit && !hasBrand) {
                bindingResult.rejectValue("newProductBrand", "NotNull");
            }
            if (!isAutoSubmit && !hasModel) {
                bindingResult.rejectValue("newProductModel", "NotNull");
            }
            if (!isAutoSubmit && !hasYear) {
                bindingResult.rejectValue("newProductYear", "NotNull");
            }

            // Advance to step 4 if brand, model, and year are all selected (non-null and non-blank for strings)
            if (!bindingResult.hasErrors() && hasBrand && hasModel && hasYear) form.setStep(4);
        } else if (form.getStep() == 4) {
            if (form.getTitle() == null || form.getTitle().isBlank()) {
                bindingResult.rejectValue("title", "NotEmpty.listingForm.title");
            }
            if (form.getPrice() == null) {
                bindingResult.rejectValue("price", "NotNull.listingForm.price");
            }

            if (!bindingResult.hasErrors()) {
                var newListing = listingService.create(new ListingCreationDto(
                        form.getTitle(),
                        new Price(form.getPrice()),
                        getCurrentUserId(),
                        form.getExistingProductId()
                ));
                return new ModelAndView("redirect:/listing/" + newListing.getId());
            }
        }

        // Update previous values for next request
        form.updatePreviousValues();

        populateModel(mav, form);
        return mav;
    }

    private void populateModel(ModelAndView mav, ListingForm form) {
        mav.addObject("categories", productService.getAllCategories());

        if (form.getCategoryId() != null) {
            mav.addObject("subcategories", productService.getSubcategoriesByCategory(form.getCategoryId()));
        }

        if (form.getSubcategoryId() != null) {
            mav.addObject("brands", productService.getBrandsBySubcategory(form.getSubcategoryId()));
            List<String> models = List.of();
            if (form.getNewProductBrand() != null && !form.getNewProductBrand().isBlank()) {
                models = productService.getModelsBySubcategoryAndBrand(form.getSubcategoryId(), form.getNewProductBrand());
            }
            mav.addObject("models", models);
            mav.addObject("modelsEmpty", models.isEmpty());
        }
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails userDetails) {
            return userDetails.getDomainUser().getId();
        }
        throw new IllegalStateException("No authenticated user");
    }
}
