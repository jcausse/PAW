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

        // Reset model if brand is "Any" (empty string)
        if (form.getNewProductBrand() != null && form.getNewProductBrand().isBlank()) {
            form.setNewProductModel(null);
        }

        if (form.getStep() == 1) {
            if (form.getCategoryId() == null) {
                bindingResult.rejectValue("categoryId", "NotNull.listingForm.categoryId");
            }

            if (!bindingResult.hasErrors()) {
                form.setStep(2);
            }
        } else if (form.getStep() == 2) {
            if (form.getSubcategoryId() == null) {
                bindingResult.rejectValue("subcategoryId", "NotNull.listingForm.subcategoryId");
            }

            if (!bindingResult.hasErrors()) {
                form.setStep(3);
            }
        } else if (form.getStep() == 3) {
            if (form.getExistingProductId() == null) {
                bindingResult.rejectValue("existingProductId", "NotNull.listingForm.existingProductId");
            }

            if (!bindingResult.hasErrors()) {
                form.setStep(4);
            }
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

            var brand = form.getNewProductBrand();
            if (brand == null) brand = "";
            var model = form.getNewProductModel();
            if (model == null) model = "";
            mav.addObject("products", productService.getBySubcategoryBrandModel(form.getSubcategoryId(), brand, model));
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
