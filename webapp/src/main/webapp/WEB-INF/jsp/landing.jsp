<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="landing.title">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</paw:head>

<body class=" pb-24 bg-neutral-50">
    <paw:navbar/>

    <main class="flex flex-col items-center gap-6 py-4 px-4">
        <div style="position:relative; width:100%; max-width:72rem; container-type:inline-size;">
            <img
                src="<c:url value="/static-image/banner.png"/>"
                alt="Banner"
                style="width:100%; border-radius:0.5rem; display:block;"
            />

            <div style="position:absolute; inset:0; pointer-events:none; font-family:'Montserrat',sans-serif;">

                 <%-- Title --%>
                <div style="position:absolute; left:5.8%; top:35%; width:38%;">
                    <p style="margin:0; font-weight:800; line-height:1.15; color:#ffffff; font-size:2.7cqw;">
                        <spring:message code="landing.banner.title.line1"/>
                    </p>

                    <p style="margin:0; font-weight:800; line-height:1.15; color:#A6E61A; font-size:2.7cqw;">
                        <spring:message code="landing.banner.title.line2"/>
                    </p>

                    <p style="margin:0; font-weight:800; line-height:1.15; color:#A6E61A; font-size:2.7cqw;">
                        <spring:message code="landing.banner.title.line3"/>
                    </p>
                </div>

                <%-- Icons --%>
                <div style="position:absolute; left:5.7%; top:72%; display:flex; align-items:center; gap:1.8cqw; font-size:1cqw;">

                    <%-- Easy trade --%>
                    <div style="display:flex; align-items:center; gap:0.6em;">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                            fill="none" stroke="#A6E61A" stroke-width="2"
                            stroke-linecap="round" stroke-linejoin="round"
                            style="width:2.3em; height:2.3em; flex-shrink:0;">
                            <path d="M17 1l4 4-4 4"/>
                            <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                            <path d="M7 23l-4-4 4-4"/>
                            <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                        </svg>
                        <span style="font-weight:700; color:#ffffff; line-height:1.2; font-size:0.95em;">
                            <spring:message code="landing.banner.feature1.line1"/><br/>
                            <spring:message code="landing.banner.feature1.line2"/>
                        </span>
                    </div>

                    <div style="width:1px; align-self:stretch; background:rgba(255,255,255,0.2);"></div>

                    <%-- In-person pickup --%>
                    <div style="display:flex; align-items:center; gap:0.6em;">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                            fill="none" stroke="#A6E61A" stroke-width="2"
                            stroke-linecap="round" stroke-linejoin="round"
                            style="width:2.3em; height:2.3em; flex-shrink:0;">
                            <path d="M12 22s8-4 8-10V4l-8-2-8 2v8c0 6 8 10 8 10z"/>
                            <path d="M9 12l2 2 4-4"/>
                        </svg>
                        <span style="font-weight:700; color:#ffffff; line-height:1.2; font-size:0.95em;">
                            <spring:message code="landing.banner.feature2.line1"/><br/>
                            <spring:message code="landing.banner.feature2.line2"/>
                        </span>
                    </div>

                </div>
            </div>
        </div>

        <div class="flex items-center gap-12 mt-8">

            <c:url value='/listing' var="buyUrl" />
            <c:url value='/listing/new/choose-product' var="sellUrl" />

            <paw:linkButton href="${buyUrl}" size="lg" variant="outline">
                <spring:message code="landing.buy.title" var="buyTitle"/>
                <spring:message code="landing.buy.button" var="buyButton"/>
                <div class="flex flex-col items-center gap-1">
                    <span class="text-xl font-semibold"><c:out value="${buyButton}"/></span>
                    <span class="text-base font-normal text-black/60"><c:out value="${buyTitle}"/></span>
                </div>
            </paw:linkButton>

            <div class="w-px h-16 bg-black/15"></div>

            <paw:linkButton href="${sellUrl}" size="lg" variant="outline">
                <spring:message code="landing.sell.title" var="sellTitle"/>
                <spring:message code="landing.sell.button" var="sellButton"/>
                <div class="flex flex-col items-center gap-1">
                    <span class="text-xl font-semibold"><c:out value="${sellButton}"/></span>
                    <span class="text-base font-normal text-black/60"><c:out value="${sellTitle}"/></span>
                </div>
            </paw:linkButton>
        </div>
    </main>
</body>
</html>
