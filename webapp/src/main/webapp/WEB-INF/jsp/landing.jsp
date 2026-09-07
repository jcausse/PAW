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
            <paw:linkButton href="<c:url value='/home'/>" size="lg">
                <spring:message code="landing.buy.title" var="buyTitle"/>
                <spring:message code="landing.buy.button" var="buyButton"/>
                <div class="flex flex-col items-center gap-1">
                    <span class="text-base font-medium"><c:out value="${buyTitle}"/></span>
                    <span class="text-sm font-semibold"><c:out value="${buyButton}"/></span>
                </div>
            </paw:linkButton>

            <div class="w-px h-16 bg-black/15"></div>

            <paw:linkButton href="<c:url value='/listing/new/choose-product'/>" size="lg" variant="outline">
                <spring:message code="landing.sell.title" var="sellTitle"/>
                <spring:message code="landing.sell.button" var="sellButton"/>
                <div class="flex flex-col items-center gap-1">
                    <span class="text-base font-medium"><c:out value="${sellTitle}"/></span>
                    <span class="text-sm font-semibold"><c:out value="${sellButton}"/></span>
                </div>
            </paw:linkButton>
        </div>
    </main>
</body>
</html>
