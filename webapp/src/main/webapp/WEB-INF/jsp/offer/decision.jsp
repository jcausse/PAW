<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="offer.decision.title" />
<body class="min-h-screen bg-neutral-50">
    <paw:navbar />

    <div class="max-w-2xl mx-auto p-8 pb-24">
        <paw:card classname="w-full">
            <div class="flex flex-col gap-4">
                <div class="text-center">
                    <spring:message code="offer.decision.title"
                                    arguments="${offer.buyer.displayName},${offer.amount},${offer.listing.product.brand},${offer.listing.product.model},${offer.listing.product.year}"
                                    var="title"/>
                    <h1 class="text-xl font-medium text-balance"><c:out value="${title}"/></h1>

                    <spring:message code="offer.decision.description" var="description"/>
                    <p class="text-black/60 mt-2 text-balance"><c:out value="${description}"/></p>
                </div>

                <hr class="border-t-0 border-b border-black/10">

                <!-- Listing info section -->
                <div class="flex flex-row gap-4">
                    <!-- Listing image (smaller, to the side) -->
                    <c:set var="listingImageUrl" value=""/>
                    <c:forEach items="${offer.listing.imageIds}" var="imageId" varStatus="status">
                        <c:if test="${status.first}">
                            <c:url value="/image/${imageId}" var="listingImageUrl"/>
                        </c:if>
                    </c:forEach>
                    <div class="w-32 h-32 flex-shrink-0 rounded-xl overflow-hidden border border-black/10 bg-neutral-200">
                        <c:choose>
                            <c:when test="${not empty listingImageUrl}">
                                <img src="${listingImageUrl}" alt="<c:out value='${offer.listing.title}'/>" class="w-full h-full object-cover"/>
                            </c:when>
                            <c:otherwise>
                                <div class="w-full h-full grid place-items-center text-black/30 text-xs px-2 text-center">
                                    <spring:message code="card.noImage"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Listing title and product info -->
                    <div class="flex-1 min-w-0 flex flex-col justify-center">
                        <h2 class="text-xl font-semibold truncate"><c:out value="${offer.listing.title}"/></h2>
                        <p class="text-sm text-black/60 mt-1 truncate">
                            <c:out value="${offer.listing.product.brand}"/>
                            <c:out value="${offer.listing.product.model}"/>
                            (<c:out value="${offer.listing.product.year}"/>)
                        </p>
                    </div>
                </div>

                <div class="flex flex-col gap-4">
                    <spring:message code="offer.decision.buyer" var="buyerLabel"/>
                    <div class="flex flex-col">
                        <span class="text-sm text-black/60"><c:out value="${buyerLabel}"/></span>
                        <c:url value="/profile/${offer.buyer.id}" var="buyerProfileUrl"/>
                        <paw:linkButton href="${buyerProfileUrl}" variant="ghost" classname="w-full justify-start px-0 gap-3">
                            <div class="flex flex-row gap-2 items-center text-sm">
                                <div class="rounded-full border border-black/10 w-10 h-10 grid place-items-center overflow-hidden flex-shrink-0">
                                    <c:choose>
                                        <c:when test="${offer.buyer.getImageId().isPresent()}">
                                            <img src="<c:url value='/image/${offer.buyer.getImageId().get()}'/>" alt="<c:out value='${offer.buyer.displayName}'/> Profile Picture" class="w-full h-full object-cover"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="<c:url value='/static-image/defaultProfilePicture.svg'/>" alt="Default Profile Picture" class="w-full h-full object-cover"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <p>
                                    <span class="text-black font-normal"><c:out value="${offer.buyer.displayName}"/></span>
                                    <span class="text-black/60 font-normal">(<c:out value="${offer.buyer.username}"/>)</span>
                                 </p>
                            </div>
                        </paw:linkButton>
                    </div>
                </div>

                <div class="flex flex-col">
                    <spring:message code="offer.decision.amount" var="amountLabel"/>
                    <span class="text-sm text-black/60"><c:out value="${amountLabel}"/></span>
                    <c:if test="${not offer.isFullPrice}">
                        <p class="text-lg font-medium line-through text-black/60">
                            $<c:out value="${offer.listing.price.getAmount()}"/>
                        </p>
                    </c:if>
                    <p class="text-3xl font-bold">
                        $<c:out value="${offer.amount}"/>
                        <c:if test="${not offer.isFullPrice}">
                            <c:set var="listingPrice" value="${offer.listing.price.getAmount()}"/>
                            <c:set var="offerAmount" value="${offer.amount}"/>
                            <c:set var="discountPercent" value="${((listingPrice - offerAmount) / listingPrice) * 100}"/>
                            <span class="text-red-600 text-xl"> -<c:out value="${String.format('%.0f', discountPercent)}"/>%</span>
                        </c:if>
                    </p>
                </div>

                <c:if test="${not empty offer.message}">
                    <hr class="border-t-0 border-b border-black/10">
                    <spring:message code="offer.decision.message" var="messageLabel"/>
                    <div class="flex flex-col">
                        <span class="text-sm text-black/60"><c:out value="${messageLabel}"/></span>
                        <p class="text-black/90 whitespace-pre-wrap"><c:out value="${offer.message}"/></p>
                    </div>
                </c:if>

                <hr class="border-t-0 border-b border-black/10">

                <div class="flex flex-row gap-4">
                    <spring:message code="offer.decision.accept" var="acceptLabel"/>
                    <form action="<c:url value='/offer/${offer.id}/accept'/>" method="POST" class="flex-1">
                        <paw:button type="submit" variant="default" size="lg" classname="w-full" text="${acceptLabel}"/>
                    </form>

                    <spring:message code="offer.decision.reject" var="rejectLabel"/>
                    <form action="<c:url value='/offer/${offer.id}/reject'/>" method="POST" class="flex-1">
                        <paw:button type="submit" variant="default" role="danger" size="lg" classname="w-full" text="${rejectLabel}"/>
                    </form>
                </div>
            </div>
        </paw:card>
    </div>
</body>
</html>
