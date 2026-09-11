<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.incomingOffers.title" var="titleMsg"/>
<spring:message code="account.incomingOffers.subtitle" var="subtitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.incomingOffers.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <c:choose>
        <c:when test="${empty offers}">
            <div class="text-center py-12">
                <spring:message code="account.incomingOffers.empty" var="emptyMsg"/>
                <p class="text-black/50 text-lg"><c:out value="${emptyMsg}"/></p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid grid-cols-1 gap-4">
                <c:forEach var="offer" items="${offers}">
                    <c:set var="coverUrl" value=""/>
                    <c:if test="${not empty offer.listing.imageIds}">
                        <c:url value="/image/${offer.listing.imageIds[0]}" var="coverUrl"/>
                    </c:if>

                    <c:choose>
                        <c:when test="${offer.status.name() == 'PENDING'}">
                            <c:set var="statusClass" value="bg-yellow-100 text-yellow-800"/>
                            <spring:message code="offer.status.PENDING" var="statusLabel"/>
                        </c:when>
                        <c:when test="${offer.status.name() == 'ACCEPTED'}">
                            <c:set var="statusClass" value="bg-lime-100 text-lime-800"/>
                            <spring:message code="offer.status.ACCEPTED" var="statusLabel"/>
                        </c:when>
                        <c:when test="${offer.status.name() == 'REJECTED'}">
                            <c:set var="statusClass" value="bg-red-100 text-red-800"/>
                            <spring:message code="offer.status.REJECTED" var="statusLabel"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="statusClass" value="bg-black/10 text-black/70"/>
                            <c:set var="statusLabel" value="${offer.status.name()}"/>
                        </c:otherwise>
                    </c:choose>

                    <paw:card classname="flex flex-col h-full">
                        <div class="flex items-start justify-between gap-2">
                            <h3 class="text-base font-semibold flex-1 min-w-0 truncate">
                                <c:url value="/listing/${offer.listing.id}" var="listingUrl"/>
                                <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${offer.listing.title}"/></a>
                            </h3>
                            <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${statusClass} flex-shrink-0">
                                <c:out value="${statusLabel}"/>
                            </span>
                        </div>

                        <div class="flex gap-3 mt-2">
                            <div class="w-24 h-24 flex-shrink-0 rounded-lg overflow-hidden border border-black/10 bg-neutral-200">
                                <c:choose>
                                    <c:when test="${not empty coverUrl}">
                                        <img src="${coverUrl}" alt="<c:out value='${offer.listing.title}'/>" class="w-full h-full object-cover"/>
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
                                    <div class="text-black truncate"><c:out value="${offer.listing.product.brand}"/> <c:out value="${offer.listing.product.model}"/> (<c:out value="${offer.listing.product.year}"/>)</div>
                                    <div class="text-sm text-black/60 mt-1 truncate">
                                        <spring:message code="category.${offer.listing.product.subcategory.category.name}"/>
                                        /
                                        <spring:message code="subcategory.${offer.listing.product.subcategory.name}"/>
                                    </div>
                                </div>
                                <div class="text-xl font-bold mt-2">
                                    <c:if test="${not offer.isFullPrice}">
                                        <p class="text-lg font-medium line-through text-black/60">
                                            $<c:out value="${offer.listing.price.amount}"/>
                                        </p>
                                    </c:if>
                                    <p class="text-3xl font-bold">
                                        $<c:out value="${offer.amount}"/>
                                        <c:if test="${not offer.isFullPrice}">
                                            <c:set var="listingPrice" value="${offer.listing.price.amount}"/>
                                            <c:set var="offerAmount" value="${offer.amount}"/>
                                            <c:set var="discountPercent" value="${((listingPrice - offerAmount) / listingPrice) * 100}"/>
                                            <span class="text-red-600 text-xl"> -<c:out value="${String.format('%.0f', discountPercent)}"/>%</span>
                                        </c:if>
                                    </p>
                                </div>
                            </div>
                        </div>

                        <c:if test="${not empty offer.message}">
                            <paw:divider />
                            <details class="group mt-2">
                                <summary class="flex items-center gap-2 text-sm text-black/60 cursor-pointer list-none">
                                    <spring:message code="offer.decision.message" var="messageLabel"/>
                                    <c:out value="${messageLabel}"/>
                                    <span class="text-black/30 ml-auto">▼</span>
                                </summary>
                                <div class="mt-2 p-3 bg-neutral-50 rounded-lg text-black/90 whitespace-pre-wrap text-sm">
                                    <c:out value="${offer.message}"/>
                                </div>
                            </details>
                        </c:if>

                        <c:if test="${offer.status.name() == 'PENDING'}">
                            <paw:divider />
                            <c:url value="/offer/${offer.id}" var="offerUrl"/>
                            <spring:message code="account.incomingOffers.table.view" var="viewLabel"/>
                            <paw:linkButton href="${offerUrl}" text="${viewLabel}" variant="outline" size="sm" classname="w-full justify-center"/>
                        </c:if>
                    </paw:card>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</account:layout>
</html>