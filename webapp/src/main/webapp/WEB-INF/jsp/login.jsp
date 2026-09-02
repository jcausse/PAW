<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="login.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4"><spring:message code="login.title"/></h2>

    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/login" var="loginUrl"/>
        <form action="${loginUrl}" method="post" class="flex flex-col gap-4">
            <c:if test="${param.error != null}">
                <div class="text-xs text-red-600 font-medium">
                    <spring:message code="login.error.invalidCredentials"/>
                </div>
            </c:if>

            <spring:message code="field.username" var="usernameLabel"/>
            <paw:input id="username" name="username" label="${usernameLabel}" variant="outline"/>

            <spring:message code="field.password" var="passwordLabel"/>
            <paw:input id="password" name="password" type="password" label="${passwordLabel}" variant="outline"/>

            <div class="flex items-center gap-2">
                <input type="checkbox" id="rememberMe" name="rememberMe" class="rounded border-black/10 text-black focus:ring-black">
                <label for="rememberMe" class="text-sm font-medium select-none"><spring:message code="field.rememberMe"/></label>
            </div>

            <spring:message code="login.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form>
    </div>

    <a href="<c:url value="/register"/>" class="text-sm underline mt-4"><spring:message code="login.noAccount"/></a>
</body>
</html>
