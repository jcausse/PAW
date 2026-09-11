<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.listings.title" var="titleMsg"/>
<spring:message code="account.listings.subtitle" var="subtitleMsg"/>
<spring:message code="account.listings.newListing" var="newListingLabel"/>
<spring:message code="account.listings.filter.status" var="statusLabel"/>
<spring:message code="account.listings.filter.all" var="allLabel"/>
<spring:message code="account.listings.filter.sort" var="sortLabel"/>
<spring:message code="account.listings.filter.default" var="defaultLabel"/>
<spring:message code="listing.status.ACTIVE" var="statusActive"/>
<spring:message code="listing.status.SOLD" var="statusSold"/>
<spring:message code="discovery.sort.recent" var="sortRecent"/>
<spring:message code="discovery.sort.price_asc" var="sortPriceAsc"/>
<spring:message code="discovery.sort.price_desc" var="sortPriceDesc"/>
<spring:message code="account.listings.sort.name_asc" var="sortNameAsc"/>
<spring:message code="account.listings.sort.name_desc" var="sortNameDesc"/>

<c:url value="/account/listings" var="filterAction"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.listings.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}" actionHref="/listing/new/choose-product" actionText="${newListingLabel}">
    <paw:card>
        <form:form modelAttribute="filterForm" action="${filterAction}" method="get" id="filterForm">
            <div class="flex items-center justify-between gap-4 mb-4">
                <div class="flex gap-2">
                    <paw:formSelect path="status" label="${statusLabel}" placeholder="${allLabel}" items="${statusOptions}" stringOptions="true" classname="w-auto" />
                </div>
                <div class="flex items-center gap-2">
                    <paw:formSelect path="sort" label="${sortLabel}" placeholder="${defaultLabel}" items="${sortOptions}" stringOptions="true" classname="w-auto" />
                </div>
            </div>

            <paw:divider />

            <c:choose>
                <c:when test="${empty listings}">
                    <div class="mt-12 mb-12 flex flex-col items-center">
                        <spring:message code="account.listings.empty" var="emptyMsg"/>
                        <p class="text-black/60 text-lg mb-4"><c:out value="${emptyMsg}"/></p>
                        <paw:linkButton text="${newListingLabel}" size="lg" href="${newListingUrl}"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="listing" items="${listings}" varStatus="loop">
                        <c:set var="coverUrl" value=""/>
                        <c:if test="${not empty listing.imageIds}">
                            <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                        </c:if>

                        <c:choose>
                            <c:when test="${listing.status.name() == 'ACTIVE'}">
                                <c:set var="statusClass" value="text-green-600"/>
                                <spring:message code="listing.status.ACTIVE" var="statusLabel"/>
                            </c:when>
                            <c:when test="${listing.status.name() == 'SOLD'}">
                                <c:set var="statusClass" value="text-neutral-600"/>
                                <spring:message code="listing.status.SOLD" var="statusLabel"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="statusClass" value="text-yellow-800"/>
                                <c:set var="statusLabel" value="${listing.status.name()}"/>
                            </c:otherwise>
                        </c:choose>

                        <div class="py-4 flex flex-col sm:flex-row sm:items-center gap-4">
                            <div class="flex-1 min-w-0">
                                <h3 class="text-base font-semibold truncate">
                                    <c:url value="/listing/${listing.id}" var="listingUrl"/>
                                    <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${listing.title}"/></a>
                                </h3>
                            </div>
                            <paw:badge text="${statusLabel}" classname="${statusClass}" />

                            <div class="flex gap-3 w-full sm:w-auto">
                                <div class="w-20 h-20 flex-shrink-0 rounded-lg overflow-hidden border border-black/10 bg-neutral-200">
                                    <c:choose>
                                        <c:when test="${not empty coverUrl}">
                                            <img src="${coverUrl}" alt="<c:out value='${listing.title}'/>" class="w-full h-full object-cover"/>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="w-full h-full grid place-items-center text-black/30 text-xs px-2 text-center">
                                                <spring:message code="card.noImage" var="noImageLabel"/>
                                                <c:out value="${noImageLabel}"/>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="flex-1 min-w-0 flex flex-col justify-between">
                                    <div>
                                        <div class="text-black truncate"><c:out value="${listing.product.brand}"/> <c:out value="${listing.product.model}"/> (<c:out value="${listing.product.year}"/>)</div>
                                        <div class="text-sm text-black/60 mt-1 truncate">
                                            <spring:message code="category.${listing.product.subcategory.category.name}"/>
                                            /
                                            <spring:message code="subcategory.${listing.product.subcategory.name}"/>
                                        </div>
                                    </div>
                                    <div class="text-xl font-bold mt-2 sm:mt-0">
                                        $<c:out value="${listing.price.amount}"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <c:if test="${!loop.last}">
                            <paw:divider />
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </form:form>
    </paw:card>
</account:layout>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const statusSelect = document.getElementById('status');
        const sortSelect = document.getElementById('sort');
        const isAutoSubmitInput = document.getElementById('isAutoSubmit');

        function submitForm() {
            if (isAutoSubmitInput) {
                isAutoSubmitInput.value = 'true';
            }
            document.querySelector('form#filterForm').submit();
        }

        if (statusSelect) {
            statusSelect.addEventListener('change', submitForm);
        }
        if (sortSelect) {
            sortSelect.addEventListener('change', submitForm);
        }
    });
</script>
</html>
