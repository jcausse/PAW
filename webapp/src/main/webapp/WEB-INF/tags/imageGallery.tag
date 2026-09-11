<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="images" required="true" type="java.lang.String[]" %>
<%@ attribute name="alt" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="imageGallery.noImages" var="noImagesLabel"/>

<c:set var="xmlSafeAlt" value="${fn:escapeXml(alt)}"/>

<div class="w-full">
    <c:choose>
        <c:when test="${not empty images}">
            <div class="relative aspect-video w-full rounded-lg overflow-hidden border border-black/10 bg-neutral-200 mb-3">
                <img
                    id="${id}-main"
                    src="${images[0]}"
                    alt="${empty xmlSafeAlt ? '' : xmlSafeAlt} - Image 1"
                    class="w-full h-full object-cover transition-opacity duration-200"
                />
            </div>

            <div
                id="${id}-thumbnails"
                class="flex gap-2 overflow-x-auto scroll-smooth pb-2 [scrollbar-width:none]"
                role="list"
                aria-label="Image thumbnails"
            >
                <c:forEach items="${images}" var="imageUrl" varStatus="status">
                    <c:set var="cleanImageUrl" value="${fn:trim(imageUrl)}"/>
                    <button
                        type="button"
                        class="flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all duration-200
                               ${status.first ? 'border-lime-600' : 'border-transparent hover:border-black/10'}
                               focus-visible:outline focus-visible:outline-lime-600 focus-visible:outline-offset-0
                               active:scale-[0.98]"
                        onclick="document.getElementById('${id}-main').src = '${cleanImageUrl}';
                               var thumbs = document.getElementById('${id}-thumbnails').querySelectorAll('button');
                               thumbs.forEach(function(btn) { btn.classList.remove('border-lime-600'); btn.classList.add('border-transparent'); });
                               this.classList.remove('border-transparent'); this.classList.add('border-lime-600');"
                        aria-label="${empty xmlSafeAlt ? 'Image' : xmlSafeAlt} ${status.count}"
                        role="listitem"
                    >
                        <img
                            src="${cleanImageUrl}"
                            alt=""
                            class="w-full h-full object-cover"
                        />
                    </button>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="w-full aspect-video bg-neutral-200 rounded-xl flex items-center justify-center">
                <span class="text-neutral-500 text-center px-4"><c:out value="${noImagesLabel}"/></span>
            </div>
        </c:otherwise>
    </c:choose>
</div>