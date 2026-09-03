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
        <c:url value="/listing/new/choose-product" var="chooseProductUrl"/>

        <form:form id="chooseProductForm" modelAttribute="chooseProductForm" action="${chooseProductUrl}" method="post" class="bg-white rounded-xl shadow-sm p-6">
            <form:hidden path="step"/>
            <form:hidden path="isAutoSubmit"/>
            <form:hidden path="previousCategoryId"/>
            <form:hidden path="previousSubcategoryId"/>

            <%-- Step 1: Category Selection (always visible) --%>
            <div class="mb-6">
                <spring:message code="listing.new.category" var="categoryLabel"/>
                <spring:message code="listing.new.category.select" var="categoryPlaceholder"/>
                <paw:formSelect path="categoryId" label="${categoryLabel}" placeholder="${categoryPlaceholder}" items="${categories}" />
            </div>

            <%-- Step 2: Subcategory Selection (visible when category selected) --%>
            <c:if test="${chooseProductForm.step ge 2}">
                <div class="mb-6">
                    <spring:message code="listing.new.subcategory" var="subcategoryLabel"/>
                    <spring:message code="listing.new.subcategory.select" var="subcategoryPlaceholder"/>
                    <paw:formSelect path="subcategoryId" label="${subcategoryLabel}" placeholder="${subcategoryPlaceholder}" items="${subcategories}" />
                </div>
            </c:if>

            <%-- Step 3: Product Selection (visible when subcategory selected) --%>
            <c:if test="${chooseProductForm.step ge 3}">
                <div class="mb-6">
                    <spring:message code="listing.new.step3.title" var="step3Title"/>
                    <h2 class="text-xl font-semibold mb-4"><c:out value="${step3Title}"/></h2>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <spring:message code="listing.new.filter.brand" var="filterBrandLabel"/>
                        <spring:message code="listing.new.filter.brand.select" var="brandPlaceholder"/>
                        <paw:formSelect path="newProductBrand" label="${filterBrandLabel}" placeholder="${brandPlaceholder}" items="${brands}" plainStrings="true" />

                        <spring:message code="listing.new.filter.model" var="filterModelLabel"/>
                        <spring:message code="listing.new.filter.model.select" var="modelPlaceholder"/>
                        <paw:formSelect path="newProductModel" label="${filterModelLabel}" placeholder="${modelPlaceholder}" items="${models}" plainStrings="true" disabled="${modelsEmpty}" />

                        <spring:message code="listing.new.filter.year" var="filterYearLabel"/>
                        <spring:message code="listing.new.filter.year.select" var="yearPlaceholder"/>
                        <paw:formInput path="newProductYear" label="${filterYearLabel}" placeholder="${yearPlaceholder}" type="number" />
                    </div>
                </div>
            </c:if>

            <div class="mt-6 flex gap-4">
                <spring:message code="listing.new.next" var="nextLabel"/>
                <paw:button text="${nextLabel}" type="submit" variant="primary"/>
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
            const isAutoSubmitInput = document.getElementById('isAutoSubmit');

            function submitForm() {
                if (isAutoSubmitInput) {
                    isAutoSubmitInput.value = 'true';
                }
                document.querySelector('form#chooseProductForm').submit();
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
        });
    </script>
</body>
</html>