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
        <paw:button
            type="button"
            onclick="document.getElementById('${id}').scrollBy({left: -280, behavior: 'smooth'})"
            variant="outline"
            icon="chevron-left"
            classname="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 z-10 opacity-0 group-hover:opacity-100 transition"
            ariaLabel="${prevLabel}"
        />

        <div
            id="${id}"
            class="flex gap-4 overflow-x-auto scroll-smooth snap-x snap-mandatory [scrollbar-width:none] pb-4"
        >
            <c:forEach var="listing" items="${listings}" varStatus="status">
                <c:url value="/listing/${listing.id}" var="listingUrl"/>
                <paw:listingCard
                    listing="${listing}"
                    listingUrl="${listingUrl}"
                    noImageLabel="${noImageLabel}"
                    linkClassname="snap-start shrink-0"
                />
            </c:forEach>
        </div>

        <paw:button
            type="button"
            onclick="document.getElementById('${id}').scrollBy({left: 280, behavior: 'smooth'})"
            variant="outline"
            icon="chevron-right"
            classname="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 z-10 opacity-0 group-hover:opacity-100 transition"
            ariaLabel="${nextLabel}"
        />
    </div>
</div>