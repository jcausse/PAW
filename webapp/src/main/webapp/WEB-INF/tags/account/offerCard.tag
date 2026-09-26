<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="offer" required="true" type="ar.edu.itba.paw.model.Offer" %>
<%@ attribute name="user" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<paw:card classname="flex flex-col h-full gap-2">
    <div class="flex items-start justify-between gap-2">
        <h3 class="text-base font-semibold flex-1 min-w-0 truncate">
            <c:url value="/listing/${offer.listing.id}" var="listingUrl"/>
            <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${offer.listing.title}"/></a>
        </h3>
        <c:choose>
            <c:when test="${offer.status.name() == 'PENDING'}">
                <c:set var="statusClass" value="text-amber-600"/>
                <spring:message code="offer.status.PENDING" var="statusLabel"/>
            </c:when>
            <c:when test="${offer.status.name() == 'PENDING_PAYMENT'}">
                <c:set var="statusClass" value="text-blue-600"/>
                <spring:message code="offer.status.PENDING_PAYMENT" var="statusLabel"/>
            </c:when>
        </c:choose>
        <paw:badge text="${statusLabel}" classname="ml-auto ${statusClass}" />
    </div>

    <div class="flex gap-3 mt-2">
        <paw:listingImage listing="${offer.listing}" />

        <div class="flex-1 min-w-0 flex flex-col justify-between">
            <paw:product product="${offer.listing.product}" size="sm" />

            <div class="mt-2 flex flex-row gap-4 items-end">
                <div>
                    <c:if test="${not offer.isFullPrice}">
                        <p class="text-base font-medium line-through text-black/60"> $<c:out value="${offer.listing.price.amount}"/> </p>
                    </c:if>
                    <p class="text-2xl font-bold">
                        $<c:out value="${offer.amount}"/>
                        <c:if test="${not offer.isFullPrice}">
                            <c:set var="listingPrice" value="${offer.listing.price.amount}"/>
                            <c:set var="offerAmount" value="${offer.amount}"/>
                            <c:set var="discountPercent" value="${((listingPrice - offerAmount) / listingPrice) * 100}"/>
                            <span class="text-red-600 text-lg"> -<c:out value="${String.format('%.0f', discountPercent)}"/>%</span>
                        </c:if>
                    </p>
                </div>

                <div class="max-w-48 ml-auto">
                    <c:choose>
                        <c:when test="${user == 'seller'}">
                            <paw:user user="${offer.listing.creator}" variant="compact" />
                        </c:when>
                        <c:when test="${user == 'buyer'}">
                            <paw:user user="${offer.buyer}" variant="compact" />
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="flex flex-row gap-1 self-end">
            <jsp:doBody />
        </div>
    </div>

    <c:if test="${offer.hasOtherOffers}">
        <spring:message code="${offer.hasBetterOffers ? 'offer.warning.betterOffers' : 'offer.warning.otherOffers'}" var="warningMsg"/>
        <paw:banner text="${warningMsg}" icon="triangle-alert" role="warning" />
    </c:if>

    <c:if test="${offer.proofOfPaymentId != null}">
        <spring:message code="offer.proofOfPayment.uploadedNotification" var="proofOfPaymentMsg"/>
        <paw:banner text="${proofOfPaymentMsg}" icon="badge-check" />
        <paw:collapsible title="Proof of Payment">
            <paw:offerProofOfPayment offer="${offer}" />
        </paw:collapsible>
    </c:if>

    <c:if test="${offer.proofOfShippingId != null || (offer.trackingNumber != null && !offer.trackingNumber.empty)}">
        <spring:message code="offer.proofOfShipping.uploadedNotification" var="proofOfShippingMsg"/>
        <paw:banner text="${proofOfShippingMsg}" icon="badge-check" />
        <paw:collapsible title="Proof of Shipping">
            <paw:offerProofOfShipping offer="${offer}" />
        </paw:collapsible>
    </c:if>

    <c:if test="${not empty offer.message}">
        <paw:divider />
        <spring:message code="offer.decision.message" var="messageLabel"/>
        <paw:collapsible title="${messageLabel}">
            <p class="text-sm whitespace-pre-wrap"><c:out value="${offer.message}"/></p>
        </paw:collapsible>
    </c:if>
</paw:card>
