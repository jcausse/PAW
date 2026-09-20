<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" />


<c:url value="/offer/${offer.id}/proof-of-payment" var="uploadAction"/>
<c:url value="/account/my-offers" var="backUrl"/>
<spring:message code="offer.proofOfPayment.title" var="titleMsg"/>
<spring:message code="offer.proofOfPayment.subtitle" var="subtitleMsg"/>
<spring:message code="offer.proofOfPayment.uploadLabel" var="uploadLabelMsg"/>
<spring:message code="offer.proofOfPayment.back" var="backLabelMsg"/>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="offer.proofOfPayment.title"/>

<account:layout title="${titleMsg}" subtitle="${subtitleMsg}">
    <div class="max-w-2xl mx-auto">
        <paw:card classname="p-6">
            <div class="mb-6">
                <h2 class="text-lg font-semibold"><c:out value="${offer.listing.title}"/></h2>
                <p class="text-black/60 mt-1">
                    <spring:message code="offer.proofOfPayment.amount" arguments="${offer.amount}" var="amountMsg"/>
                    <c:out value="${amountMsg}"/>
                </p>
            </div>

            <paw:divider />

            <form:form method="POST" action="${uploadAction}" enctype="multipart/form-data" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-black/70 mb-2">
                        <spring:message code="offer.proofOfPayment.fileLabel" var="fileLabelMsg"/>
                        <c:out value="${fileLabelMsg}"/>
                    </label>
                    <paw:fileUpload path="file" required="true" accept="image/*,application/pdf" />
                    <p class="text-xs text-black/50 mt-1">
                        <spring:message code="offer.proofOfPayment.fileHint" var="fileHintMsg"/>
                        <c:out value="${fileHintMsg}"/>
                    </p>
                </div>

                <div class="flex gap-2 pt-4">
                    <button type="submit" class="px-4 py-2 bg-lime-600 text-white font-medium rounded-lg hover:bg-lime-700 transition">
                        <spring:message code="offer.proofOfPayment.submit" var="submitLabelMsg"/>
                        <c:out value="${submitLabelMsg}"/>
                    </button>
                    <a href="${backUrl}" class="px-4 py-2 bg-stone-200 text-black/70 font-medium rounded-lg hover:bg-stone-300 transition flex items-center justify-center">
                        <c:out value="${backLabelMsg}"/>
                    </a>
                </div>
            </form:form>
        </paw:card>
    </div>
</account:layout>
</html>