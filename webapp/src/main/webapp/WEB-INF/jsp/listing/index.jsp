<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="listing.detail.title"/></title>
    <%-- FOR DEVELOPMENT ONLY!! --%>
    <%-- <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script> --%>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-5xl mx-auto">
        <div class="flex flex-row gap-4">
            <div class="flex-2 min-w-0">
                <paw:card>
                    <c:choose>
                        <c:when test="${not empty listing.imageIds}">
                            <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                                <c:if test="${status.first}">
                                    <img
                                        src="<c:url value='/image/${imageId}'/>"
                                        alt="<c:out value='${listing.title}'/> - Image ${status.count}"
                                        class="w-full h-auto object-cover rounded-xl"
                                    >
                                </c:if>
                            </c:forEach>
                            <c:if test="${listing.imageIds.size() > 1}">
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                                    <c:forEach items="${listing.imageIds}" var="imageId" varStatus="status">
                                        <c:if test="${not status.first}">
                                            <img
                                                src="<c:url value='/image/${imageId}'/>"
                                                alt="<c:out value='${listing.title}'/> - Image ${status.count}"
                                                class="w-full h-auto object-cover rounded-xl"
                                            >
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <div class="w-full aspect-video bg-neutral-200 rounded-xl flex items-center justify-center">
                                <span class="text-neutral-500 text-center px-4"><spring:message code="listing.detail.noImages"/></span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </paw:card>
            </div>
            <div class="flex-1 min-w-md">
                <paw:card>
                    <div class="flex flex-col gap-4">
                        <h1 class="text-2xl font-semibold">${listing.title}</h1>

                        <div class="flex flex-col">
                            <div class="text-black">${listing.product.brand} ${listing.product.model} (${listing.product.year})</div>
                            <div class="text-sm text-black/60">
                                <spring:message code="category.${listing.product.subcategory.category.name}"/>
                                /
                                <spring:message code="subcategory.${listing.product.subcategory.name}"/>
                            </div>
                        </div>

                        <%-- TODO move this to a custom tag --%>
                        <hr class="border-t-0 border-b border-black/10">

                        <div class="flex flex-row gap-2 items-center text-sm">
                            <div class="rounded-full bg-sky-200 text-sky-400 border border-black/10 w-10 h-10 grid place-items-center overflow-hidden">
                                <c:choose>
                                    <c:when test="${listing.creator.imageId.present}">
                                        <img
                                            src="<c:url value='/image/${listing.creator.imageId.get()}'/>"
                                            alt="<c:out value='${listing.creator.displayName}'/>"s Profile Picture"
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
