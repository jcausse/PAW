package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.webapp.exception.UserNotAuthenticatedException;
import ar.edu.itba.paw.webapp.form.ChooseProductForm;
import ar.edu.itba.paw.webapp.form.ListingDetailsForm;
import ar.edu.itba.paw.webapp.form.ListingFilterForm;
import ar.edu.itba.paw.webapp.form.SelectOption;
import ar.edu.itba.paw.webapp.form.StringSelectOption;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            filterForm.getSort()
        );

        final var mav = new ModelAndView("listing/discovery");
        mav.addObject("listings", listingService.search(filter));
        mav.addObject("categories", productService.getAllCategories());
        mav.addObject("conditions", Condition.values());
        mav.addObject("sortOptions", ListingSort.values());
        if (filterForm.getCategoryId() != null) {
            mav.addObject("subcategories", productService.getSubcategoriesByCategory(filterForm.getCategoryId()));
        }
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView listing(@PathVariable Long id) {
        return new ModelAndView("listing/index")
                .addObject("listing", listingService.getById(id));
    }

    @GetMapping("/new/choose-product")
    public ModelAndView chooseProduct(@ModelAttribute("chooseProductForm") ChooseProductForm form) {
        var mav = new ModelAndView("listing/new/chooseProduct");
        form.setStep(1);
        populateModel(mav, form);
        return mav;
    }

    @PostMapping("/new/choose-product")
    public ModelAndView chooseProductPost(@Valid @ModelAttribute("chooseProductForm") ChooseProductForm form, BindingResult bindingResult) {
        var mav = new ModelAndView("listing/new/chooseProduct");

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

        // Reset model if brand is empty (empty string)
        if (form.getNewProductBrand() == null || form.getNewProductBrand().isBlank()) {
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
            // Resolve brand and model - if "Other" is selected, use the text input value
            var brand = form.getNewProductBrand();
            var model = form.getNewProductModel();

            // Validate brand: must not be empty
            var hasBrand = brand != null && !brand.isBlank();
            var hasModel = model != null && !model.isBlank();

            if (!isAutoSubmit && !hasBrand) {
                bindingResult.rejectValue("newProductBrand", "NotNull");
            }

            if (hasBrand) {
                // Set model to "Other" if brand is set to "Other"
                // Validate model: must not be empty only if brand is set
                if (OTHER_VALUE.equals(brand)) {
                    form.setNewProductModel(OTHER_VALUE);
                    model = OTHER_VALUE;
                } else if (!isAutoSubmit && !hasModel) {
                    bindingResult.rejectValue("newProductModel", "NotNull");
                }
            } else {
                // Brand is not set, clear model fields
                form.setNewProductModel(null);
                form.setOtherModel(null);
                model = null;
            }

            var otherBrand = form.getOtherBrand();
            var otherModel = form.getOtherModel();

            // Validate otherBrand: must not be empty if brand is "__OTHER__"
            if (OTHER_VALUE.equals(brand) && !isAutoSubmit && (otherBrand == null || otherBrand.isBlank())) {
                    bindingResult.rejectValue("otherBrand", "NotNull");
            }

            // Validate otherModel: must not be empty if model is "__OTHER__"
            if (OTHER_VALUE.equals(model) && !isAutoSubmit && (otherModel == null || otherModel.isBlank())) {
                bindingResult.rejectValue("otherModel", "NotNull");
            }

            // Resolve final brand and model for product creation
            brand = OTHER_VALUE.equals(brand) ? otherBrand : brand;
            model = OTHER_VALUE.equals(model) ? otherModel : model;

            hasBrand = brand != null && !brand.isBlank();
            hasModel = model != null && !model.isBlank();
            var year = form.getNewProductYear();
            var hasYear = year != null;

            if (!isAutoSubmit) {
                var currentYear = Year.now().getValue();
                if (!hasYear) {
                    bindingResult.rejectValue("newProductYear", "NotNull");
                }
                else if (year < 1900 || year > currentYear) {
                    bindingResult.rejectValue("newProductYear", "Range", new Object[]{1900, currentYear}, "Year must be between 1900 and " + currentYear);
                }
            }

            // Advance to details page if brand, model, and year are all selected
            if (!bindingResult.hasErrors() && hasBrand && hasModel && hasYear) {
                // Find or create product by brand, model, year and subcategory
                var product = productService.findOrCreateByBrandModelYear(
                        brand,
                        model,
                        form.getNewProductYear(),
                        form.getSubcategoryId()
                );
                return new ModelAndView("redirect:/listing/new/details?productId=" + product.getId());
            }
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
    public ModelAndView detailsPost(@Valid @ModelAttribute("detailsForm") ListingDetailsForm form, BindingResult bindingResult, @ModelAttribute("currentUser") Optional<User> currentUser) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            bindingResult.rejectValue("title", "NotEmpty.listingForm.title");
        }
        if (form.getPrice() == null) {
            bindingResult.rejectValue("price", "NotNull.listingForm.price");
        }

        if (!bindingResult.hasErrors()) {
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
                            break;
                        }
                    }
                }
            }

            var newListing = listingService.create(new ListingCreationDto(
                    form.getTitle(),
                    new Price(form.getPrice()),
                    currentUser.orElseThrow(UserNotAuthenticatedException::new).getId(),
                    form.getProductId(),
                    form.getCondition(),
                    form.isAcceptsTrade(),
                    form.getDescription(),
                    imageDataList
            ));
            return new ModelAndView("redirect:/listing/" + newListing.getId());
        }

        return new ModelAndView("listing/new/details");
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
