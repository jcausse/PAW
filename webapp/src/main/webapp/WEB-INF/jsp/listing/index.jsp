<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                    <c:choose>
                        <c:when test="${not empty listing.imageIds}">
                            <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                                <c:if test="${status.first}">
                                    <img
                                        src="<c:url value='/image/${imageId}'/>"
                                        alt="<c:out value='${listing.title}'/> - Image ${status.count}"
                                        class="w-full h-auto object-cover rounded-lg border border-black/10"
                                    >
                                </c:if>
                            </c:forEach>
                            <c:if test="${listing.imageIds.size() > 1}">
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-2 mt-2">
                                    <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                                        <c:if test="${not status.first}">
                                            <img
                                                src="<c:url value='/image/${imageId}'/>"
                                                alt="<c:out value='${listing.title}'/> - Image ${status.count}"
                                                class="w-full h-auto object-cover rounded-lg border border-black/10"
                                            >
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <div class="w-full aspect-video bg-neutral-200 rounded-xl flex items-center justify-center">
                                <span class="text-neutral-500 text-center px-4"><spring:message code="listing.detail.noImages"/></span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </paw:card>
            </div>

            <div class="flex-1 min-w-md">
                <paw:card>
                    <div class="flex flex-col gap-4">
                        <h1 class="text-2xl font-semibold"><c:out value="${listing.title}"/></h1>
                        <paw:product product="${listing.product}" />

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
                                <spring:message code="listing.detail.makeOffer" var="makeOfferLabel"/>
                                <c:url value="/checkout?listingId=${listing.id}" var="checkoutUrl"/>
                                <paw:linkButton href="${checkoutUrl}" size="lg" classname="w-full" text="${makeOfferLabel}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </paw:card>
            </div>
        </div>
    </div>
</body>
</html>
