# ESTOS SON LOS CAMBIOS DEL AGENTE PARA ESTA RAMA
# ELIMINAR ESTE ARCHIVO LUEGO DE REVISAR EL PR

---

All manual validations in ListingController.java have been successfully converted into annotation-based validations.

### Summary of Changes

#### 1. Form Validation for ListingDetailsForm

• **ListingDetailsForm.java**:
• Added @NotNull on productId.
• Added @NotBlank and @Size(min = 3, max = 100) on title.
• Added @NotNull, @DecimalMin(value = "0.01"), and @Digits(integer = 9, fraction = 2) on price.
• Added @ValidImages on images.
• **ValidImages.java** & **ValidImagesValidator.java**:
• Validates that uploaded multipart files do not exceed max size (5MB per file) and have valid image MIME types (image/*).
• **ListingController.java:235-272** (detailsPost):
• Removed manual bindingResult.rejectValue("title", ...) and bindingResult.rejectValue("price", ...). Spring's @Valid handles them declaratively before entering the method body.


#### 2. Class-Level Validation for ChooseProductForm

• **ValidChooseProduct.java** & **ChooseProductValidator.java**:
• Bypasses validation on auto-submits (isAutoSubmit == true) or category/subcategory resets.
• Step 1: Validates categoryId.
• Step 2: Validates subcategoryId.
• Step 3: Validates newProductBrand, conditional otherBrand (when brand is "__OTHER__"), newProductModel, conditional otherModel, and newProductYear (range 1900 to current year).
• Binds errors directly to each field's property node so <form:errors path="..." /> in chooseProduct.jsp displays them on the corresponding inputs.
• **ChooseProductForm.java**:
• Annotated with @ValidChooseProduct.
• **ListingController.java:95-192** (chooseProductPost):
• Removed ~80 lines of manual bindingResult.rejectValue(...) calls. The controller now only handles navigation and product creation.


#### 3. Internationalization (i18n)

• Updated both **messages.properties** and **messages_es.properties** with message keys for all new constraints on chooseProductForm and detailsForm.
