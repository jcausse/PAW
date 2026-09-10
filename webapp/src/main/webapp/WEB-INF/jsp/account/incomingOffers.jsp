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
            <div class="overflow-x-auto">
                <table class="w-full text-left">
                    <thead>
                        <tr class="border-b border-black/10">
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.listing"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.buyer"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.amount"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.type"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.status"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.message"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.incomingOffers.table.actions"/>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="offer" items="${offers}">
                            <tr class="border-b border-black/5 hover:bg-black/5">
                                <td class="py-4 px-4">
                                    <c:url value="/listing/${offer.listing.id}" var="listingUrl"/>
                                    <a href="${listingUrl}" class="font-medium hover:text-lime-600 transition"><c:out value="${offer.listing.title}"/></a>
                                </td>
                                <td class="py-4 px-4">
                                    <paw:user user="${offer.buyer}" />
                                </td>
                                <td class="py-4 px-4 font-medium">
                                    $<c:out value="${offer.amount}"/>
                                </td>
                                <td class="py-4 px-4">
                                    <c:choose>
                                        <c:when test="${offer.isFullPrice}">
                                            <spring:message code="account.incomingOffers.type.fullPrice" var="fullPriceLabel"/>
                                            <c:out value="${fullPriceLabel}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="account.incomingOffers.type.custom" var="customLabel"/>
                                            <c:out value="${customLabel}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-4 px-4">
                                    <c:choose>
                                        <c:when test="${offer.status.name() == 'PENDING'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                <spring:message code="offer.status.PENDING" var="pendingLabel"/>
                                                <c:out value="${pendingLabel}"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${offer.status.name() == 'ACCEPTED'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-lime-100 text-lime-800">
                                                <spring:message code="offer.status.ACCEPTED" var="acceptedLabel"/>
                                                <c:out value="${acceptedLabel}"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${offer.status.name() == 'REJECTED'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                                <spring:message code="offer.status.REJECTED" var="rejectedLabel"/>
                                                <c:out value="${rejectedLabel}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-black/10 text-black/70">
                                                <c:out value="${offer.status.name()}"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-4 px-4 max-w-xs truncate">
                                    <c:choose>
                                        <c:when test="${not empty offer.message}">
                                            <c:out value="${offer.message}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-black/30 italic">
                                                <spring:message code="account.incomingOffers.noMessage" var="noMsgLabel"/>
                                                <c:out value="${noMsgLabel}"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-4 px-4">
                                    <c:choose>
                                        <c:when test="${offer.status.name() == 'PENDING'}">
                                            <c:url value="/offer/${offer.id}" var="offerUrl"/>
                                            <spring:message code="account.incomingOffers.table.view" var="viewLabel"/>
                                            <paw:linkButton href="${offerUrl}" text="${viewLabel}" variant="ghost" size="sm"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-black/30 text-sm">
                                                <spring:message code="account.incomingOffers.table.closed" var="closedLabel"/>
                                                <c:out value="${closedLabel}"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</account:layout>
</html>
