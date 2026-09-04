<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="listing.new.title"/></title>
    <%-- FOR DEVELOPMENT ONLY!! --%>
    <%-- <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script> --%>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="px-8 pb-24 bg-neutral-50">
    <paw:navbar />

    <div class="max-w-3xl mx-auto mt-8">
        <c:url value="/listing/new/details" var="detailsUrl"/>

        <paw:card>
            <jsp:body>
                <div class="mb-2 flex items-center gap-2">
                    <div class="flex-1 text-sky-600 font-medium text-sm flex items-center gap-2 p-3 rounded-lg">
                        <div class="w-6 h-6 rounded-full border-2 border-sky-600 flex items-center justify-center text-xs text-sky-600">✓</div>
                        <spring:message code="listing.new.step1" var="step1Label"/>
                        <span><c:out value="${step1Label}"/></span>
                    </div>
                    <div class="flex-1 text-sky-600 font-medium text-sm flex items-center gap-2 bg-sky-100 p-3 rounded-lg">
                        <div class="w-6 h-6 rounded-full border-2 border-sky-600 bg-sky-600 flex items-center justify-center text-xs text-sky-100">2</div>
                        <spring:message code="listing.new.step2" var="step2Label"/>
                        <span><c:out value="${step2Label}"/></span>
                    </div>
                </div>

                <div class="text-sm text-black/60 mb-2">
                    <spring:message code="listing.new.details.desc" var="detailsDesc"/>
                    <c:out value="${detailsDesc}"/>
                </div>

                <%-- TODO move this to a custom tag --%>
                <hr class="border-t-0 border-b border-black/10">

                <c:url value="/listing/new/details" var="detailsUrl"/>

                <form:form id="detailsForm" modelAttribute="detailsForm" action="${detailsUrl}" method="post" class="flex flex-col gap-6">
                    <form:hidden path="productId"/>

                    <spring:message code="listing.new.titleLabel" var="titleLabel"/>
                    <paw:formInput path="title" label="${titleLabel}" />

                    <spring:message code="listing.new.price" var="priceLabel"/>
                    <paw:formInput path="price" label="${priceLabel}" type="number" step="0.01" min="0" />

                    <div class="mt-2 flex justify-center gap-4">
                        <spring:message code="listing.new.submitListing" var="submitLabel"/>
                        <paw:button text="${submitLabel}" size="lg" classname="w-60" type="submit" variant="primary"/>
                    </div>
                </form:form>
            </jsp:body>
        </paw:card>
    </div>
</body>
</html>