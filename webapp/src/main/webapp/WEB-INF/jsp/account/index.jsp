<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.title"/>
<body class="min-h-screen bg-neutral-50">
    <paw:navbar/>
    <main class="max-w-6xl mx-auto px-6 py-8">
        <div class="mb-8">
            <spring:message code="account.welcome" arguments="${user.displayName}" var="welcomeMsg"/>
            <h1 class="text-3xl font-bold"><c:out value="${welcomeMsg}"/></h1>
            <spring:message code="account.subtitle" var="subtitleMsg"/>
            <p class="text-black/60 mt-2"><c:out value="${subtitleMsg}"/></p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="md:col-span-1">
                <paw:card title="${user.displayName}">
                    <div class="flex items-center gap-4">
                        <div class="w-20 h-20 rounded-full object-cover shadow-sm bg-neutral-200 flex items-center justify-center">
                            <span class="text-black/30 text-xs">No Image</span>
                        </div>
                        <div>
                            <p class="text-lg font-semibold"><c:out value="${user.displayName}"/></p>
                            <p class="text-black/60">@<c:out value="${user.username}"/></p>
                            <p class="text-sm text-black/50"><c:out value="${user.email}"/></p>
                        </div>
                    </div>
                </paw:card>
            </div>

            <div class="md:col-span-2 space-y-4">
                <spring:message code="account.myListings" var="myListingsTitle"/>
                <paw:card title="${myListingsTitle}">
                    <div class="flex flex-col gap-4">
                        <c:url value="/account/listings" var="listingsUrl"/>
                        <spring:message code="account.myListings.desc" var="listingsDesc"/>
                        <p class="text-black/60"><c:out value="${listingsDesc}"/></p>
                        <spring:message code="account.myListings.link" var="listingsLink"/>
                        <paw:linkButton text="${listingsLink}" variant="primary" href="${listingsUrl}"/>
                    </div>
                </paw:card>

                <spring:message code="account.incomingOffers" var="incomingOffersTitle"/>
                <paw:card title="${incomingOffersTitle}">
                    <div class="flex flex-col gap-4">
                        <c:url value="/account/incoming-offers" var="offersUrl"/>
                        <spring:message code="account.incomingOffers.desc" var="offersDesc"/>
                        <p class="text-black/60"><c:out value="${offersDesc}"/></p>
                        <spring:message code="account.incomingOffers.link" var="offersLink"/>
                        <paw:linkButton text="${offersLink}" variant="primary" href="${offersUrl}"/>
                    </div>
                </paw:card>

                <spring:message code="account.myPurchases" var="myPurchasesTitle"/>
                <paw:card title="${myPurchasesTitle}">
                    <div class="flex flex-col gap-4">
                        <spring:message code="account.myPurchases.desc" var="purchasesDesc"/>
                        <p class="text-black/60"><c:out value="${purchasesDesc}"/></p>
                        <spring:message code="account.comingSoon" var="comingSoon"/>
                        <paw:button text="${comingSoon}" variant="outline" disabled="true"/>
                    </div>
                </paw:card>
            </div>
        </div>
    </main>
</body>
</html>