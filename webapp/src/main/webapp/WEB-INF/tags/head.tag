<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="titleKey" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <c:choose>
        <c:when test="${not empty titleKey}">
            <title><spring:message code="${titleKey}"/></title>
        </c:when>
        <c:when test="${not empty title}">
            <title><c:out value="${title}"/></title>
        </c:when>
    </c:choose>

    <link rel="icon" type="image/svg+xml" href="<c:url value="/static-image/favicon.svg"/>"/>
    <link rel="alternate icon" type="image/png" href="<c:url value="/static-image/favicon.png"/>"/>
    <link rel="shortcut icon" href="<c:url value="/favicon.ico"/>"/>

    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>

    <jsp:doBody/>
</head>
