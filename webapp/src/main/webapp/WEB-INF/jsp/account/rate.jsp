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

<c:set var="isBuyer" value="${currentUser.isPresent() and offer.buyer.id == currentUser.get().id}"/>
<c:set var="ratedUser" value="${isBuyer ? offer.listing.creator : offer.buyer}"/>
<c:set var="ratingTargetLabel" value="${isBuyer ? 'account.rate.ratingSeller' : 'account.rate.ratingBuyer'}"/>

<c:url value="${isBuyer ? incomingOffersUrl : myOffersUrl}" var="backUrl"/>
<c:url value="${isBuyer ? incomingOffersUrl : myOffersUrl}?statusGroup=resolved" var="backUrlResolved"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.rate.title" />

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <!-- Back link -->
    <div class="mb-4">
        <spring:message code="${isBuyer ? 'account.incomingOffers.title' : 'account.myOffers.title'}" var="backLabel"/>
        <paw:linkButton href="${backUrlResolved}" text="${backLabel}" variant="ghost" icon="chevron-left" classname="justify-start" />
    </div>

    <paw:card classname="flex flex-col gap-4 max-w-2xl mx-auto">
        <!-- Offer information section -->
        <div class="p-4 rounded-lg border border-black/10">
            <h2 class="text-lg font-semibold text-black mb-4">
                <spring:message code="account.rate.offerInfo" var="offerInfoLabel"/>
                <c:out value="${offerInfoLabel}"/>
            </h2>

            <!-- Listing info -->
            <div class="flex flex-row gap-4 mb-4">
                <paw:listingImage listing="${offer.listing}" size="lg" />
                <div class="flex-1 min-w-0 flex flex-col justify-center">
                    <h3 class="text-base font-semibold truncate"><c:out value="${offer.listing.title}"/></h3>
                    <p class="text-sm text-black/60 mt-1">
                        <c:out value="${offer.listing.product.brand}"/>
                        <c:out value="${offer.listing.product.model}"/>
                        (<c:out value="${offer.listing.product.year}"/>)
                    </p>
                    <c:if test="${not offer.isFullPrice}">
                        <p class="text-sm line-through text-black/40 mt-1">$<c:out value="${offer.listing.price.amount}"/></p>
                    </c:if>
                    <p class="text-lg font-bold text-black mt-1">$<c:out value="${offer.amount}"/></p>
                </div>
            </div>

            <!-- Buyer/Seller info (user being rated) -->
            <div class="flex flex-row gap-4">
                <paw:user user="${ratedUser}" variant="compact" />
                <div class="flex-1 flex flex-col justify-center">
                    <spring:message code="${ratingTargetLabel}" var="ratingUserLabel"/>
                    <p class="text-sm font-medium text-black"><c:out value="${ratingUserLabel}"/></p>
                    <p class="text-sm text-black/50">
                        <spring:message code="account.rate.userJoined" arguments="${ratedUser.joinedAt}" var="joinedLabel"/>
                        <c:out value="${joinedLabel}"/>
                    </p>
                </div>
            </div>
        </div>

        <!-- Rating form -->
        <form:form modelAttribute="rateForm" action="${rateFormUrl}" method="POST" class="flex flex-col gap-4">
            
            <!-- Rating selection -->
            <div>
                <spring:message code="account.rate.ratingLabel" var="ratingLabel"/>
                <label class="block text-sm font-medium text-black/70 mb-2"><c:out value="${ratingLabel}"/></label>
                <div class="flex flex-row gap-4" role="radiogroup" aria-label="${ratingLabel}">
                    <spring:message code="account.rate.positive" var="positiveLabel"/>
                    <spring:message code="account.rate.neutral" var="neutralLabel"/>
                    <spring:message code="account.rate.negative" var="negativeLabel"/>
                    
                    <label class="flex flex-col items-center gap-2 cursor-pointer p-4 border-2 rounded-lg transition hover:bg-black/5 ${rateForm.rating == 'POSITIVE' ? 'border-lime-500 bg-lime-50' : 'border-black/10 hover:border-black/20'}">
                        <input type="radio" name="rating" value="POSITIVE" 
                               <c:if test="${rateForm.rating == 'POSITIVE'}">checked</c:if>
                               class="sr-only" required/>
                        <paw:icon name="arrow-up" classname="text-3xl text-lime-600" />
                        <span class="text-sm font-medium text-lime-700"><c:out value="${positiveLabel}"/></span>
                    </label>
                    
                    <label class="flex flex-col items-center gap-2 cursor-pointer p-4 border-2 rounded-lg transition hover:bg-black/5 ${rateForm.rating == 'NEUTRAL' ? 'border-stone-500 bg-stone-50' : 'border-black/10 hover:border-black/20'}">
                        <input type="radio" name="rating" value="NEUTRAL" 
                               <c:if test="${rateForm.rating == 'NEUTRAL'}">checked</c:if>
                               class="sr-only" required/>
                        <paw:icon name="minus" classname="text-3xl text-stone-500" />
                        <span class="text-sm font-medium text-black/60"><c:out value="${neutralLabel}"/></span>
                    </label>
                    
                    <label class="flex flex-col items-center gap-2 cursor-pointer p-4 border-2 rounded-lg transition hover:bg-black/5 ${rateForm.rating == 'NEGATIVE' ? 'border-red-500 bg-red-50' : 'border-black/10 hover:border-black/20'}">
                        <input type="radio" name="rating" value="NEGATIVE" 
                               <c:if test="${rateForm.rating == 'NEGATIVE'}">checked</c:if>
                               class="sr-only" required/>
                        <paw:icon name="arrow-down" classname="text-3xl text-red-600" />
                        <span class="text-sm font-medium text-red-700"><c:out value="${negativeLabel}"/></span>
                    </label>
                </div>
                <form:errors path="rating" cssClass="text-red-600 text-sm mt-1" element="div"/>
            </div>

            <!-- Review text -->
            <div>
                <spring:message code="account.rate.reviewLabel" var="reviewLabel"/>
                <spring:message code="account.rate.reviewPlaceholder" var="reviewPlaceholder"/>
                <label class="block text-sm font-medium text-black/70 mb-2"><c:out value="${reviewLabel}"/></label>
                <form:textarea path="reviewText" rows="4" class="w-full px-3 py-2 border border-black/10 rounded-lg focus:outline-none focus:ring-2 focus:ring-lime-500 focus:border-transparent transition" placeholder="${reviewPlaceholder}"/>
                <p class="text-xs text-black/40 mt-1">
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