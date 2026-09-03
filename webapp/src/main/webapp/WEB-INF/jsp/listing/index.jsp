<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="listing.detail.title"/></title>
    <%-- FOR DEVELOPMENT ONLY!! --%>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-5xl mx-auto">
      <div class="flex flex-row gap-4">
        <div class="flex-2 bg-white"></div>
        <div class="flex-1 min-w-md">
          <paw:card>
            <div class="flex flex-col gap-4">
              <h1 class="text-2xl font-semibold">${listing.title}</h1>

              <c:if test="${listing.product != null}">
                  <div class="text-black">${listing.product.name} (${listing.product.year})</div>
                  <div class="text-sm text-black/60">${listing.product.brand} ${listing.product.model}</div>
              </c:if>

              <%-- TODO move this to a custom tag --%>
              <hr class="border-t-0 border-b border-black/10">

              <%-- TODO move this to a custom tag --%>
              <div class="flex flex-row gap-2 items-center text-sm">
                <div class="rounded-full bg-sky-200 text-sky-400 border border-black/10 w-10 h-10 grid place-items-center overflow-hidden">
                  <c:choose>
                      <c:when test="${listing.creator.imageId.present}">
                          <img
                              src="<c:url value='/image/${listing.creator.imageId.get()}'/>"
                              alt="<c:out value='${listing.creator.displayName}'/>&quot;s Profile Picture"
                              class="w-full h-full object-cover shadow-sm"
                          >
                      </c:when>
                      <c:otherwise>
                          <img
                              src="<c:url value='/static-image/defaultProfilePicture.svg'/>"
                              alt="Default Profile Picture"
                              class="w-full h-full object-cover shadow-sm"
                          >
                      </c:otherwise>
                  </c:choose>
                </div>
                <p>
                  ${listing.creator.displayName}
                  <span class="text-black/60">(${listing.creator.username})</span>
                </p>
              </div>

              <p class="text-3xl font-bold">$${listing.price.getAmount()}</p>

              <spring:message code="listing.detail.makeOffer" var="makeOfferLabel"/>
              <paw:button size="lg" classname="w-full" text="${makeOfferLabel}" />
            </div>
          </paw:card>
        </div>
      </div>
    </div>
</body>
</html>
