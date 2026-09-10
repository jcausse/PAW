<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>

<c:url value="/account/listings" var="listingsUrl"/>
<c:url value="/account/incoming-offers" var="offersUrl"/>

<spring:message code="account.myListings" var="myListingsTitle"/>
<spring:message code="account.incomingOffers" var="incomingOffersTitle"/>

<spring:message code="account.title" var="accountTitle"/>
<paw:card title="${accountTitle}">
    <div class="flex flex-col gap-2 mt-4">
        <paw:user user="${user}" variant="detailed" href="/account" />

        <paw:divider />

        <spring:message code="account.sidebar.buyer" var="buyerSubtitle"/>
        <%-- TODO --%>

        <spring:message code="account.sidebar.seller" var="sellerSubtitle"/>
        <h4 class="font-semibold text-sm mt-2"><c:out value="${sellerSubtitle}" /></h4>
        <div class="flex flex-col gap-1">
            <account:link href="${listingsUrl}" text="${myListingsTitle}" icon="store" />
            <account:link href="${offersUrl}" text="${incomingOffersTitle}" icon="handshake" />
        </div>
    </div>
</paw:card>
