<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>


<c:url value="/account/incoming-offers" var="filterAction"/>
<spring:message code="account.incomingOffers.title" var="titleMsg"/>
<spring:message code="account.incomingOffers.subtitle" var="subtitleMsg"/>
<spring:message code="account.incomingOffers.pendingTitle" var="pendingTitleMsg"/>
<spring:message code="account.incomingOffers.pendingPaymentTitle" var="pendingPaymentTitleMsg"/>
<spring:message code="account.incomingOffers.resolvedTitle" var="resolvedTitleMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="account.incomingOffers.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <form:form modelAttribute="filterForm" action="${filterAction}" method="get" id="filterForm">
        <paw:formButtonToggle path="statusGroup" items="${statusGroupOptions}" selectedOption="${filterForm.getStatusGroup()}" classname="mb-4" />
    </form:form>

    <c:choose>
        <c:when test="${empty offers}">
            <div class="text-center py-12">
                <spring:message code="account.incomingOffers.empty" var="emptyMsg"/>
                <p class="text-black/50 text-lg"><c:out value="${emptyMsg}"/></p>
            </div>
        </c:when>
        <c:when test="${filterForm.getStatusGroup() == 'pending' || filterForm.getStatusGroup() == 'pending_payment'}">
            <div class="flex flex-col gap-4 mb-8">
                <c:forEach var="offer" items="${offers}">
                    <account:offerCard offer="${offer}" user="buyer">
                        <c:url value="/offer/${offer.id}" var="offerUrl"/>
                        <paw:linkButton variant="outline" href="${offerUrl}" icon="eye" />
                        <c:if test="${offer.status.name() == 'PENDING_PAYMENT'}">
                            <form action="<c:url value='/offer/${offer.id}/confirm-payment'/>" method="POST">
                                <paw:button type="submit" variant="outline" icon="check" />
                            </form>
                            <c:if test="${offer.proofOfShippingId == null && empty offer.trackingNumber}">
                                <c:url value="/offer/${offer.id}/proof-of-shipping" var="shippingUrl"/>
                                <paw:linkButton variant="outline" href="${shippingUrl}" icon="truck" />
                            </c:if>
                        </c:if>
                        <c:if test="${offer.status.name() == 'PENDING'}">
                            <form action="<c:url value='/offer/${offer.id}/accept'/>" method="POST">
                                <paw:button type="submit" variant="outline" icon="check" />
                            </form>
                        </c:if>
                        <form action="<c:url value='/offer/${offer.id}/reject'/>" method="POST">
                            <paw:button type="submit" variant="outline" role="danger" icon="x" />
                        </form>
                    </account:offerCard>
                </c:forEach>

                <paw:pagination page="${offerPage}" baseUrl="/account/incoming-offers"/>
            </div>
        </c:when>
        <c:when test="${filterForm.getStatusGroup() == 'resolved'}">
            <paw:card>
                <account:offerTable offers="${offers}" user="buyer" />
                <paw:pagination page="${offerPage}" baseUrl="/account/incoming-offers"/>
            </paw:card>
        </c:when>
    </c:choose>
</account:layout>
</html>
