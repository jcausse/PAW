<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>

<c:url value="/account/incoming-offers" var="incomingOffersUrl"/>
<c:url value="/account/my-offers" var="myOffersUrl"/>
<c:url value="/account/rate/${offer.id}" var="rateFormUrl"/>

<spring:message code="account.rate.title" var="titleMsg"/>
<spring:message code="account.rate.subtitle" var="subtitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.rate.title" />

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <!-- Back link -->
    <div class="mb-4">
        <c:choose>
            <c:when test="${rateForm.role == 'SELLER'}">
                <spring:message code="account.incomingOffers.title" var="backLabel"/>
                <paw:linkButton href="${incomingOffersUrl}?statusGroup=resolved" text="${backLabel}" variant="ghost" icon="chevron-left" classname="justify-start" />
            </c:when>
            <c:otherwise>
                <spring:message code="account.myOffers.title" var="backLabel"/>
                <paw:linkButton href="${myOffersUrl}?statusGroup=resolved" text="${backLabel}" variant="ghost" icon="chevron-left" classname="justify-start" />
            </c:otherwise>
        </c:choose>
    </div>

    <paw:card classname="flex flex-col gap-4 max-w-2xl mx-auto">
        <!-- Offer information section -->
        <div class="p-4 bg-stone-50 rounded-lg border border-stone-200">
            <h2 class="text-lg font-semibold text-stone-900 mb-4">
                <spring:message code="account.rate.offerInfo" var="offerInfoLabel"/>
                <c:out value="${offerInfoLabel}"/>
            </h2>

            <!-- Listing info -->
            <div class="flex flex-row gap-4 mb-4">
                <paw:listingImage listing="${offer.listing}" size="lg" />
                <div class="flex-1 min-w-0 flex flex-col justify-center">
                    <h3 class="text-base font-semibold truncate"><c:out value="${offer.listing.title}"/></h3>
                    <p class="text-sm text-stone-600 mt-1">
                        <c:out value="${offer.listing.product.brand}"/>
                        <c:out value="${offer.listing.product.model}"/>
                        (<c:out value="${offer.listing.product.year}"/>)
                    </p>
                    <c:if test="${not offer.isFullPrice}">
                        <p class="text-sm line-through text-stone-500 mt-1">$<c:out value="${offer.listing.price.amount}"/></p>
                    </c:if>
                    <p class="text-lg font-bold text-stone-900 mt-1">$<c:out value="${offer.amount}"/></p>
                </div>
            </div>

            <!-- Buyer/Seller info (user being rated) -->
            <div class="flex flex-row gap-4">
                <paw:user user="${rateForm.role == 'SELLER' ? offer.listing.creator : offer.buyer}" variant="compact" />
                <div class="flex-1 flex flex-col justify-center">
                    <c:choose>
                        <c:when test="${rateForm.role == 'SELLER'}">
                            <spring:message code="account.rate.ratingSeller" var="ratingUserLabel"/>
                        </c:when>
                        <c:otherwise>
                            <spring:message code="account.rate.ratingBuyer" var="ratingUserLabel"/>
                        </c:otherwise>
                    </c:choose>
                    <p class="text-sm font-medium text-stone-700"><c:out value="${ratingUserLabel}"/></p>
                    <p class="text-sm text-stone-500">
                        <spring:message code="account.rate.userJoined" arguments="${(rateForm.role == 'SELLER' ? offer.listing.creator : offer.buyer).joinedAt}" var="joinedLabel"/>
                        <c:out value="${joinedLabel}"/>
                    </p>
                </div>
            </div>
        </div>

        <!-- Rating form -->
        <form:form modelAttribute="rateForm" action="${rateFormUrl}" method="POST" class="flex flex-col gap-4">
            <!-- Hidden fields -->
            <form:hidden path="role"/>
            
            <!-- Rating selection -->
            <div>
                <spring:message code="account.rate.ratingLabel" var="ratingLabel"/>
                <label class="block text-sm font-medium text-stone-700 mb-2"><c:out value="${ratingLabel}"/></label>
                <div class="flex flex-row gap-4" role="radiogroup" aria-label="${ratingLabel}">
                    <c:forEach var="ratingOption" items="${ratingOptions}">
                        <label class="flex flex-col items-center gap-2 cursor-pointer p-4 border-2 rounded-lg transition hover:bg-stone-50 ${rateForm.rating == ratingOption.value ? 'border-lime-500 bg-lime-50' : 'border-stone-200 hover:border-stone-300'}">
                            <input type="radio" name="rating" value="${ratingOption.value}" 
                                   <c:if test="${rateForm.rating == ratingOption.value}">checked</c:if>
                                   class="sr-only" required/>
                            <paw:icon name="${ratingOption.icon}" classname="text-3xl ${ratingOption.iconColor}" />
                            <spring:message code="account.rate.${ratingOption.value.toLowerCase()}" var="ratingOptionLabel"/>
                            <span class="text-sm font-medium ${ratingOption.textColor}"><c:out value="${ratingOptionLabel}"/></span>
                        </label>
                    </c:forEach>
                </div>
                <form:errors path="rating" cssClass="text-red-600 text-sm mt-1" element="div"/>
            </div>

            <!-- Review text -->
            <div>
                <spring:message code="account.rate.reviewLabel" var="reviewLabel"/>
                <spring:message code="account.rate.reviewPlaceholder" var="reviewPlaceholder"/>
                <label class="block text-sm font-medium text-stone-700 mb-2"><c:out value="${reviewLabel}"/></label>
                <form:textarea path="reviewText" rows="4" class="w-full px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500 focus:border-transparent transition" placeholder="${reviewPlaceholder}"/>
                <p class="text-xs text-stone-500 mt-1">
                    <spring:message code="account.rate.reviewHint" var="reviewHint"/>
                    <c:out value="${reviewHint}"/>
                </p>
                <form:errors path="reviewText" cssClass="text-red-600 text-sm mt-1" element="div"/>
            </div>

            <!-- Submit button -->
            <div class="pt-4">
                <spring:message code="account.rate.submit" var="submitLabel"/>
                <paw:button type="submit" variant="default" size="lg" classname="w-full" text="${submitLabel}" icon="star"/>
            </div>
        </form:form>
    </paw:card>
</account:layout>
</html>