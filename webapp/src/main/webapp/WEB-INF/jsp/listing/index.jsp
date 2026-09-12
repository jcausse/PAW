<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="listing.detail.title" />
<body class="min-h-screen bg-neutral-50">
    <paw:navbar />

    <div class="max-w-5xl mx-auto p-8 pb-24">
        <div class="flex flex-row gap-4">
            <div class="flex-2 min-w-0">
                <paw:card>
                    <c:set var="imageUrlsList">
                        <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                            <c:url value="/image/${imageId}" var="imageUrl"/>
                            <c:out value="${imageUrl}"/>
                            <c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    </c:set>
                    <paw:imageGallery id="listing-gallery" images="${fn:split(imageUrlsList, ',')}" alt="${listing.title}"/>
                </paw:card>
            </div>

            <div class="flex-1 min-w-sm">
                <paw:card>
                    <div class="flex flex-col gap-4">
                        <h1 class="text-2xl font-semibold"><c:out value="${listing.title}"/></h1>
                        <paw:product product="${listing.product}" />

                        <paw:divider />

                        <spring:message code="condition.${listing.condition}" var="conditionLabel"/>
                        <spring:message code="condition.description.${listing.condition}" var="conditionDescription"/>
                        <paw:collapsible title="Condition: ${conditionLabel}" classname="w-full">
                            <p class="text-sm text-black/70"><c:out value="${conditionDescription}"/></p>
                        </paw:collapsible>

                        <paw:divider />

                        <paw:user user="${listing.creator}" />
                        <p class="text-3xl font-bold">$<c:out value="${listing.price.getAmount()}"/></p>

                        <c:choose>
                            <c:when test="${isCreator}">
                                <spring:message code="listing.detail.cannotOfferOwn" var="cannotOfferLabel"/>
                                <p class="text-center text-black/60 py-4"><c:out value="${cannotOfferLabel}"/></p>
                            </c:when>
                            <c:when test="${isSold}">
                                <spring:message code="listing.detail.alreadyPurchased" var="alreadyPurchasedLabel"/>
                                <p class="text-center text-black/60 py-4"><c:out value="${alreadyPurchasedLabel}"/></p>
                            </c:when>
                            <c:otherwise>
                                <div class="flex flex-col gap-2">
                                    <c:if test="${listing.pendingOffersCount > 0}">
                                        <spring:message code="listing.detail.hotItem" arguments="${listing.pendingOffersCount}" var="hotItemMsg"/>
                                        <p class="text-red-600 text-sm"><c:out value="${hotItemMsg}"/></p>
                                    </c:if>
                                    <spring:message code="listing.detail.makeOffer" var="makeOfferLabel"/>
                                    <c:url value="/checkout?listingId=${listing.id}" var="checkoutUrl"/>
                                    <paw:linkButton href="${checkoutUrl}" size="lg" classname="w-full" text="${makeOfferLabel}"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </paw:card>
            </div>
        </div>
    </div>
</body>
</html>
