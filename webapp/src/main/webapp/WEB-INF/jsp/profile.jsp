<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="profile.listings.title" var="listingsTitle"/>
<spring:message code="profile.listings.empty" var="listingsEmpty"/>
<spring:message code="profile.listings.view" var="viewListingLabel"/>

<html lang="${pageContext.response.locale.language}">
<paw:head title="${user.displayName}"/>
<body class="min-h-screen bg-neutral-50 flex flex-col">
    <paw:navbar/>
    <main class="max-w-6xl w-full mx-auto px-6 pt-8 pb-16">
        <paw:card classname="w-full flex flex-col gap-8">
            <div class="flex items-center gap-6">
                <paw:userAvatar user="${user}" size="xl" />
                <div class="flex flex-col">
                    <h1 class="text-3xl font-bold tracking-tight text-neutral-900"><c:out value="${user.displayName}"/></h1>
                    <span class="text-lg text-neutral-500 font-medium">@<c:out value="${user.username}"/></span>
                    <c:if test="${not empty user.joinedAt}">
                        <span class="text-sm text-neutral-500 mt-1">
                            <spring:message code="profile.memberSince" arguments="${user.joinedAt.toEpochMilli()}"/>
                        </span>
                    </c:if>
                    <c:if test="${allowEdit}">
                        <div class="mt-6">
                            <c:url value="/profile/edit" var="editUrl"/>
                            <spring:message code="profile.edit" var="editLabel"/>
                            <paw:linkButton href="${editUrl}" text="${editLabel}" size="sm" variant="outline"/>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="flex flex-col gap-4">
                <div class="flex items-center justify-between bg-neutral-50 p-4 rounded-xl border border-black/5">
                    <div class="flex flex-col">
                        <span class="text-xs font-semibold text-neutral-500 uppercase tracking-wider mb-1"><spring:message code="profile.email"/></span>
                        <span class="text-neutral-800 font-medium"><c:out value="${user.email}"/></span>
                    </div>
                    <spring:message code="profile.sendEmail" var="sendEmailLabel"/>
                    <paw:linkButton href="mailto:${user.email}" text="${sendEmailLabel}" size="sm" variant="outline"/>
                </div>
            </div>
        </paw:card>

        <paw:card>
            <div class="flex items-center justify-between mb-6">
                <h2 class="text-xl font-semibold text-neutral-900"><c:out value="${listingsTitle}"/></h2>
            </div>

            <c:choose>
                <c:when test="${empty listings}">
                    <div class="mt-12 mb-12 flex flex-col items-center">
                        <p class="text-black/60 text-lg mb-4"><c:out value="${listingsEmpty}"/></p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="flex flex-col gap-4">
                        <c:forEach var="listing" items="${listings}" varStatus="loop">
                            <c:url value="/listing/${listing.id}" var="listingUrl"/>
                            <div class="flex flex-col gap-2">
                                <div class="flex flex-col sm:flex-row sm:items-center gap-4">
                                    <div class="min-w-0">
                                        <h3 class="text-base font-semibold truncate">
                                            <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${listing.title}"/></a>
                                        </h3>
                                    </div>
                                </div>

                                <div class="flex flex-row gap-3 w-full sm:w-auto">
                                    <paw:listingImage listing="${listing}" />

                                    <div class="flex-1 min-w-0 flex flex-col justify-between">
                                        <paw:product product="${listing.product}" size="sm" />
                                        <div class="text-xl font-bold mt-2 sm:mt-0">
                                            $<c:out value="${listing.price.amount}"/>
                                        </div>
                                    </div>

                                    <div class="flex flex-row gap-1 self-end">
                                        <paw:linkButton variant="outline" href="${listingUrl}" text="${viewListingLabel}" size="sm" />
                                    </div>
                                </div>
                            </div>

                            <c:if test="${!loop.last}">
                                <paw:divider />
                            </c:if>
                        </c:forEach>
                    </div>

                    <paw:pagination page="${listingPage}" baseUrl="/profile/${user.id}"/>
                </c:otherwise>
            </c:choose>
        </paw:card>
    </main>
</body>
</html>
