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

<body class="pb-24 bg-neutral-50">
    <paw:navbar/>

    <main class="max-w-6xl mx-auto px-4 py-8 flex flex-col gap-12">
        <!-- Hot Items -->
        <c:if test="${not empty hotItems}">
            <c:url value="/listing?sort=most_offers" var="hotItemsSeeMoreUrl"/>
            <spring:message code="landing.hotItems.title" var="hotItemsTitle"/>
            <spring:message code="landing.hotItems.subtitle" var="hotItemsSubtitle"/>
            <spring:message code="landing.seeMore" var="seeMoreLabel"/>
            <paw:listingCarousel
                id="hotItemsCarousel"
                title="${hotItemsTitle}"
                subtitle="${hotItemsSubtitle}"
                listings="${hotItems}"
                seeMoreUrl="${hotItemsSeeMoreUrl}"
                seeMoreLabel="${seeMoreLabel}"
            />
        </c:if>

        <!-- Trending Items -->
        <c:if test="${not empty trendingItems}">
            <c:url value="/listing?sort=recent_offers" var="trendingItemsSeeMoreUrl"/>
            <spring:message code="landing.trendingItems.title" var="trendingItemsTitle"/>
            <spring:message code="landing.trendingItems.subtitle" var="trendingItemsSubtitle"/>
            <spring:message code="landing.seeMore" var="seeMoreLabel"/>
            <paw:listingCarousel
                id="trendingItemsCarousel"
                title="${trendingItemsTitle}"
                subtitle="${trendingItemsSubtitle}"
                listings="${trendingItems}"
                seeMoreUrl="${trendingItemsSeeMoreUrl}"
                seeMoreLabel="${seeMoreLabel}"
            />
        </c:if>

        <!-- New Items -->
        <c:if test="${not empty newItems}">
            <c:url value="/listing?sort=recent" var="newItemsSeeMoreUrl"/>
            <spring:message code="landing.newItems.title" var="newItemsTitle"/>
            <spring:message code="landing.newItems.subtitle" var="newItemsSubtitle"/>
            <spring:message code="landing.seeMore" var="seeMoreLabel"/>
            <paw:listingCarousel
                id="newItemsCarousel"
                title="${newItemsTitle}"
                subtitle="${newItemsSubtitle}"
                listings="${newItems}"
                seeMoreUrl="${newItemsSeeMoreUrl}"
                seeMoreLabel="${seeMoreLabel}"
            />
        </c:if>

        <!-- Categories Grid -->
        <c:if test="${not empty categories}">
            <spring:message code="landing.categories.title" var="categoriesTitle"/>
            <div>
                <h2 class="text-xl font-bold mb-4"><c:out value="${categoriesTitle}"/></h2>
                <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                    <c:forEach var="category" items="${categories}">
                        <c:url value="/listing" var="categoryUrl">
                            <c:param name="categoryId" value="${category.id}"/>
                        </c:url>
                        <spring:message code="category.${category.name}" var="categoryLabel"/>
                        <a href="${categoryUrl}"
                           class="block p-4 rounded-xl border border-black/10 bg-white hover:bg-black/5 hover:border-black/20 transition text-center">
                            <p class="font-medium"><c:out value="${categoryLabel}"/></p>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </main>
</body>
</html>
