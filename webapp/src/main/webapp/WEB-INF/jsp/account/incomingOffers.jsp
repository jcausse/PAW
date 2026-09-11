<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.incomingOffers.title" var="titleMsg"/>
<spring:message code="account.incomingOffers.subtitle" var="subtitleMsg"/>
<spring:message code="account.incomingOffers.pendingTitle" var="pendingTitleMsg"/>
<spring:message code="account.incomingOffers.resolvedTitle" var="resolvedTitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.incomingOffers.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <c:choose>
        <c:when test="${empty pendingOffers and empty resolvedOffers}">
            <div class="text-center py-12">
                <spring:message code="account.incomingOffers.empty" var="emptyMsg"/>
                <p class="text-black/50 text-lg"><c:out value="${emptyMsg}"/></p>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty pendingOffers}">
                <h2 class="text-lg font-semibold mb-4"><c:out value="${pendingTitleMsg}"/></h2>
                <div class="flex flex-col gap-4 mb-8">
                    <c:forEach var="offer" items="${pendingOffers}">
                        <c:set var="coverUrl" value=""/>
                        <c:if test="${not empty offer.listing.imageIds}">
                            <c:url value="/image/${offer.listing.imageIds[0]}" var="coverUrl"/>
                        </c:if>

                        <paw:card classname="flex flex-col h-full gap-2">
                            <div class="flex items-start justify-between gap-2">
                                <h3 class="text-base font-semibold flex-1 min-w-0 truncate">
                                    <c:url value="/listing/${offer.listing.id}" var="listingUrl"/>
                                    <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${offer.listing.title}"/></a>
                                </h3>
                                <c:set var="statusClass" value="text-amber-600"/>
                                <spring:message code="offer.status.PENDING" var="statusLabel"/>
                                <paw:badge text="${statusLabel}" classname="ml-auto ${statusClass}" />
                            </div>

                            <div class="flex gap-3 mt-2">
                                <paw:listingImage listing="${offer.listing}" />

                                <div class="flex-1 min-w-0 flex flex-col justify-between">
                                    <paw:product product="${offer.listing.product}" size="sm" />
                                    <div class="mt-2 flex flex-row gap-4 items-end justify-between">
                                        <div>
                                            <c:if test="${not offer.isFullPrice}">
                                                <p class="text-base font-medium line-through text-black/60">
                                                    $<c:out value="${offer.listing.price.amount}"/>
                                                </p>
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
                                        <div class="max-w-48">
                                            <paw:user user="${offer.buyer}" variant="compact" />
                                        </div>
                                    </div>
                                </div>

                                <div class="flex flex-row gap-1 self-end">
                                    <c:url value="/offer/${offer.id}" var="offerUrl"/>
                                    <paw:linkButton variant="outline" href="${offerUrl}" icon="eye" />
                                    <form action="<c:url value='/offer/${offer.id}/accept'/>" method="POST">
                                        <paw:button type="submit" variant="outline" icon="check" />
                                    </form>
                                    <form action="<c:url value='/offer/${offer.id}/reject'/>" method="POST">
                                        <paw:button type="submit" variant="outline" role="danger" icon="x" />
                                    </form>
                                </div>
                            </div>

                            <c:if test="${offer.hasOtherOffers}">
                                <div class="p-3 rounded-lg bg-amber-50 border border-amber-200 mt-2 flex flex-row gap-2 text-amber-800 items-center">
                                    <paw:icon name="triangle-alert" />
                                    <spring:message code="${offer.hasBetterOffers ? 'offer.warning.betterOffers' : 'offer.warning.otherOffers'}" var="warningMsg"/>
                                    <p class="text-sm"><c:out value="${warningMsg}"/></p>
                                </div>
                            </c:if>

                            <c:if test="${not empty offer.message}">
                                <paw:divider />
                                <spring:message code="offer.decision.message" var="messageLabel"/>
                                <paw:collapsible title="${messageLabel}">
                                    <p class="text-sm whitespace-pre-wrap"><c:out value="${offer.message}"/></p>
                                </paw:collapsible>
                            </c:if>
                        </paw:card>
                    </c:forEach>
                </div>
            </c:if>

            <c:if test="${not empty resolvedOffers}">
                <paw:card title="${resolvedTitleMsg}">
                    <div class="overflow-x-auto mt-2">
                        <table class="w-full text-left text-sm">
                            <thead>
                                <tr class="border-b border-black/10">
                                    <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.listing"/></th>
                                    <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.buyer"/></th>
                                    <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.amount"/></th>
                                    <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.status"/></th>
                                    <th class="pb-2 font-medium text-black/60"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="offer" items="${resolvedOffers}">
                                    <c:choose>
                                        <c:when test="${offer.status.name() == 'ACCEPTED'}">
                                            <c:set var="statusClass" value="text-lime-600"/>
                                            <spring:message code="offer.status.ACCEPTED" var="statusLabel"/>
                                        </c:when>
                                        <c:when test="${offer.status.name() == 'REJECTED'}">
                                            <c:set var="statusClass" value="text-red-600"/>
                                            <spring:message code="offer.status.REJECTED" var="statusLabel"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="statusClass" value="text-neutral-600"/>
                                            <c:set var="statusLabel" value="${offer.status.name()}"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <tr class="border-b border-black/5 last:border-0">
                                        <td class="py-1">
                                            <c:url value="/listing/${offer.listing.id}" var="listingUrl"/>
                                            <a href="${listingUrl}" class="hover:text-lime-600 transition font-medium"><c:out value="${offer.listing.title}"/></a>
                                        </td>
                                        <td class="py-1 pr-2">
                                            <paw:user user="${offer.buyer}" variant="compact" />
                                        </td>
                                        <td class="py-1 font-semibold">
                                            <div>
                                                <c:if test="${not offer.isFullPrice}">
                                                    <p class="text-xs font-medium line-through text-black/60">
                                                        $<c:out value="${offer.listing.price.amount}"/>
                                                    </p>
                                                </c:if>
                                                $<c:out value="${offer.amount}"/>
                                            </div>
                                        </td>
                                        <td class="py-1">
                                            <div class="flex flex-row">
                                                <paw:badge text="${statusLabel}" classname="${statusClass}" size="sm" />
                                            </div>
                                        </td>
                                        <td class="py-1">
                                            <div class="flex flex-row gap-1 justify-end">
                                                <c:url value="/offer/${offer.id}" var="offerUrl"/>
                                                <paw:linkButton variant="outline" href="${offerUrl}" icon="eye" />
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </paw:card>
            </c:if>
        </c:otherwise>
    </c:choose>
</account:layout>
</html>
