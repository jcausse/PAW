<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<spring:message code="account.welcome" arguments="${user.displayName}" var="welcomeMsg"/>
<spring:message code="account.subtitle" var="subtitleMsg"/>

<c:url value="/account/listings" var="listingsUrl"/>
<c:url value="/account/incoming-offers" var="offersUrl"/>

<spring:message code="account.myListings" var="myListingsTitle"/>
<spring:message code="account.incomingOffers" var="incomingOffersTitle"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.title"/>

<account:layout title="${welcomeMsg}" subtitle="${subtitleMsg}">
    <paw:card>
        <div class="flex flex-col">
            <paw:linkButton variant="ghost" size="lg" href="${listingsUrl}">
                <div class="grow flex flex-col gap-2">
                    <h3 class="text-black font-semibold"><c:out value="${myListingsTitle}"/></h3>
                    <spring:message code="account.myListings.desc" var="listingsDesc"/>
                    <p class="text-black/60 font-normal text-sm"><c:out value="${listingsDesc}"/></p>
                </div>
                <paw:icon name="chevron-right" classname="text-xl" />
            </paw:linkButton>

            <paw:linkButton variant="ghost" size="lg" href="${offersUrl}">
                <div class="grow flex flex-col gap-2">
                    <h3 class="text-black font-semibold flex items-center gap-2">
                        <c:out value="${incomingOffersTitle}"/>
                        <c:if test="${pendingOffersCount > 0}">
                            <paw:badge text="${pendingOffersCount}" color="red" size="sm" />
                        </c:if>
                    </h3>
                    <spring:message code="account.incomingOffers.desc" var="offersDesc"/>
                    <p class="text-black/60 font-normal text-sm"><c:out value="${offersDesc}"/></p>
                </div>
                <paw:icon name="chevron-right" classname="text-xl" />
            </paw:linkButton>
        </div>
    </paw:card>
</account:layout>
</html>
