<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="recovery.verification.title"/>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4"><spring:message code="recovery.verification.title"/></h2>

    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/recovery/verification" var="verificationUrl"/>
        <form:form modelAttribute="passwordRecoveryVerificationForm" action="${verificationUrl}" method="post" class="flex flex-col gap-4">
            <form:hidden path="usernameOrEmail"/>

            <p class="text-sm text-neutral-600 mb-1">
                <spring:message code="recovery.verification.subtitle"/>
            </p>

            <spring:message code="field.otp" var="otpLabel"/>
            <paw:formInput path="otp" label="${otpLabel}" variant="outline"/>

            <spring:message code="field.newPassword" var="passwordLabel"/>
            <paw:formInput path="password" type="password" label="${passwordLabel}" variant="outline"/>

            <spring:message code="field.repeatPassword" var="repeatPasswordLabel"/>
            <paw:formInput path="repeatPassword" type="password" label="${repeatPasswordLabel}" variant="outline"/>

            <spring:message code="recovery.verification.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form:form>
    </div>

    <a href="<c:url value="/login"/>" class="text-sm underline mt-4">
        <spring:message code="recovery.request.backToLogin"/>
    </a>
</body>
</html>
