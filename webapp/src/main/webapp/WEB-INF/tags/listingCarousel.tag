<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="true" %>
<%@ attribute name="listings" required="true" type="java.util.List" %>
<%@ attribute name="seeMoreUrl" required="true" %>
<%@ attribute name="seeMoreLabel" required="true" %>
<%@ attribute name="id" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<spring:message code="carousel.previous" var="prevLabel"/>
<spring:message code="carousel.next" var="nextLabel"/>
<spring:message code="card.noImage" var="noImageLabel"/>

<div class="w-full">
    <div class="flex items-center justify-between mb-4">
        <div>
            <h2 class="text-xl font-bold"><c:out value="${title}"/></h2>
            <p class="text-sm text-black/60"><c:out value="${subtitle}"/></p>
        </div>
        <paw:linkButton href="${seeMoreUrl}" variant="ghost" size="sm" role="secondary" classname="self-end">
            <c:out value="${seeMoreLabel}"/>
            <paw:icon name="chevron-right" classname="w-4 h-4"/>
        </paw:linkButton>
    </div>

    <div class="relative group">
        <button
            type="button"
            onclick="document.getElementById('${id}').scrollBy({left: -280, behavior: 'smooth'})"
            class="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 z-10
                   w-9 h-9 rounded-full bg-white shadow-md border border-black/10
                   flex items-center justify-center cursor-pointer
                   opacity-0 group-hover:opacity-100 transition"
                   aria-label="<c:out value='${prevLabel}'/>"
        >‹</button>

        <div
            id="${id}"
            class="flex gap-4 overflow-x-auto scroll-smooth snap-x snap-mandatory [scrollbar-width:none] pb-4"
        >
            <c:forEach var="listing" items="${listings}" varStatus="status">
                <c:url value="/listing/${listing.id}" var="listingUrl"/>
                <c:set var="coverUrl" value=""/>
                <c:if test="${not empty listing.imageIds}">
                    <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                </c:if>
                <c:set var="subLabel" value=""/>
                <c:if test="${not empty listing.product and not empty listing.product.subcategory}">
                    <spring:message code="subcategory.${listing.product.subcategory.name}" var="subLabel"/>
                </c:if>
                <a href="${listingUrl}" class="block hover:-translate-y-0.5 transition snap-start shrink-0" style="width:260px;">
                    <paw:card title="${listing.title}" subtitle="${subLabel}"
                              showImage="true" imageUrl="${coverUrl}"
                              imageAlt="${listing.title}" noImageLabel="${noImageLabel}"
                              classname="h-full relative">
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
            </c:forEach>
        </div>

        <button
            type="button"
            onclick="document.getElementById('${id}').scrollBy({left: 280, behavior: 'smooth'})"
            class="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 z-10
                   w-9 h-9 rounded-full bg-white shadow-md border border-black/10
                   flex items-center justify-center cursor-pointer
                   opacity-0 group-hover:opacity-100 transition"
                   aria-label="<c:out value='${nextLabel}'/>"
        >›</button>
    </div>
</div>