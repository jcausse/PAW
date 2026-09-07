<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="discovery.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="bg-neutral-50 min-h-screen pb-24">
<paw:navbar/>

<c:url value="/listing" var="filterAction"/>
<spring:message code="discovery.filter.query.placeholder" var="queryPlaceholder"/>

<%-- Single form wraps search bar + sidebar so query and filters submit together --%>
<form action="${filterAction}" method="get">
<main class="max-w-6xl mx-auto px-6 py-8 flex flex-col gap-6">

    <%-- Search bar: prominent, above the grid (out of the sidebar) --%>
    <div class="flex gap-2">
        <input type="text" name="query" value="<c:out value='${param.query}'/>"
               placeholder="${queryPlaceholder}"
               class="flex-1 px-3 py-2 rounded-xl border border-black/20 text-sm focus:border-sky-600 outline-none"/>
        <spring:message code="discovery.filter.search" var="searchLabel"/>
        <paw:button text="${searchLabel}" type="submit"/>
    </div>

    <div class="flex flex-col md:flex-row gap-8">

        <%-- Sidebar: refinement filters (no search bar here) --%>
        <aside class="w-full md:w-64 shrink-0">
            <div class="sticky top-24 flex flex-col gap-4 bg-white border border-black/10 rounded-2xl p-5">

                <h2 class="text-sm font-semibold text-black/70 uppercase tracking-wide">
                    <spring:message code="discovery.filters"/>
                </h2>

                <div class="flex flex-col gap-1">
                    <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.category"/></label>
                    <select name="categoryId" class="px-2 py-1.5 rounded-lg border border-black/20 text-sm bg-white">
                        <option value=""><spring:message code="discovery.filter.all"/></option>
                        <c:forEach var="cat" items="${categories}">
                            <spring:message code="category.${cat.name}" var="catLabel"/>
                            <option value="${cat.id}" ${param.categoryId eq cat.id ? 'selected' : ''}>
                                <c:out value="${catLabel}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Subcategory: only shown once a category is selected (populated server-side) --%>
                <c:if test="${not empty subcategories}">
                    <div class="flex flex-col gap-1">
                        <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.subcategory"/></label>
                        <select name="subcategoryId" class="px-2 py-1.5 rounded-lg border border-black/20 text-sm bg-white">
                            <option value=""><spring:message code="discovery.filter.all"/></option>
                            <c:forEach var="sub" items="${subcategories}">
                                <spring:message code="subcategory.${sub.name}" var="subLabel"/>
                                <option value="${sub.id}" ${param.subcategoryId eq sub.id ? 'selected' : ''}>
                                    <c:out value="${subLabel}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>

                <div class="flex flex-col gap-1">
                    <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.condition"/></label>
                    <select name="condition" class="px-2 py-1.5 rounded-lg border border-black/20 text-sm bg-white">
                        <option value=""><spring:message code="discovery.filter.all"/></option>
                        <c:forEach var="cond" items="${conditions}">
                            <spring:message code="condition.${cond}" var="condLabel"/>
                            <option value="${cond}" ${param.condition eq cond ? 'selected' : ''}>
                                <c:out value="${condLabel}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="flex flex-row gap-2">
                    <div class="flex flex-col gap-1 flex-1">
                        <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.minPrice"/></label>
                        <input type="number" step="0.01" name="minPrice" value="<c:out value='${param.minPrice}'/>"
                               class="w-full px-2 py-1.5 rounded-lg border border-black/20 text-sm focus:border-sky-600 outline-none"/>
                    </div>
                    <div class="flex flex-col gap-1 flex-1">
                        <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.maxPrice"/></label>
                        <input type="number" step="0.01" name="maxPrice" value="<c:out value='${param.maxPrice}'/>"
                               class="w-full px-2 py-1.5 rounded-lg border border-black/20 text-sm focus:border-sky-600 outline-none"/>
                    </div>
                </div>

                <label class="flex items-center gap-2 text-sm">
                    <input type="checkbox" name="acceptsTrade" value="true" ${param.acceptsTrade eq 'true' ? 'checked' : ''}/>
                    <spring:message code="discovery.filter.acceptsTrade"/>
                </label>

                <div class="flex flex-col gap-2 pt-2">
                    <spring:message code="discovery.filter.apply" var="applyLabel"/>
                    <paw:button text="${applyLabel}" type="submit" classname="w-full"/>
                    <a href="${filterAction}" class="text-xs text-center text-black/50 hover:text-black/70 underline">
                        <spring:message code="discovery.filter.clear"/>
                    </a>
                </div>
            </div>
        </aside>

        <%-- Results --%>
        <section class="flex-1">
            <c:choose>
                <c:when test="${empty listings}">
                    <div class="flex items-center justify-center h-64 text-black/50">
                        <spring:message code="discovery.empty"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        <c:forEach var="listing" items="${listings}">
                            <c:url value="/listing/${listing.id}" var="listingUrl"/>
                            <a href="${listingUrl}" class="block hover:-translate-y-0.5 transition">
                                <paw:card title="${listing.title}" subtitle="$${listing.price.amount}">
                                    <c:if test="${listing.acceptsTrade}">
                                        <span class="inline-block text-xs text-sky-600 font-medium mt-1">
                                            <spring:message code="discovery.acceptsTrade.badge"/>
                                        </span>
                                    </c:if>
                                </paw:card>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </div>
</main>
</form>
</body>
</html>
