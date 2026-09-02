<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="listing.new.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-5xl mx-auto">
        <c:url value="/listing/new" var="newListingUrl"/>

        <form:form modelAttribute="listingForm" action="${newListingUrl}" method="post" class="bg-white rounded-xl shadow-sm p-6">
            <form:hidden path="step"/>
            <form:hidden path="categoryId"/>
            <form:hidden path="subcategoryId"/>
            <form:hidden path="productSelectionMode"/>
            <form:hidden path="existingProductId"/>
            <form:hidden path="newProductName"/>
            <form:hidden path="newProductBrand"/>
            <form:hidden path="newProductModel"/>
            <form:hidden path="newProductYear"/>
            <form:hidden path="newProductSubcategoryId"/>
            <form:hidden path="selectedProductId"/>

            <div class="mb-6">
                <div class="flex gap-4 mb-4">
                    <div class="flex-1 ${listingForm.step eq 1 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step1" var="step1Label"/><c:out value="${step1Label}"/>
                    </div>
                    <div class="flex-1 ${listingForm.step eq 2 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step2" var="step2Label"/><c:out value="${step2Label}"/>
                    </div>
                    <div class="flex-1 ${listingForm.step eq 3 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step3" var="step3Label"/><c:out value="${step3Label}"/>
                    </div>
                </div>
            </div>

            <c:choose>
                <c:when test="${listingForm.step == 1}">
                    <%-- Step 1: Category and Subcategory Selection --%>
                    <spring:message code="listing.new.step1.title" var="step1Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step1Title}"/></h2>

                    <spring:message code="listing.new.category" var="categoryLabel"/>
                    <spring:message code="listing.new.category.select" var="categoryPlaceholder"/>
                    <paw:formSelect path="categoryId" label="${categoryLabel}" placeholder="${categoryPlaceholder}" items="${categories}" />

                    <spring:message code="listing.new.subcategory" var="subcategoryLabel"/>
                    <spring:message code="listing.new.subcategory.select" var="subcategoryPlaceholder"/>
                    <paw:formSelect path="subcategoryId" label="${subcategoryLabel}" placeholder="${categoryPlaceholder}" items="${subcategories}" />

                    <div class="mt-6 flex justify-end">
                        <spring:message code="listing.new.next" var="nextLabel"/>
                        <paw:button text="${nextLabel}" type="submit" variant="primary"/>
                    </div>
                </c:when>

                <c:when test="${listingForm.step == 2}">
                    <%-- Step 2: Product Selection --%>
                    <spring:message code="listing.new.step2.title" var="step2Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step2Title}"/></h2>

                    <spring:message code="listing.new.productSelectionMode" var="productSelectionModeLabel"/>
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-neutral-700 mb-2"><c:out value="${productSelectionModeLabel}"/></label>
                        <div class="flex gap-4">
                            <label class="flex items-center gap-2 cursor-pointer">
                                <form:radiobutton path="productSelectionMode" value="existing"/>
                                <spring:message code="listing.new.productSelection.existing" var="existingLabel"/>
                                <span><c:out value="${existingLabel}"/></span>
                            </label>
                            <label class="flex items-center gap-2 cursor-pointer">
                                <form:radiobutton path="productSelectionMode" value="new"/>
                                <spring:message code="listing.new.productSelection.new" var="newLabel"/>
                                <span><c:out value="${newLabel}"/></span>
                            </label>
                        </div>
                        <form:errors path="productSelectionMode" cssClass="text-xs text-red-600 mt-1"/>
                    </div>

                    <c:choose>
                        <c:when test="${listingForm.productSelectionMode == 'existing'}">
                            <%-- Existing Product Selection --%>
                            <spring:message code="listing.new.existingProduct" var="existingProductLabel"/>
                            <paw:formInput path="existingProductId" label="${existingProductLabel}" type="select">
                                <form:option value=""><spring:message code="listing.new.existingProduct.select"/></form:option>
                                <form:options items="${products}" itemValue="id" itemLabel="name"/>
                            </paw:formInput>
                        </c:when>
                        <c:when test="${listingForm.productSelectionMode == 'new'}">
                            <%-- New Product Creation --%>
                            <spring:message code="listing.new.newProduct.title" var="newProductTitle"/>
                            <h3 class="text-lg font-medium mb-3"><c:out value="${newProductTitle}"/></h3>

                            <spring:message code="listing.new.newProduct.name" var="newProductNameLabel"/>
                            <paw:formInput path="newProductName" label="${newProductNameLabel}" />

                            <spring:message code="listing.new.newProduct.brand" var="newProductBrandLabel"/>
                            <paw:formInput path="newProductBrand" label="${newProductBrandLabel}" type="select">
                                <form:option value=""><spring:message code="listing.new.newProduct.brand.select"/></form:option>
                                <form:options items="${brands}"/>
                            </paw:formInput>

                            <spring:message code="listing.new.newProduct.model" var="newProductModelLabel"/>
                            <paw:formInput path="newProductModel" label="${newProductModelLabel}" type="select">
                                <form:option value=""><spring:message code="listing.new.newProduct.model.select"/></form:option>
                                <form:options items="${models}"/>
                            </paw:formInput>

                            <spring:message code="listing.new.newProduct.year" var="newProductYearLabel"/>
                            <paw:formInput path="newProductYear" label="${newProductYearLabel}" type="select">
                                <form:option value=""><spring:message code="listing.new.newProduct.year.select"/></form:option>
                                <form:options items="${years}"/>
                            </paw:formInput>

                            <form:hidden path="newProductSubcategoryId" value="${listingForm.subcategoryId}"/>
                        </c:when>
                        <c:otherwise>
                            <p class="text-neutral-500"><spring:message code="listing.new.productSelection.prompt"/></p>
                        </c:otherwise>
                    </c:choose>

                    <div class="mt-6 flex gap-4">
                        <spring:message code="listing.new.back" var="backLabel"/>
                        <button type="submit" name="_action" value="back" class="px-4 py-2 bg-neutral-200 text-neutral-700 rounded-lg hover:bg-neutral-300 transition"><c:out value="${backLabel}"/></button>

                        <spring:message code="listing.new.next" var="nextLabel"/>
                        <paw:button text="${nextLabel}" type="submit" variant="primary"/>
                    </div>
                </c:when>

                <c:when test="${listingForm.step == 3}">
                    <%-- Step 3: Listing Details --%>
                    <spring:message code="listing.new.step3.title" var="step3Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step3Title}"/></h2>

                    <spring:message code="listing.new.titleLabel" var="titleLabel"/>
                    <paw:formInput path="title" label="${titleLabel}" />

                    <spring:message code="listing.new.price" var="priceLabel"/>
                    <paw:formInput path="price" label="${priceLabel}" type="number" step="0.01" min="0" />

                    <div class="mt-6 flex gap-4">
                        <spring:message code="listing.new.back" var="backLabel"/>
                        <button type="submit" name="_action" value="back" class="px-4 py-2 bg-neutral-200 text-neutral-700 rounded-lg hover:bg-neutral-300 transition"><c:out value="${backLabel}"/></button>

                        <spring:message code="listing.new.submit" var="submitLabel"/>
                        <paw:button text="${submitLabel}" type="submit" variant="primary"/>
                    </div>
                </c:when>
            </c:choose>
        </form:form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const categorySelect = document.getElementById('categoryId');
            const subcategorySelect = document.getElementById('subcategoryId');
            const brandSelect = document.getElementById('newProductBrand');
            const modelSelect = document.getElementById('newProductModel');
            const yearSelect = document.getElementById('newProductYear');
            const productSelectionRadios = document.querySelectorAll('input[name="productSelectionMode"]');

            function submitForm() {
                document.querySelector('form').submit();
            }

            if (categorySelect) {
                categorySelect.addEventListener('change', submitForm);
            }
            if (subcategorySelect) {
                subcategorySelect.addEventListener('change', submitForm);
            }
            if (brandSelect) {
                brandSelect.addEventListener('change', submitForm);
            }
            if (modelSelect) {
                modelSelect.addEventListener('change', submitForm);
            }
            if (yearSelect) {
                yearSelect.addEventListener('change', submitForm);
            }

            productSelectionRadios.forEach(radio => {
                radio.addEventListener('change', submitForm);
            });
        });
    </script>
</body>
</html>
