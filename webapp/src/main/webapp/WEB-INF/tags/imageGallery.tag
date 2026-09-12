<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="images" required="true" type="java.lang.String[]" %>
<%@ attribute name="alt" required="false" %>
<%@ attribute name="badgeText" required="false" %>
<%@ attribute name="badgeColor" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<spring:message code="imageGallery.noImages" var="noImagesLabel"/>

<c:set var="xmlSafeAlt" value="${fn:escapeXml(alt)}"/>

<div class="w-full">
    <c:choose>
        <c:when test="${not empty images}">
            <div class="relative aspect-square w-full rounded-lg overflow-hidden border border-black/10 bg-neutral-200">
                <img
                    id="${id}_main_bg"
                    src="${images[0]}"
                    alt=""
                    class="object-cover absolute inset-0 w-full h-full blur-2xl opacity-30"
                />
                <img
                    id="${id}_main"
                    src="${images[0]}"
                    alt="${empty xmlSafeAlt ? '' : xmlSafeAlt} - Image 1"
                    class="w-full h-full object-contain relative"
                />
                <c:if test="${not empty badgeText}">
                    <div class="absolute top-2 left-2 z-10">
                        <paw:badge text="${badgeText}" color="${badgeColor}" size="sm" classname="text-red-600 bg-red-600/10 border-red-600/20"/>
                    </div>
                </c:if>
            </div>

            <div
                id="${id}_thumbnails"
                class="flex gap-2 overflow-x-auto scroll-smooth p-2 -mx-2 [scrollbar-width:none]"
                role="list"
                aria-label="Image thumbnails"
            >
                <c:forEach items="${images}" var="imageUrl" varStatus="status">
                    <c:set var="cleanImageUrl" value="${fn:trim(imageUrl)}"/>
                    <button
                        type="button"
                        class="flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border border-black/10 transition-all duration-200
                               outline-transparent outline-2 focus-visible:outline-lime-600/30 outline-offset-0 focus-visible:border-lime-600
                               active:scale-[0.98] cursor-pointer group data-[state=on]:border-lime-600 data-[state=on]:outline-lime-600/30 relative"
                        aria-label="${empty xmlSafeAlt ? 'Image' : xmlSafeAlt} ${status.count}"
                        data-state="${status.first ? 'on' : 'off'}"
                        role="listitem"
                    >
                        <img
                            src="${cleanImageUrl}"
                            alt=""
                            class="w-full h-full object-cover group-hover:scale-[1.05] transition"
                        />
                        <div class="absolute inset-0 bg-black/30 backdrop-blur-xs transition grid place-items-center text-2xl text-white opacity-0 group-hover:opacity-100">
                            <paw:icon name="zoom-in" />
                        </div>
                    </button>
                </c:forEach>
            </div>

            <script>
                document.addEventListener('DOMContentLoaded', function() {
                  const main = document.getElementById('${id}_main');
                  const mainBg = document.getElementById('${id}_main_bg');
                  const thumbnails = document.getElementById('${id}_thumbnails').querySelectorAll('button');

                  thumbnails.forEach((thumb) => {
                    thumb.addEventListener('click', () => {
                      thumbImage = thumb.querySelector('img');
                      main.src = thumbImage.src;
                      mainBg.src = thumbImage.src;
                      thumbnails.forEach((otherThumb) => otherThumb.dataset.state = (otherThumb === thumb ? "on" : "off"));
                    })
                  })
                });
            </script>
        </c:when>
        <c:otherwise>
            <div class="w-full aspect-video bg-neutral-200 rounded-xl flex items-center justify-center">
                <span class="text-neutral-500 text-center px-4"><c:out value="${noImagesLabel}"/></span>
            </div>
        </c:otherwise>
    </c:choose>
</div>
