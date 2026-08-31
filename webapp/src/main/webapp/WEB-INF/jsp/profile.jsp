<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="profile.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="min-h-screen bg-neutral-50">
    <paw:navbar/>
    <main class="max-w-2xl mx-auto p-6">
        <h1 class="text-4xl font-bold mb-4"><spring:message code="profile.greeting" arguments="${user.displayName}"/></h1>
        <p><spring:message code="profile.username"/>: <c:out value="${user.username}"/></p>
        <p><spring:message code="profile.email"/>: <c:out value="${user.email}"/></p>
    </main>
</body>
</html>
