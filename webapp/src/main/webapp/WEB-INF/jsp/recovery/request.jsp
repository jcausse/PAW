<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="recovery.request.title"/>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4"><spring:message code="recovery.request.title"/></h2>

    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/recovery/request" var="requestUrl"/>
        <form:form modelAttribute="passwordRecoveryRequestForm" action="${requestUrl}" method="post" id="recoveryForm" class="flex flex-col gap-4">
            <p class="text-sm text-neutral-600 mb-1">
                <spring:message code="recovery.request.subtitle"/>
            </p>

            <form:errors path="username" element="div" cssClass="text-xs text-red-600 font-medium" />
            <form:errors path="email" element="div" cssClass="text-xs text-red-600 font-medium" />

            <spring:message code="recovery.request.label.username" var="usernameLabel"/>
            <spring:message code="recovery.request.label.email" var="emailLabel"/>
            <spring:message code="recovery.request.placeholder.username" var="usernamePlaceholder"/>
            <spring:message code="recovery.request.placeholder.email" var="emailPlaceholder"/>

            <c:set var="isEmailMode" value="${not empty passwordRecoveryRequestForm.email}"/>

            <div class="flex flex-col gap-1">
                <label id="identifierLabel" for="identifierInput" class="text-xs text-black/70 font-medium">
                    <c:out value="${isEmailMode ? emailLabel : usernameLabel}"/>
                </label>
                <input
                    type="${isEmailMode ? 'email' : 'text'}"
                    id="identifierInput"
                    name="${isEmailMode ? 'email' : 'username'}"
                    value="<c:out value="${isEmailMode ? passwordRecoveryRequestForm.email : passwordRecoveryRequestForm.username}"/>"
                    placeholder="${isEmailMode ? emailPlaceholder : usernamePlaceholder}"
                    class="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-lime-600/30 placeholder:text-black/40 border border-black/20 focus-visible:border-lime-600 focus-visible:outline-2"
                />
            </div>

            <div class="flex items-center gap-2">
                <input
                    type="checkbox"
                    id="useEmailCheckbox"
                    <c:if test="${isEmailMode}">checked</c:if>
                    class="w-4 h-4 text-lime-600 border-black/20 outline-0 outline-offset-0 outline-lime-600/30 focus-visible:outline-2 accent-lime-600 cursor-pointer"
                >
                <label for="useEmailCheckbox" class="text-sm font-medium select-none cursor-pointer">
                    <spring:message code="recovery.request.useEmailCheckbox"/>
                </label>
            </div>

            <spring:message code="recovery.request.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form:form>
    </div>

    <a href="<c:url value="/login"/>" class="text-sm underline mt-4">
        <spring:message code="recovery.request.backToLogin"/>
    </a>

    <c:if test="${codeSent}">
        <div class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div class="bg-white rounded-2xl p-6 max-w-sm w-full border border-black/10 shadow-xl flex flex-col items-center text-center gap-4">
                <div class="w-12 h-12 rounded-full bg-lime-100 flex items-center justify-center text-lime-600">
                    <paw:icon name="mail-check" />
                </div>
                <h3 class="text-lg font-bold text-neutral-900">
                    <spring:message code="recovery.request.modal.title"/>
                </h3>
                <p class="text-sm text-neutral-600">
                    <spring:message code="recovery.request.modal.message"/>
                </p>
                <c:url value="/recovery/verification" var="continueUrl">
                    <c:if test="${not empty identifierParam}">
                        <c:param name="${identifierType}" value="${identifierParam}"/>
                    </c:if>
                </c:url>
                <spring:message code="recovery.request.modal.continue" var="continueLabel"/>
                <paw:linkButton href="${continueUrl}" text="${continueLabel}" classname="w-full"/>
            </div>
        </div>
    </c:if>

    <script>
        (function() {
            var checkbox = document.getElementById('useEmailCheckbox');
            var input = document.getElementById('identifierInput');
            var label = document.getElementById('identifierLabel');

            var usernamePlaceholder = "${usernamePlaceholder}";
            var emailPlaceholder = "${emailPlaceholder}";
            var usernameLabel = "${usernameLabel}";
            var emailLabel = "${emailLabel}";

            function updateMode() {
                if (checkbox.checked) {
                    input.name = 'email';
                    input.type = 'email';
                    input.placeholder = emailPlaceholder;
                    label.textContent = emailLabel;
                } else {
                    input.name = 'username';
                    input.type = 'text';
                    input.placeholder = usernamePlaceholder;
                    label.textContent = usernameLabel;
                }
            }

            checkbox.addEventListener('change', updateMode);
        })();
    </script>
</body>
</html>
