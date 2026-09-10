<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.listings.title" var="titleMsg"/>
<spring:message code="account.listings.subtitle" var="subtitleMsg"/>
<spring:message code="account.listings.newListing" var="actionText"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.listings.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}" actionHref="/listing/new/choose-product" actionText="${actionText}">
    <c:choose>
        <c:when test="${empty listings}">
            <div class="text-center py-12">
                <spring:message code="account.listings.empty" var="emptyMsg"/>
                <p class="text-black/50 text-lg mb-4"><c:out value="${emptyMsg}"/></p>
                <paw:linkButton text="${newListingLabel}" variant="primary" href="${newListingUrl}"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <c:forEach var="listing" items="${listings}">
                    <c:set var="coverUrl" value=""/>
                    <c:if test="${not empty listing.imageIds}">
                        <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                    </c:if>

                    <c:choose>
                        <c:when test="${listing.status.name() == 'ACTIVE'}">
                            <c:set var="statusClass" value="bg-lime-100 text-lime-800"/>
                            <spring:message code="listing.status.ACTIVE" var="statusLabel"/>
                        </c:when>
                        <c:when test="${listing.status.name() == 'SOLD'}">
                            <c:set var="statusClass" value="bg-black/10 text-black/70"/>
                            <spring:message code="listing.status.SOLD" var="statusLabel"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="statusClass" value="bg-yellow-100 text-yellow-800"/>
                            <c:set var="statusLabel" value="${listing.status.name()}"/>
                        </c:otherwise>
                    </c:choose>

                    <paw:card classname="flex flex-col h-full">
                        <div class="flex items-start justify-between gap-2">
                            <h3 class="text-base font-semibold flex-1 min-w-0 truncate">
                                <c:url value="/listing/${listing.id}" var="listingUrl"/>
                                <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${listing.title}"/></a>
                            </h3>
                            <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${statusClass} flex-shrink-0">
                                <c:out value="${statusLabel}"/>
                            </span>
                        </div>

                        <div class="flex gap-3 mt-2">
                            <div class="w-24 h-24 flex-shrink-0 rounded-lg overflow-hidden border border-black/10 bg-neutral-200">
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
                                <div class="text-xl font-bold mt-2">
                                    $<c:out value="${listing.price.amount}"/>
                                </div>
                            </div>
                        </div>
                    </paw:card>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</account:layout>
</html>