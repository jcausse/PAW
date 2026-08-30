<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
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

              <%-- TODO move this to a custom tag --%>
              <hr class="border-t-0 border-b border-black/10">

              <%-- TODO move this to a custom tag --%>
              <div class="flex flex-row gap-2 items-center text-sm">
                <div class="rounded-full bg-sky-200 text-sky-400 border border-black/10 w-10 h-10 grid place-items-center">
                  <span class="text-lg font-bold">JD</span>
                </div>
                <p>
                  ${listing.creator.firstName} ${listing.creator.lastName}
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
