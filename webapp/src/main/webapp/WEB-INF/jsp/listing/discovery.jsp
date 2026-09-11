<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="discovery.title"/>
<body class="bg-neutral-50 min-h-screen pb-24">
<paw:navbar/>

<c:url value="/listing" var="filterAction"/>
<spring:message code="discovery.filter.query.placeholder" var="queryPlaceholder"/>
<spring:message code="card.noImage" var="noImageLabel"/>
<spring:message code="discovery.filters" var="filtersTitle"/>
<spring:message code="discovery.filter.search" var="searchLabel"/>
<spring:message code="discovery.filter.category" var="categoryLabel"/>
<spring:message code="discovery.filter.subcategory" var="subcategoryLabel"/>
<spring:message code="discovery.filter.condition" var="conditionLabel"/>
<spring:message code="discovery.filter.minPrice" var="minPriceLabel"/>
<spring:message code="discovery.filter.maxPrice" var="maxPriceLabel"/>
<spring:message code="discovery.filter.acceptsTrade" var="acceptsTradeLabel"/>
<spring:message code="discovery.filter.apply" var="applyLabel"/>
<spring:message code="discovery.filter.clear" var="clearLabel"/>
<spring:message code="discovery.sort.label" var="sortLabel"/>
<spring:message code="discovery.filter.all" var="allLabel"/>

<form:form modelAttribute="filterForm" action="${filterAction}" method="get" id="filterForm">
<main class="max-w-6xl mx-auto px-6 py-8 flex flex-col gap-6">
    <div class="flex gap-2">
        <paw:formInput variant="outline" path="query" placeholder="${queryPlaceholder}" classname="flex-1" inputClassname="min-h-8.5" />
        <paw:button variant="outline" text="${searchLabel}" type="submit"/>
    </div>

    <div class="flex flex-col md:flex-row gap-8">
        <aside class="w-full md:w-56 shrink-0">
            <div class="sticky top-24">
                <paw:card title="${filtersTitle}">
                    <div class="flex flex-col gap-4">
                        <paw:formSelect path="categoryId" label="${categoryLabel}" placeholder="${allLabel}" items="${categoryOptions}" />
                        <c:if test="${not empty subcategoryOptions}">
                            <paw:formSelect path="subcategoryId" label="${subcategoryLabel}" placeholder="${allLabel}" items="${subcategoryOptions}" />
                        </c:if>

                        <paw:formSelect path="condition" label="${conditionLabel}" placeholder="${allLabel}" items="${conditionOptions}" stringOptions="true" />

                        <div class="flex flex-row gap-2">
                            <paw:formInput path="minPrice" type="number" step="0.01" min="0" label="${minPriceLabel}" classname="min-w-0" />
                            <paw:formInput path="maxPrice" type="number" step="0.01" min="0" label="${maxPriceLabel}" classname="min-w-0" />
                        </div>

                        <div class="flex flex-col gap-1">
                            <label class="flex items-center gap-2 text-sm">
                                <form:checkbox path="acceptsTrade" value="true" class="w-4 h-4 text-lime-600 border-black/20 outline-0 outline-offset-0 outline-lime-600/30 focus-visible:outline-2 accent-lime-600"/>
                                <c:out value="${acceptsTradeLabel}"/>
                            </label>
                        </div>

                        <div class="flex flex-col gap-2 pt-1">
                            <paw:button text="${applyLabel}" type="submit" classname="w-full"/>
                            <spring:message code="discovery.filter.clear" var="clearLabel"/>
                            <paw:linkButton href="${filterAction}" text="${clearLabel}" variant="ghost" size="sm" role="secondary" />
                        </div>
                    </div>
                </paw:card>
            </div>
        </aside>

        <section class="flex-1">
            <div class="flex items-center justify-between gap-4 mb-4">
                <span class="text-sm text-black/60">
                    <spring:message code="discovery.results.count" arguments="${fn:length(listings)}"/>
                </span>
                <div class="flex items-center gap-2">
                    <paw:formSelect path="sort" label="${sortLabel}" placeholder="${allLabel}" items="${sortOptions}" stringOptions="true" classname="w-auto" />
                </div>
            </div>

            <c:choose>
                <c:when test="${empty listings}">
                    <div class="flex items-center justify-center h-64 text-black/50">
                        <spring:message code="discovery.empty"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        <c:forEach var="listing" items="${listings}">
                            <c:url value="/listing/${listing.id}" var="listingUrl"/>
                            <c:set var="coverUrl" value=""/>
                            <c:if test="${not empty listing.imageIds}">
                                <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                            </c:if>
                            <c:set var="subLabel" value=""/>
                            <c:if test="${not empty listing.product and not empty listing.product.subcategory}">
                                <spring:message code="subcategory.${listing.product.subcategory.name}" var="subLabel"/>
                            </c:if>
                            <a href="${listingUrl}" class="block hover:-translate-y-0.5 transition">
                                <paw:card title="${listing.title}" subtitle="${subLabel}"
                                          showImage="true" imageUrl="${coverUrl}"
                                          imageAlt="${listing.title}" noImageLabel="${noImageLabel}">
                                    <div class="flex items-center gap-2 flex-wrap">
                                        <p class="text-xl font-bold">$<c:out value="${listing.price.amount}"/></p>
                                        <c:if test="${listing.acceptsTrade}">
                                            <span class="text-xs text-black/50 bg-black/5 rounded-full px-2 py-0.5">
                                                <spring:message code="discovery.acceptsTrade.badge"/>
                                            </span>
                                        </c:if>
                                    </div>
                                </paw:card>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>
</form:form>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const sortSelect = document.getElementById('sort');
        if (sortSelect) {
            sortSelect.addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });
        }
    });
</script>
</body>
</html>
