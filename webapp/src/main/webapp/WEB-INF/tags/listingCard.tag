<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="listing" required="true" type="ar.edu.itba.paw.model.Listing" %>
<%@ attribute name="listingUrl" required="true" %>
<%@ attribute name="noImageLabel" required="false" %>
<%@ attribute name="cardClassname" required="false" %>
<%@ attribute name="linkClassname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="coverUrl" value=""/>
<c:if test="${not empty listing.imageIds}">
    <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
</c:if>
<c:set var="subLabel" value=""/>
<c:if test="${not empty listing.product and not empty listing.product.subcategory}">
    <spring:message code="subcategory.${listing.product.subcategory.name}" var="subLabel"/>
</c:if>
<a href="${listingUrl}" class="block hover:-translate-y-0.5 transition ${linkClassname}">
    <paw:card title="${listing.title}" subtitle="${subLabel}"
              showImage="true" imageUrl="${coverUrl}"
              imageAlt="${listing.title}" noImageLabel="${noImageLabel}"
              classname="h-full relative ${cardClassname}">
        <paw:listingHotBadge listing="${listing}" />
        <div class="flex items-center gap-2 flex-wrap mt-auto">
            <p class="text-xl font-bold">$<c:out value="${listing.price.amount}"/></p>
            <c:if test="${listing.acceptsTrade}">
                <span class="text-xs text-black/50 bg-black/5 rounded-full px-2 py-0.5">
                    <spring:message code="discovery.acceptsTrade.badge"/>
                </span>
            </c:if>
        </div>
    </paw:card>
</a>