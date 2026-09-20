<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="verify.title"/>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4"><spring:message code="verify.title"/></h2>

    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:if test="${resent}">
            <div class="mb-4 text-xs text-lime-700 bg-lime-50 border border-lime-200 rounded-lg p-3 font-medium">
                <spring:message code="verify.resend.success"/>
            </div>
        </c:if>

        <c:url value="/verify" var="verifyUrl"/>
        <form:form modelAttribute="emailVerificationForm" action="${verifyUrl}" method="post" class="flex flex-col gap-4">
            <p class="text-sm text-neutral-600 mb-1">
                <spring:message code="verify.subtitle"/>
            </p>

            <spring:message code="field.usernameOrEmail" var="usernameOrEmailLabel"/>
            <paw:formInput path="usernameOrEmail" label="${usernameOrEmailLabel}" variant="outline"/>

            <spring:message code="field.verificationCode" var="otpLabel"/>
            <paw:formInput path="otp" label="${otpLabel}" variant="outline"/>

            <spring:message code="verify.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form:form>

        <c:url value="/verify/resend" var="resendUrl"/>
        <form action="${resendUrl}" method="post" class="mt-4 text-center">
            <input type="hidden" name="usernameOrEmail" value="<c:out value="${emailVerificationForm.usernameOrEmail}"/>"/>
            <button type="submit" class="text-xs text-neutral-600 hover:text-neutral-900 underline cursor-pointer bg-transparent border-none p-0">
                <spring:message code="verify.resend"/>
            </button>
        </form>
    </div>

    <c:url value="/login" var="loginUrl"/>
    <a href="${loginUrl}" class="text-sm underline mt-4">
        <spring:message code="verify.backToLogin"/>
    </a>
</body>
</html>
