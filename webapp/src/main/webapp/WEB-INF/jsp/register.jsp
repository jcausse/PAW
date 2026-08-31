<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="register.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4"><spring:message code="register.title"/></h2>
    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/register" var="registerUrl"/>
        <form:form modelAttribute="userForm" action="${registerUrl}" method="post" cssClass="flex flex-col gap-4">
            <form:errors path="" element="div" cssClass="text-xs text-red-600"/>

            <spring:message code="field.username" var="usernameLabel"/>
            <paw:formInput path="username" label="${usernameLabel}" variant="outline"/>

            <spring:message code="register.displayName" var="displayNameLabel"/>
            <paw:formInput path="displayName" label="${displayNameLabel}" variant="outline"/>

            <spring:message code="register.email" var="emailLabel"/>
            <paw:formInput path="email" type="email" label="${emailLabel}" variant="outline"/>

            <spring:message code="register.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" type="submit"/>
        </form:form>
    </div>
    <a href="<c:url value="/login"/>" class="text-sm underline mt-4"><spring:message code="register.haveAccount"/></a>
</body>
</html>
