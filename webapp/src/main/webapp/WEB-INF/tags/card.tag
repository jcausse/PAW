<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="subtitle" required="false" %>
<%@ attribute name="imageUrl" required="false" %>
<%@ attribute name="imageAlt" required="false" %>
<%@ attribute name="noImageLabel" required="false" %>
<%@ attribute name="showImage" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="rounded-2xl border border-black/10 bg-border bg-gradient-to-b from-neutral-50 to-white to-40% p-4 flex flex-col gap-1">
  <c:if test="${showImage}">
    <div class="w-full aspect-square rounded-lg overflow-hidden border border-black/10 mb-3 bg-neutral-200">
      <c:choose>
        <c:when test="${not empty imageUrl}">
          <img src="${imageUrl}" alt="<c:out value='${imageAlt}'/>" class="w-full h-full object-cover"/>
        </c:when>
        <c:otherwise>
          <div class="w-full h-full grid place-items-center text-black/30 text-xs px-2 text-center">
            <c:out value="${noImageLabel}"/>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </c:if>
  <c:if test="${not empty title}">
    <h3 class="text-base font-semibold"><c:out value="${title}"/></h3>
  </c:if>
  <c:if test="${not empty subtitle}">
    <p class="text-sm text-black/60 mb-2"><c:out value="${subtitle}"/></p>
  </c:if>
  <jsp:doBody/>
</div>
