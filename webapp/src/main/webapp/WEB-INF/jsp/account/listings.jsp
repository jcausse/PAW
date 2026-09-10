<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.listings.title" var="titleMsg"/>
<spring:message code="account.listings.subtitle" var="subtitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.listings.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <c:choose>
        <c:when test="${empty listings}">
            <div class="text-center py-12">
                <spring:message code="account.listings.empty" var="emptyMsg"/>
                <p class="text-black/50 text-lg mb-4"><c:out value="${emptyMsg}"/></p>
                <paw:linkButton text="${newListingLabel}" variant="primary" href="${newListingUrl}"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="overflow-x-auto">
                <table class="w-full text-left">
                    <thead>
                        <tr class="border-b border-black/10">
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.image"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.title"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.product"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.price"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.status"/>
                            </th>
                            <th class="py-3 px-4 font-semibold text-black/70">
                                <spring:message code="account.listings.table.actions"/>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="listing" items="${listings}">
                            <tr class="border-b border-black/5 hover:bg-black/5">
                                <td class="py-4 px-4">
                                    <c:set var="coverUrl" value=""/>
                                    <c:if test="${not empty listing.imageIds}">
                                        <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${not empty coverUrl}">
                                            <img src="${coverUrl}" alt="<c:out value='${listing.title}'/>" class="w-16 h-16 object-cover rounded-lg"/>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="w-16 h-16 rounded-lg bg-black/5 flex items-center justify-center">
                                                <spring:message code="card.noImage" var="noImageLabel"/>
                                                <span class="text-xs text-black/40"><c:out value="${noImageLabel}"/></span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-4 px-4">
                                    <c:url value="/listing/${listing.id}" var="listingUrl"/>
                                    <a href="${listingUrl}" class="font-medium hover:text-lime-600 transition"><c:out value="${listing.title}"/></a>
                                </td>
                                <td class="py-4 px-4">
                                    <c:out value="${listing.product.brand} ${listing.product.model} (${listing.product.year})"/>
                                </td>
                                <td class="py-4 px-4 font-medium">
                                    $<c:out value="${listing.price.amount}"/>
                                </td>
                                <td class="py-4 px-4">
                                    <c:choose>
                                        <c:when test="${listing.status.name() == 'ACTIVE'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-lime-100 text-lime-800">
                                                <spring:message code="listing.status.ACTIVE" var="activeLabel"/>
                                                <c:out value="${activeLabel}"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${listing.status.name() == 'SOLD'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-black/10 text-black/70">
                                                <spring:message code="listing.status.SOLD" var="soldLabel"/>
                                                <c:out value="${soldLabel}"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                <c:out value="${listing.status.name()}"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="py-4 px-4">
                                    <c:url value="/listing/${listing.id}" var="listingUrl"/>
                                    <spring:message code="account.listings.table.view" var="viewLabel"/>
                                    <paw:linkButton href="${listingUrl}" text="${viewLabel}" variant="ghost" size="sm"/>
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
