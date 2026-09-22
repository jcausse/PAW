<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="offers" required="true" type="java.util.List" %>
<%@ attribute name="user" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<div class="overflow-x-auto mt-2">
    <table class="w-full text-left text-sm">
        <thead>
            <tr class="border-b border-black/10">
                <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.listing"/></th>
                <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.${user}"/></th>
                <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.amount"/></th>
                <th class="pb-2 font-medium text-black/60"><spring:message code="account.incomingOffers.table.status"/></th>
                <c:if test="${user == 'buyer'}">
                    <th class="pb-2 font-medium text-black/60"></th>
                </c:if>
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
                        <c:if test="${user == 'buyer'}"><paw:user user="${offer.buyer}" variant="compact" /></c:if>
                        <c:if test="${user == 'seller'}"><paw:user user="${offer.listing.creator}" variant="compact" /></c:if>
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
                    <c:if test="${user == 'buyer'}">
                        <td class="py-1">
                            <div class="flex flex-row gap-1 justify-end">
                                <c:url value="/offer/${offer.id}" var="offerUrl"/>
                                <paw:linkButton variant="outline" href="${offerUrl}" icon="eye" />
                            </div>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
