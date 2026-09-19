<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="listing" required="true" type="ar.edu.itba.paw.model.Listing" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<c:if test="${listing.pendingOffersCount > 0}">
    <c:choose>
        <c:when test="${listing.pendingOffersCount > 1}">
            <spring:message code="listing.detail.hotItemBadge" arguments="${listing.pendingOffersCount}" var="badgeText"/>
        </c:when>
        <c:otherwise>
            <spring:message code="listing.detail.hotItemBadge1" arguments="${listing.pendingOffersCount}" var="badgeText"/>
        </c:otherwise>
    </c:choose>
    <div class="bg-white rounded-full absolute top-2 left-2">
        <paw:badge text="${badgeText}" classname="text-red-600"/>
    </div>
</c:if>
