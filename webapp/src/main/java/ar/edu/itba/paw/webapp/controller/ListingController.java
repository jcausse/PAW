package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.form.ChooseProductForm;
import ar.edu.itba.paw.webapp.form.ListingDetailsForm;
import ar.edu.itba.paw.webapp.form.ListingFilterForm;
import ar.edu.itba.paw.webapp.form.SelectOption;
import ar.edu.itba.paw.webapp.form.StringSelectOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/listing")
public class ListingController {

    private static final String OTHER_VALUE = "__OTHER__";

    private final ListingService listingService;
    private final ProductService productService;
    private final MessageSource messageSource;

    @GetMapping
    public ModelAndView discovery(@ModelAttribute("filterForm") ListingFilterForm filterForm) {
        final var filter = new ListingFilterDto(
            filterForm.getCategoryId(),
            filterForm.getSubcategoryId(),
            filterForm.getMinPrice(),
            filterForm.getMaxPrice(),
            filterForm.getCondition(),
            filterForm.getAcceptsTrade(),
            filterForm.getQuery(),
            filterForm.getSort(),
            null,
            ListingStatus.ACTIVE.getStatus()
        );

        final var mav = new ModelAndView("listing/discovery");
        mav.addObject("listings", listingService.search(filter));

        // Create translated condition options for paw:formSelect
        var conditionOptions = new java.util.ArrayList<StringSelectOption>();
        for (var c : Condition.values()) {
            conditionOptions.add(new StringSelectOption(
                c.name(),
                messageSource.getMessage("condition." + c.name(), null, LocaleContextHolder.getLocale())
            ));
        }
        mav.addObject("conditionOptions", conditionOptions);

        // Create translated sort options for paw:formSelect
        var sortOptions = new java.util.ArrayList<StringSelectOption>();
        for (var s : ListingSort.values()) {
            sortOptions.add(new StringSelectOption(
                s.getKey(),
                messageSource.getMessage("discovery.sort." + s.getKey(), null, LocaleContextHolder.getLocale())
            ));
        }
        mav.addObject("sortOptions", sortOptions);

        // Create translated category options for paw:formSelect
        var categories = productService.getAllCategories();
        var categoryOptions = new java.util.ArrayList<SelectOption>();
        for (var cat : categories) {
            categoryOptions.add(new SelectOption(cat.getId(), messageSource.getMessage("category." + cat.getName(), null, LocaleContextHolder.getLocale())));
        }
        mav.addObject("categoryOptions", categoryOptions);

        if (filterForm.getCategoryId() != null) {
            // Create translated subcategory options for paw:formSelect
            var subcategories = productService.getSubcategoriesByCategory(filterForm.getCategoryId());
            var subcategoryOptions = new java.util.ArrayList<SelectOption>();
            for (var sub : subcategories) {
                subcategoryOptions.add(new SelectOption(sub.getId(), messageSource.getMessage("subcategory." + sub.getName(), null, LocaleContextHolder.getLocale())));
            }
            mav.addObject("subcategoryOptions", subcategoryOptions);
        }
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView listing(@PathVariable Long id, @CurrentUser(required = false) User currentUser) {
        var listing = listingService.getById(id);
        var isCreator = currentUser != null && currentUser.getId().equals(listing.getCreator().getId());
        var isSold = listing.getStatus() == ListingStatus.SOLD;

        return new ModelAndView("listing/index")
                .addObject("listing", listing)
                .addObject("isCreator", isCreator)
                .addObject("isSold", isSold);
    }

    @GetMapping("/new/choose-product")
    public ModelAndView chooseProduct(@ModelAttribute("chooseProductForm") ChooseProductForm form,
                                      @RequestParam(value = "productId", required = false) Long productId) {
        var mav = new ModelAndView("listing/new/chooseProduct");
        // Coming back from step 2: rehydrate the form from the already chosen product
        // so the user sees and can change their selection instead of starting over.
        if (productId != null) {
            var product = productService.getById(productId);
            var subcategory = product.getSubcategory();
            form.setCategoryId(subcategory.getCategory().getId());
            form.setSubcategoryId(subcategory.getId());
            form.setNewProductBrand(product.getBrand());
            form.setNewProductModel(product.getModel());
            form.setNewProductYear(product.getYear());
            form.setStep(3);
            form.updatePreviousValues();
        } else {
            form.setStep(1);
        }
        populateModel(mav, form);
        return mav;
    }

    @PostMapping("/new/choose-product")
    public ModelAndView chooseProductPost(@Valid @ModelAttribute("chooseProductForm") ChooseProductForm form, BindingResult bindingResult) {
        var mav = new ModelAndView("listing/new/chooseProduct");

        // Skip validation for auto-submits (triggered by field changes during form filling)
        var isAutoSubmit = Boolean.TRUE.equals(form.getIsAutoSubmit());
        if (isAutoSubmit) {
            form.setIsAutoSubmit(false); // Reset for next request
        }

        // Reset downstream fields when user goes back and changes a previous field
        var step = form.getStep();
        if (step != null && step > 1 && form.isCategoryChanged()) {
            // Category was changed - reset subcategory and product fields
            form.setSubcategoryId(null);
            form.setNewProductBrand(null);
            form.setNewProductModel(null);
            form.setNewProductYear(null);
            form.setStep(2);
        } else if (step != null && step > 2 && form.isSubcategoryChanged()) {
            // Subcategory was changed - reset product fields
            form.setNewProductBrand(null);
            form.setNewProductModel(null);
            form.setNewProductYear(null);
            form.setStep(3);
        }

        // Reset model if brand is empty (empty string)
        if (form.getNewProductBrand() == null || form.getNewProductBrand().isBlank()) {
            form.setNewProductModel(null);
        }

        if (bindingResult.hasErrors()) {
            form.updatePreviousValues();
            populateModel(mav, form);
            return mav;
        }

        if (form.getStep() != null && form.getStep() == 1) {
            if (form.getCategoryId() != null) {
                form.setStep(2);
                form.setSubcategoryId(null);
            }
        } else if (form.getStep() != null && form.getStep() == 2) {
            if (form.getSubcategoryId() != null) {
                form.setStep(3);
                form.setNewProductBrand(null);
                form.setNewProductModel(null);
                form.setNewProductYear(null);
            }
        } else if (form.getStep() != null && form.getStep() == 3 && !isAutoSubmit) {
            var brand = form.getNewProductBrand();
            var model = form.getNewProductModel();

            if (OTHER_VALUE.equals(brand)) {
                form.setNewProductModel(OTHER_VALUE);
                model = OTHER_VALUE;
            }

            var otherBrand = form.getOtherBrand();
            var otherModel = form.getOtherModel();

            brand = OTHER_VALUE.equals(brand) ? otherBrand : brand;
            model = OTHER_VALUE.equals(model) ? otherModel : model;

            var product = productService.findOrCreateByBrandModelYear(
                    brand,
                    model,
                    form.getNewProductYear(),
                    form.getSubcategoryId()
            );
            return new ModelAndView("redirect:/listing/new/details?productId=" + product.getId());
        }

        // Update previous values for next request
        form.updatePreviousValues();

        populateModel(mav, form);
        return mav;
    }

    @GetMapping("/new/details")
    public ModelAndView details(@RequestParam("productId") Long productId, @ModelAttribute("detailsForm") ListingDetailsForm form) {
        var product = productService.getById(productId);
        var mav = new ModelAndView("listing/new/details");

        mav.addObject("product", product);
        mav.addObject("conditionOptions", buildConditionOptions());
        form.setProductId(product.getId());
        return mav;
    }

    @PostMapping("/new/details")
    public ModelAndView detailsPost(
            @Valid @ModelAttribute("detailsForm") ListingDetailsForm form,
            BindingResult bindingResult,
            @CurrentUser User currentUser
    ) {
        if (bindingResult.hasErrors()) {
            return detailsWithErrors();
        }

        List<ImageData> imageDataList = new ArrayList<>();
        if (form.getImages() != null) {
            for (MultipartFile imageFile : form.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        imageDataList.add(new ImageData(
                            imageFile.getBytes(),
                            imageFile.getOriginalFilename(),
                            imageFile.getContentType()
                        ));
                    } catch (IOException e) {
                        bindingResult.rejectValue("images", "error.image.upload");
                        return detailsWithErrors();
                    }
                }
            }
        }

