<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="${pageContext.response.locale.language}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="landing.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>

<body class=" pb-24 bg-neutral-50">
    <paw:navbar/>

    <main class="flex flex-col items-center gap-6 py-16 px-4">
        <h1 class="text-2xl font-bold text-sky-600"><fmt:message key="landing.hero.title"/></h1>
        <p class="text-black/70"><fmt:message key="landing.hero.description"/></p>

        <div class="flex items-center gap-12 mt-8">
            <div class="flex flex-col items-center gap-3">
                <span class="text-sm font-medium"><fmt:message key="landing.buy.title"/></span>
                <a href="<c:url value="/home"/>">
                    <paw:button text="<fmt:message key='landing.buy.button'/>" size="lg"/>
                </a>
            </div>

            <div class="w-px h-16 bg-black/15"></div>

            <div class="flex flex-col items-center gap-3">
                <span class="text-sm font-medium"><fmt:message key="landing.sell.title"/></span>
                <paw:button text="<fmt:message key='landing.sell.button'/>" size="lg"/>
            </div>
        </div>
    </main>
</body>
</html>
