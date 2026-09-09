<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="register.title"/>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-2"><spring:message code="register.title"/></h2>
    <p class="text-sm text-neutral-500 mb-6 text-center max-w-sm"><spring:message code="register.subtitle"/></p>
    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/register" var="registerUrl"/>
        <form:form modelAttribute="userForm" action="${registerUrl}" method="post" enctype="multipart/form-data" cssClass="flex flex-col gap-4">
            <form:errors path="" element="div" cssClass="text-xs text-red-600"/>

            <spring:message code="field.username" var="usernameLabel"/>
            <paw:formInput path="username" label="${usernameLabel}" variant="outline"/>

            <spring:message code="register.firstTimeCheck" var="firstTimeLabel"/>
            <paw:formCheckbox path="firstTime" label="${firstTimeLabel}"/>

            <div id="newUserDataFields" class="flex flex-col gap-4 ${userForm.firstTime ? '' : 'hidden'}">
                <spring:message code="register.displayName" var="displayNameLabel"/>
                <paw:formInput path="displayName" label="${displayNameLabel}" variant="outline"/>

                <spring:message code="register.email" var="emailLabel"/>
                <paw:formInput path="email" type="email" label="${emailLabel}" variant="outline"/>

                <spring:message code="register.profilePicture" var="profilePictureLabel"/>
                <paw:formInput path="profilePicture" type="file" label="${profilePictureLabel}" variant="outline"/>
            </div>

            <spring:message code="register.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form:form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const checkbox = document.getElementById('firstTime');
            const container = document.getElementById('newUserDataFields');
            if (checkbox && container) {
                function updateVisibility() {
                    if (checkbox.checked) {
                        container.classList.remove('hidden');
                    } else {
                        container.classList.add('hidden');
                    }
                }
                checkbox.addEventListener('change', updateVisibility);
                if (container.querySelector('.errors')) {
                    checkbox.checked = true;
                    updateVisibility();
                }
            }
        });
    </script>
</body>
</html>
