<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<paw:head titleKey="discovery.title"/>
<body class="bg-neutral-50 min-h-screen pb-24">
<paw:navbar/>

<c:url value="/listing" var="filterAction"/>
<spring:message code="discovery.filter.query.placeholder" var="queryPlaceholder"/>
<spring:message code="card.noImage" var="noImageLabel"/>
<spring:message code="discovery.filters" var="filtersTitle"/>

<form action="${filterAction}" method="get">
<main class="max-w-6xl mx-auto px-6 py-8 flex flex-col gap-6">

    <div class="flex gap-2">
        <input type="text" name="query" value="<c:out value='${param.query}'/>"
               placeholder="${queryPlaceholder}"
               class="flex-1 px-3 py-2 rounded-xl border border-black/15 text-sm bg-white focus:border-lime-600 outline-none"/>
        <spring:message code="discovery.filter.search" var="searchLabel"/>
        <paw:button text="${searchLabel}" type="submit"/>
    </div>

    <div class="flex flex-col md:flex-row gap-8">

        <aside class="w-full md:w-56 shrink-0">
            <div class="sticky top-24">
                <paw:card title="${filtersTitle}">
                    <div class="flex flex-col gap-4">

                        <div class="flex flex-col gap-1">
                            <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.category"/></label>
                            <select name="categoryId" class="px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white">
                                <option value=""><spring:message code="discovery.filter.all"/></option>
                                <c:forEach var="cat" items="${categories}">
                                    <spring:message code="category.${cat.name}" var="catLabel"/>
                                    <option value="${cat.id}" ${param.categoryId eq cat.id ? 'selected' : ''}>
                                        <c:out value="${catLabel}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <c:if test="${not empty subcategories}">
                            <div class="flex flex-col gap-1">
                                <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.subcategory"/></label>
                                <select name="subcategoryId" class="px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white">
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
                            <select name="condition" class="px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white">
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
                                       class="w-full px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white focus:border-lime-600 outline-none"/>
                            </div>
                            <div class="flex flex-col gap-1 flex-1">
                                <label class="text-xs text-black/60 font-medium"><spring:message code="discovery.filter.maxPrice"/></label>
                                <input type="number" step="0.01" name="maxPrice" value="<c:out value='${param.maxPrice}'/>"
                                       class="w-full px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white focus:border-lime-600 outline-none"/>
                            </div>
                        </div>

                        <label class="flex items-center gap-2 text-sm">
                            <input type="checkbox" name="acceptsTrade" value="true" ${param.acceptsTrade eq 'true' ? 'checked' : ''}/>
                            <spring:message code="discovery.filter.acceptsTrade"/>
                        </label>

                        <div class="flex flex-col gap-2 pt-1">
                            <spring:message code="discovery.filter.apply" var="applyLabel"/>
                            <paw:button text="${applyLabel}" type="submit" classname="w-full"/>
                            <a href="${filterAction}" class="text-xs text-center text-black/50 hover:text-black/70 underline">
                                <spring:message code="discovery.filter.clear"/>
                            </a>
                        </div>
                    </div>
                </paw:card>
            </div>
        </aside>

        <section class="flex-1">

            <div class="flex items-center justify-between gap-4 mb-4">
                <span class="text-sm text-black/60">
                    <spring:message code="discovery.results.count" arguments="${fn:length(listings)}"/>
                </span>
                <div class="flex items-center gap-2">
                    <label class="text-xs text-black/60 font-medium whitespace-nowrap"><spring:message code="discovery.sort.label"/></label>
                    <select name="sort" onchange="this.form.submit()"
                            class="px-2 py-1.5 rounded-lg border border-black/15 text-sm bg-white">
                        <c:forEach var="opt" items="${sortOptions}">
                            <spring:message code="discovery.sort.${opt.key}" var="sortLabel"/>
                            <option value="${opt.key}" ${param.sort eq opt.key ? 'selected' : ''}>
                                <c:out value="${sortLabel}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>

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
                            <c:set var="coverUrl" value=""/>
                            <c:if test="${not empty listing.imageIds}">
                                <c:url value="/image/${listing.imageIds[0]}" var="coverUrl"/>
                            </c:if>
                            <c:set var="subLabel" value=""/>
                            <c:if test="${not empty listing.product and not empty listing.product.subcategory}">
                                <spring:message code="subcategory.${listing.product.subcategory.name}" var="subLabel"/>
                            </c:if>
                            <a href="${listingUrl}" class="block hover:-translate-y-0.5 transition">
                                <paw:card title="${listing.title}" subtitle="${subLabel}"
                                          showImage="true" imageUrl="${coverUrl}"
                                          imageAlt="${listing.title}" noImageLabel="${noImageLabel}">
                                    <div class="flex items-center gap-2 flex-wrap">
                                        <p class="text-xl font-bold">$<c:out value="${listing.price.amount}"/></p>
                                        <c:if test="${listing.acceptsTrade}">
                                            <span class="text-xs text-black/50 bg-black/5 rounded-full px-2 py-0.5">
                                                <spring:message code="discovery.acceptsTrade.badge"/>
                                            </span>
                                        </c:if>
                                    </div>
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