        var newListing = listingService.create(new ListingCreationDto(
                form.getTitle(),
                new Price(form.getPrice()),
                currentUser.getId(),
                form.getProductId(),
                form.getCondition(),
                form.isAcceptsTrade(),
                form.getDescription(),
                imageDataList
        ));
        return new ModelAndView("redirect:/listing/" + newListing.getId());
    }

    private ModelAndView detailsWithErrors() {
        return new ModelAndView("listing/new/details")
                .addObject("conditionOptions", buildConditionOptions());
    }

    private void populateModel(ModelAndView mav, ChooseProductForm form) {
        var categories = productService.getAllCategories();
        mav.addObject("categories", categories);

        // Create translated category options
        var categoryOptions = new java.util.ArrayList<SelectOption>();
        for (var cat : categories) {
            categoryOptions.add(new SelectOption(cat.getId(), messageSource.getMessage("category." + cat.getName(), null, LocaleContextHolder.getLocale())));
        }
        mav.addObject("categoryOptions", categoryOptions);

        if (form.getCategoryId() != null) {
            var subcategories = productService.getSubcategoriesByCategory(form.getCategoryId());
            mav.addObject("subcategories", subcategories);

            // Create translated subcategory options
            var subcategoryOptions = new java.util.ArrayList<SelectOption>();
            for (var sub : subcategories) {
                subcategoryOptions.add(new SelectOption(sub.getId(), messageSource.getMessage("subcategory." + sub.getName(), null, LocaleContextHolder.getLocale())));
            }
            mav.addObject("subcategoryOptions", subcategoryOptions);
        }

        if (form.getSubcategoryId() != null) {
            mav.addObject("brands", productService.getBrandsBySubcategory(form.getSubcategoryId()));
            List<String> models = List.of();
            // Only fetch models if brand is selected and not "Other"
            if (form.getNewProductBrand() != null && !form.getNewProductBrand().isBlank() && !OTHER_VALUE.equals(form.getNewProductBrand())) {
                models = productService.getModelsBySubcategoryAndBrand(form.getSubcategoryId(), form.getNewProductBrand());
            }
            mav.addObject("models", models);
            mav.addObject("modelsEmpty", models.isEmpty());
        }
    }

    private List<StringSelectOption> buildConditionOptions() {
        var options = new java.util.ArrayList<StringSelectOption>();
        for (var c : Condition.values()) {
            options.add(new StringSelectOption(
                c.name(),
                messageSource.getMessage("condition." + c.name(), null, LocaleContextHolder.getLocale())
            ));
        }
        return options;
    }
}
