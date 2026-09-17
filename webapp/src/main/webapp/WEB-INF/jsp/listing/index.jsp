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
                <paw:card classname="relative">
                    <c:set var="imageUrlsList">
                        <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                            <c:url value="/image/${imageId}" var="imageUrl"/>
                            <c:out value="${imageUrl}"/>
                            <c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    </c:set>
                    <paw:imageGallery id="listing-gallery" images="${fn:split(imageUrlsList, ',')}" alt="${listing.title}" />
                    <paw:listingHotBadge listing="${listing}" />
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
                                <c:choose>
                                    <c:when test="${isCanceled}">
                                        <spring:message code="listing.detail.canceled" var="canceledLabel"/>
                                        <p class="text-center text-black/60 py-4"><c:out value="${canceledLabel}"/></p>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="listing.detail.edit" var="editLabel"/>
                                        <c:url value="/listing/${listing.id}/edit" var="editUrl"/>
                                        <paw:linkButton href="${editUrl}" size="lg" classname="w-full" variant="outline" text="${editLabel}"/>

                                        <spring:message code="listing.detail.cancel" var="cancelLabel"/>
                                        <button type="button"
                                                onclick="document.getElementById('cancelListingDialog').showModal()"
                                                class="w-full text-center text-sm text-red-600 hover:underline mt-2">
                                            <c:out value="${cancelLabel}"/>
                                        </button>
                                        <spring:message code="listing.cancel.confirm.title" var="cancelTitle"/>
                                        <spring:message code="listing.cancel.confirm.confirm" var="cancelConfirm"/>
                                        <spring:message code="listing.cancel.confirm.cancel" var="cancelCancel"/>
                                        <spring:message code="listing.cancel.confirm.message" var="cancelMessage"/>
                                        <c:url value="/listing/${listing.id}/cancel" var="cancelUrl"/>
                                        <paw:confirmDialog id="cancelListingDialog" title="${cancelTitle}" confirmText="${cancelConfirm}"
                                                            cancelText="${cancelCancel}" formAction="${cancelUrl}">
                                            <c:out value="${cancelMessage}"/>
                                        </paw:confirmDialog>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:when test="${isCanceled}">
                                <spring:message code="listing.detail.canceled" var="canceledLabel"/>
                                <p class="text-center text-black/60 py-4"><c:out value="${canceledLabel}"/></p>
                            </c:when>
                            <c:when test="${isSold}">
                                <spring:message code="listing.detail.alreadyPurchased" var="alreadyPurchasedLabel"/>
                                <p class="text-center text-black/60 py-4"><c:out value="${alreadyPurchasedLabel}"/></p>
                            </c:when>
                            <c:when test="${userPendingOffer.isPresent()}">
                                <div class="flex flex-col gap-2">
                                    <spring:message code="listing.detail.pendingOffer" arguments="${userPendingOffer.get().amount}" var="pendingOfferMsg"/>
                                    <p class="text-black/60 text-sm"><c:out value="${pendingOfferMsg}"/></p>
                                    <form action="<c:url value='/offer/${userPendingOffer.get().id}/withdraw'/>" method="POST">
                                        <spring:message code="account.myOffers.withdraw" var="withdrawLabel"/>
                                        <paw:button type="submit" variant="outline" role="danger" classname="w-full" icon="x" text="${withdrawLabel}" />
                                    </form>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="flex flex-col gap-2">
                                    <c:if test="${listing.pendingOffersCount > 0}">
                                        <c:choose>
                                            <c:when test="${listing.pendingOffersCount > 1}">
                                                <spring:message code="listing.detail.hotItem" arguments="${listing.pendingOffersCount}" var="hotItemMsg"/>
                                            </c:when>
                                            <c:otherwise>
                                                <spring:message code="listing.detail.hotItem1" arguments="${listing.pendingOffersCount}" var="hotItemMsg"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <p class="text-red-600 text-sm"><c:out value="${hotItemMsg}"/></p>
                                    </c:if>
                                    <spring:message code="listing.detail.makeOffer" var="makeOfferLabel"/>
                                    <c:url value="/checkout?listingId=${listing.id}" var="checkoutUrl"/>
                                    <paw:linkButton href="${checkoutUrl}" size="lg" classname="w-full" text="${makeOfferLabel}"/>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <paw:divider />

                        <c:url value="/listing/new/choose-product?productId=${listing.product.getId()}" var="sellSameProductUrl" />
                        <spring:message code="listing.detail.sellSameProduct" var="sellSameProductLabel" />
                        <paw:linkButton href="${sellSameProductUrl}" variant="ghost" size="sm" role="secondary" text="${sellSameProductLabel}" />
                    </div>
                </paw:card>
            </div>
        </div>
    </div>
</body>
</html>
