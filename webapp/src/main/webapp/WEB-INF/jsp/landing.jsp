<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="${pageContext.response.locale.language}">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="landing.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>

<body class=" pb-24 bg-neutral-50">
    <paw:navbar/>

    <main class="flex flex-col items-center gap-6 py-16 px-4">
        <h1 class="text-2xl font-bold text-sky-600"><spring:message code="landing.hero.title"/></h1>
        <p class="text-black/70"><spring:message code="landing.hero.description"/></p>

        <div class="flex items-center gap-12 mt-8">
            <div class="flex flex-col items-center gap-3">
                <span class="text-sm font-medium"><spring:message code="landing.buy.title"/></span>
                <a href="<c:url value="/home"/>">
                    <spring:message code="landing.buy.button" var="buyButton"/>
                    <paw:button text="${buyButton}" size="lg"/>
                </a>
            </div>

            <div class="w-px h-16 bg-black/15"></div>

            <div class="flex flex-col items-center gap-3">
                <span class="text-sm font-medium"><spring:message code="landing.sell.title"/></span>
                <spring:message code="landing.sell.button" var="sellButton"/>
                <paw:button text="${sellButton}" size="lg"/>
            </div>
        </div>
    </main>
</body>
</html>
