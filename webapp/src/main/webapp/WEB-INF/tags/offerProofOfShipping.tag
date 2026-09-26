<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="offer" required="true" type="ar.edu.itba.paw.model.Offer" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" />


<c:url value="/offer/${offer.id}/proof-of-shipping/download" var="downloadUrl"/>

<div class="flex flex-col gap-2">
    <c:if test="${offer.trackingNumber != null && !offer.trackingNumber.empty}">
        <spring:message code="offer.proofOfShipping.trackingNumberLabel" var="trackingLabel"/>
        <div class="flex items-center gap-2 p-3 bg-neutral-50 rounded-lg border border-neutral-200">
            <paw:icon name="truck" classname="w-5 h-5 text-neutral-600" />
            <div class="flex-1 min-w-0">
                <span class="text-sm text-neutral-500"><c:out value="${trackingLabel}"/></span>
                <span class="text-sm font-medium text-neutral-900 truncate block"><c:out value="${offer.trackingNumber}"/></span>
            </div>
        </div>
    </c:if>
    <c:if test="${offer.proofOfShippingId != null}">
        <paw:linkButton variant="ghost" href="${downloadUrl}" icon="download" classname="w-full justify-start text-start px-3!">
            <div class="flex flex-col items-start ml-1">
                <span class="text-black font-medium"><c:out value="${offer.proofOfShippingFilename}"/></span>
                <span class="text-xs text-black/60 font-normal">
                    <c:out value="${offer.getProofOfShippingExtension()}"/>
                    &nbsp;|&nbsp;
                    <c:out value="${offer.getProofOfShippingSizeKb()}"/> KB
                </span>
            </div>
        </paw:linkButton>
        <c:if test="${offer.proofOfShippingIsImage()}">
            <img src="${downloadUrl}" alt="Proof of shipping" class="max-w-full h-auto rounded-lg border border-black/10" />
        </c:if>
    </c:if>
</div>