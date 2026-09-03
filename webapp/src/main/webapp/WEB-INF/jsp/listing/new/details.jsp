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
        <c:url value="/listing/new/details" var="detailsUrl"/>

        <form:form id="detailsForm" modelAttribute="detailsForm" action="${detailsUrl}" method="post" class="bg-white rounded-xl shadow-sm p-6">
            <form:hidden path="productId"/>

            <h2 class="text-2xl font-semibold mb-6"><spring:message code="listing.new.step4.title"/></h2>

            <spring:message code="listing.new.titleLabel" var="titleLabel"/>
            <paw:formInput path="title" label="${titleLabel}" />

            <spring:message code="listing.new.price" var="priceLabel"/>
            <paw:formInput path="price" label="${priceLabel}" type="number" step="0.01" min="0" />

            <div class="mt-6 flex gap-4">
                <spring:message code="listing.new.submit" var="submitLabel"/>
                <paw:button text="${submitLabel}" type="submit" variant="primary"/>
            </div>
        </form:form>
    </div>
</body>
</html>