<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="profile.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="min-h-screen bg-neutral-50">
    <paw:navbar/>
    <main class="max-w-2xl mx-auto p-6">
        <div class="flex items-center space-x-4 mb-4">
            <c:choose>
                <c:when test="${user.imageId.present}">
                    <img
                        src="<c:url value='/image/${user.imageId.get()}'/>"
                        alt="<c:out value='${user.displayName}'/> Profile Picture"
                        class="w-24 h-24 rounded-full object-cover shadow-sm"
                    >
                </c:when>
                <c:otherwise>
                    <img
                        src="<c:url value='/static-image/defaultProfilePicture.svg'/>"
                        alt="Default Profile Picture"
                        class="w-24 h-24 rounded-full object-cover shadow-sm"
                    >
                </c:otherwise>
            </c:choose>
            <h1 class="text-4xl font-bold"><spring:message code="profile.greeting" arguments="${user.displayName}"/></h1>
        </div>
        <p><spring:message code="profile.username"/>: <c:out value="${user.username}"/></p>
        <p><spring:message code="profile.email"/>: <c:out value="${user.email}"/></p>
    </main>
</body>
</html>
