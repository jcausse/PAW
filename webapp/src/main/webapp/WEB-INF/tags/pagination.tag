<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="page" required="true" type="ar.edu.itba.paw.model.Page" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--
  Renders Previous / Page X of Y / Next controls for a Page.
  Preserves every current query param except "page", so active filters/sorting survive navigation.
--%>
<c:if test="${page.totalPages > 1}">
    <c:set var="keptQuery" value=""/>
    <c:forEach var="entry" items="${paramValues}">
        <c:if test="${entry.key ne 'page'}">
            <c:forEach var="val" items="${entry.value}">
                <c:set var="keptQuery" value="${keptQuery}${entry.key}=${val}&"/>
            </c:forEach>
        </c:if>
    </c:forEach>

    <c:url value="${baseUrl}?${keptQuery}page=${page.page - 1}" var="prevUrl"/>
    <c:url value="${baseUrl}?${keptQuery}page=${page.page + 1}" var="nextUrl"/>

    <nav class="flex items-center justify-center gap-1 mt-8">
        <c:choose>
            <c:when test="${page.hasPrevious}">
                <a href="${prevUrl}" class="px-3 py-1.5 rounded-lg border border-black/15 text-sm hover:bg-black/5">
                    <spring:message code="pagination.previous"/>
                </a>
            </c:when>
            <c:otherwise>
                <span class="px-3 py-1.5 rounded-lg border border-black/10 text-sm text-black/30">
                    <spring:message code="pagination.previous"/>
                </span>
            </c:otherwise>
        </c:choose>

        <span class="px-3 py-1.5 text-sm text-black/60">
            <spring:message code="pagination.current" arguments="${page.page},${page.totalPages}"/>
        </span>

        <c:choose>
            <c:when test="${page.hasNext}">
                <a href="${nextUrl}" class="px-3 py-1.5 rounded-lg border border-black/15 text-sm hover:bg-black/5">
                    <spring:message code="pagination.next"/>
                </a>
            </c:when>
            <c:otherwise>
                <span class="px-3 py-1.5 rounded-lg border border-black/10 text-sm text-black/30">
                    <spring:message code="pagination.next"/>
                </span>
            </c:otherwise>
        </c:choose>
    </nav>
</c:if>
