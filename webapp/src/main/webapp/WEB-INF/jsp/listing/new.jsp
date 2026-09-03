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
<body class="px-8 pb-24 bg-neutral-50">
    <paw:navbar />

    <div class="max-w-5xl mx-auto mt-8">
        <c:url value="/listing/new" var="newListingUrl"/>

        <form:form id="listingForm" modelAttribute="listingForm" action="${newListingUrl}" method="post" class="bg-white rounded-xl shadow-sm p-6">
            <form:hidden path="step"/>
            <form:hidden path="existingProductId"/>
            <form:hidden path="selectedProductId"/>

            <div class="mb-6">
                <div class="flex gap-4 mb-4">
                    <div class="flex-1 ${listingForm.step ge 1 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step1" var="step1Label"/><c:out value="${step1Label}"/>
                    </div>
                    <div class="flex-1 ${listingForm.step ge 2 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step2" var="step2Label"/><c:out value="${step2Label}"/>
                    </div>
                    <div class="flex-1 ${listingForm.step ge 3 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step3" var="step3Label"/><c:out value="${step3Label}"/>
                    </div>
                    <div class="flex-1 ${listingForm.step ge 4 ? 'bg-sky-600 text-white' : 'bg-neutral-200 text-neutral-600'} px-4 py-2 rounded-lg text-center font-medium">
                        <spring:message code="listing.new.step4" var="step4Label"/><c:out value="${step4Label}"/>
                    </div>
                </div>
            </div>

            <%-- Step 1: Category Selection (always visible) --%>
            <div class="mb-6">
                <spring:message code="listing.new.category" var="categoryLabel"/>
                <spring:message code="listing.new.category.select" var="categoryPlaceholder"/>
                <paw:formSelect path="categoryId" label="${categoryLabel}" placeholder="${categoryPlaceholder}" items="${categories}" />
            </div>

            <%-- Step 2: Subcategory Selection (visible when category selected) --%>
            <c:if test="${listingForm.step ge 2}">
                <div class="mb-6">
                    <spring:message code="listing.new.subcategory" var="subcategoryLabel"/>
                    <spring:message code="listing.new.subcategory.select" var="subcategoryPlaceholder"/>
                    <paw:formSelect path="subcategoryId" label="${subcategoryLabel}" placeholder="${subcategoryPlaceholder}" items="${subcategories}" />
                </div>
            </c:if>

            <%-- Step 3: Product Selection (visible when subcategory selected) --%>
            <c:if test="${listingForm.step ge 3}">
                <div class="mb-6">
                    <spring:message code="listing.new.step3.title" var="step3Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step3Title}"/></h2>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <spring:message code="listing.new.newProduct.brand" var="newProductBrandLabel"/>
                        <spring:message code="listing.new.newProduct.brand.select" var="brandPlaceholder"/>
                        <paw:formSelect path="newProductBrand" label="${newProductBrandLabel}" placeholder="${brandPlaceholder}" items="${brands}" plainStrings="true" />

                        <spring:message code="listing.new.newProduct.model" var="newProductModelLabel"/>
                        <spring:message code="listing.new.newProduct.model.select" var="modelPlaceholder"/>
                        <paw:formSelect path="newProductModel" label="${newProductModelLabel}" placeholder="${modelPlaceholder}" items="${models}" plainStrings="true" />

                        <spring:message code="listing.new.newProduct.year" var="newProductYearLabel"/>
                        <spring:message code="listing.new.newProduct.year.select" var="yearPlaceholder"/>
                        <paw:formInput path="newProductYear" label="${newProductYearLabel}" placeholder="${yearPlaceholder}" type="number" />
                    </div>

                    <spring:message code="listing.new.product" var="productLabel"/>
                    <spring:message code="listing.new.product.select" var="productPlaceholder"/>
                    <paw:formSelect path="existingProductId" label="${productLabel}" placeholder="${productPlaceholder}" items="${products}" />

                    <form:hidden path="newProductSubcategoryId" value="${listingForm.subcategoryId}"/>
                </div>
            </c:if>

            <%-- Step 4: Listing Details (visible when product selected) --%>
            <c:if test="${listingForm.step ge 4}">
                <div class="mb-6">
                    <spring:message code="listing.new.step4.title" var="step4Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step4Title}"/></h2>

                    <spring:message code="listing.new.titleLabel" var="titleLabel"/>
                    <paw:formInput path="title" label="${titleLabel}" />

                    <spring:message code="listing.new.price" var="priceLabel"/>
                    <paw:formInput path="price" label="${priceLabel}" type="number" step="0.01" min="0" />
                </div>
            </c:if>

            <div class="mt-6 flex gap-4">
                <c:choose>
                    <c:when test="${listingForm.step lt 4}">
                        <spring:message code="listing.new.next" var="nextLabel"/>
                        <paw:button text="${nextLabel}" type="submit" variant="primary"/>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="listing.new.submit" var="submitLabel"/>
                        <paw:button text="${submitLabel}" type="submit" variant="primary"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const categorySelect = document.getElementById('categoryId');
            const subcategorySelect = document.getElementById('subcategoryId');
            const brandSelect = document.getElementById('newProductBrand');
            const modelSelect = document.getElementById('newProductModel');
            const yearInput = document.getElementById('newProductYear');

            function submitForm() {
                document.querySelector('form#listingForm').submit();
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
            if (yearInput) {
                yearInput.addEventListener('change', submitForm);
            }
        });
    </script>
</body>
</html>
