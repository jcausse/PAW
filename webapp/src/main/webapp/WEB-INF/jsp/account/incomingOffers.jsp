<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<c:url value="/account/incoming-offers" var="filterAction"/>
<spring:message code="account.incomingOffers.title" var="titleMsg"/>
<spring:message code="account.incomingOffers.subtitle" var="subtitleMsg"/>
<spring:message code="account.incomingOffers.pendingTitle" var="pendingTitleMsg"/>
<spring:message code="account.incomingOffers.pendingPaymentTitle" var="pendingPaymentTitleMsg"/>
<spring:message code="account.incomingOffers.resolvedTitle" var="resolvedTitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.incomingOffers.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <form:form modelAttribute="filterForm" action="${filterAction}" method="get" id="filterForm">
        <paw:formButtonToggle path="statusGroup" items="${statusGroupOptions}" selectedOption="${filterForm.getStatusGroup()}" classname="mb-4" />

        <c:choose>
            <c:when test="${empty offers}">
                <div class="text-center py-12">
                    <spring:message code="account.incomingOffers.empty" var="emptyMsg"/>
                    <p class="text-black/50 text-lg"><c:out value="${emptyMsg}"/></p>
                </div>
            </c:when>
            <c:when test="${filterForm.getStatusGroup() == 'pending' || filterForm.getStatusGroup() == 'pending_payment'}">
                <div class="flex flex-col gap-4 mb-8">
                    <c:forEach var="offer" items="${offers}">
                        <account:offerCard offer="${offer}" user="buyer">
                            <c:url value="/offer/${offer.id}" var="offerUrl"/>
                            <paw:linkButton variant="outline" href="${offerUrl}" icon="eye" />
                            <c:if test="${offer.status.name() == 'PENDING_PAYMENT'}">
                                <form action="<c:url value='/offer/${offer.id}/confirm-payment'/>" method="POST">
                                    <paw:button type="submit" variant="outline" icon="check" />
                                </form>
                            </c:if>
                            <c:if test="${offer.status.name() == 'PENDING'}">
                                <form action="<c:url value='/offer/${offer.id}/accept'/>" method="POST">
                                    <paw:button type="submit" variant="outline" icon="check" />
                                </form>
                            </c:if>
                            <form action="<c:url value='/offer/${offer.id}/reject'/>" method="POST">
                                <paw:button type="submit" variant="outline" role="danger" icon="x" />
                            </form>
                        </account:offerCard>
                    </c:forEach>

                    <paw:pagination page="${offerPage}" baseUrl="/account/incoming-offers"/>
                </div>
            </c:when>
            <c:when test="${filterForm.getStatusGroup() == 'resolved'}">
                <paw:card>
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
                                <c:forEach var="offer" items="${offers}">
                                    <c:choose>
                                        <c:when test="${offer.status.name() == 'ACCEPTED'}">
                                            <c:set var="statusClass" value="text-lime-600"/>
                                            <spring:message code="offer.status.ACCEPTED" var="statusLabel"/>
                                        </c:when>
                                        <c:when test="${offer.status.name() == 'REJECTED'}">
                                            <c:set var="statusClass" value="text-red-600"/>
                                            <spring:message code="offer.status.REJECTED" var="statusLabel"/>
                                        </c:when>
                                        <c:when test="${offer.status.name() == 'WITHDRAWN'}">
                                            <c:set var="statusClass" value="text-stone-600"/>
                                            <spring:message code="offer.status.WITHDRAWN" var="statusLabel"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="statusClass" value="text-stone-600"/>
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

                    <paw:pagination page="${offerPage}" baseUrl="/account/incoming-offers"/>
                </paw:card>
            </c:when>
        </c:choose>
    </form:form>
</account:layout>
</html>
