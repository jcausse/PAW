<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<spring:message code="profile.listings.title" var="listingsTitle"/>
<spring:message code="profile.listings.empty" var="listingsEmpty"/>
<spring:message code="profile.listings.view" var="viewListingLabel"/>
<spring:message code="profile.ratings.title" var="ratingsTitle"/>
<spring:message code="profile.ratings.asSeller" var="ratingsAsSeller"/>
<spring:message code="profile.ratings.asBuyer" var="ratingsAsBuyer"/>
<spring:message code="profile.ratings.positive" var="ratingPositive"/>
<spring:message code="profile.ratings.neutral" var="ratingNeutral"/>
<spring:message code="profile.ratings.negative" var="ratingNegative"/>
<spring:message code="profile.ratings.total" var="ratingTotal"/>
<spring:message code="profile.ratings.filter.role" var="ratingFilterRole"/>
<spring:message code="profile.ratings.filter.type" var="ratingFilterType"/>
<spring:message code="profile.ratings.filter.all" var="ratingFilterAll"/>
<spring:message code="profile.ratings.filter.seller" var="ratingFilterSeller"/>
<spring:message code="profile.ratings.filter.buyer" var="ratingFilterBuyer"/>
<spring:message code="profile.ratings.filter.positive" var="ratingFilterPositive"/>
<spring:message code="profile.ratings.filter.neutral" var="ratingFilterNeutral"/>
<spring:message code="profile.ratings.filter.negative" var="ratingFilterNegative"/>
<spring:message code="profile.ratings.empty" var="ratingEmpty"/>

<c:url value="/profile/${user.id}" var="profileUrl"/>
<c:url value="/profile/${user.id}" var="filterAction"/>
<c:url value="/profile/${user.id}" var="listingsBaseUrl"/>

<c:set var="roleOptions" value="#{T(java.util.Arrays).asList(
    new java.util.AbstractMap.SimpleEntry('seller', ratingFilterSeller),
    new java.util.AbstractMap.SimpleEntry('buyer', ratingFilterBuyer)
)}"/>

<c:set var="typeOptions" value="#{T(java.util.Arrays).asList(
    new java.util.AbstractMap.SimpleEntry('', ratingFilterAll),
    new java.util.AbstractMap.SimpleEntry('positive', ratingFilterPositive),
    new java.util.AbstractMap.SimpleEntry('neutral', ratingFilterNeutral),
    new java.util.AbstractMap.SimpleEntry('negative', ratingFilterNegative)
)}"/>

<html lang="${pageContext.response.locale.language}">
<paw:head title="${user.displayName}"/>
<body class="min-h-screen bg-neutral-50 flex flex-col">
    <paw:navbar/>
    <main class="max-w-5xl w-full mx-auto px-6 pt-8 pb-16 flex flex-col gap-6">
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
        </paw:card>

        <paw:card title="${ratingsTitle}">
            <c:set var="sellerBalance" value="${user.sellerRatingBalance}"/>
            <c:set var="buyerBalance" value="${user.buyerRatingBalance}"/>

            <div class="flex flex-col sm:flex-row gap-8 mt-2">
                <div class="flex-1">
                    <h3 class="text-sm font-semibold text-neutral-500 uppercase tracking-wide mb-2">
                        <c:out value="${ratingsAsSeller}"/>:
                        <c:choose>
                            <c:when test="${sellerBalance > 0}">
                                <span class="text-green-600 font-bold">+<c:out value="${sellerBalance}"/></span>
                            </c:when>
                            <c:when test="${sellerBalance < 0}">
                                <span class="text-red-600 font-bold"><c:out value="${sellerBalance}"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-neutral-500 font-bold">0</span>
                            </c:otherwise>
                        </c:choose>
                    </h3>
                    <div class="flex gap-4 text-sm text-neutral-600">
                        <span><c:out value="${ratingPositive}"/>: <span class="text-green-600"><c:out value="${user.sellerPositiveRatings}"/></span></span>
                        <span><c:out value="${ratingNeutral}"/>: <c:out value="${user.sellerNeutralRatings}"/></span>
                        <span><c:out value="${ratingNegative}"/>: <span class="text-red-600"><c:out value="${user.sellerNegativeRatings}"/></span></span>
                    </div>
                </div>

                <div class="flex-1">
                    <h3 class="text-sm font-semibold text-neutral-500 uppercase tracking-wide mb-2">
                        <c:out value="${ratingsAsBuyer}"/>:
                        <c:choose>
                            <c:when test="${buyerBalance > 0}">
                                <span class="text-green-600 font-bold">+<c:out value="${buyerBalance}"/></span>
                            </c:when>
                            <c:when test="${buyerBalance < 0}">
                                <span class="text-red-600 font-bold"><c:out value="${buyerBalance}"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-neutral-500 font-bold">0</span>
                            </c:otherwise>
                        </c:choose>
                    </h3>
                    <div class="flex gap-4 text-sm text-neutral-600">
                        <span><c:out value="${ratingPositive}"/>: <span class="text-green-600"><c:out value="${user.buyerPositiveRatings}"/></span></span>
                        <span><c:out value="${ratingNeutral}"/>: <c:out value="${user.buyerNeutralRatings}"/></span>
                        <span><c:out value="${ratingNegative}"/>: <span class="text-red-600"><c:out value="${user.buyerNegativeRatings}"/></span></span>
                    </div>
                </div>
            </div>

            <!-- Ratings list with filters -->
            <paw:divider />

            <form:form modelAttribute="ratingFilterForm" action="${filterAction}" method="get" id="ratingFilterForm" class="mt-4">
                <div class="flex flex-wrap gap-4 mb-4">
                    <div class="flex flex-col gap-1">
                        <label class="text-xs font-medium text-neutral-500"><c:out value="${ratingFilterRole}"/></label>
                        <paw:formButtonToggle path="role" items="${roleOptions}" selectedOption="${ratingFilterForm.role}" />
                    </div>
                    <div class="flex flex-col gap-1">
                        <label class="text-xs font-medium text-neutral-500"><c:out value="${ratingFilterType}"/></label>
                        <paw:formButtonToggle path="type" items="${typeOptions}" selectedOption="${ratingFilterForm.type}" />
                    </div>
                </div>
            </form:form>

            <c:choose>
                <c:when test="${empty ratings}">
                    <div class="text-center py-8">
                        <p class="text-black/50"><c:out value="${ratingEmpty}"/></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="flex flex-col gap-4">
                        <c:forEach var="rating" items="${ratings}" varStatus="loop">
                            <div class="p-4 bg-white border border-black/10 rounded-lg">
                                <div class="flex flex-row gap-4 mb-2">
                                    <c:choose>
                                        <c:when test="${rating.type.name() == 'POSITIVE'}">
                                            <paw:icon name="arrow-up" classname="text-2xl text-lime-600 mt-1" />
                                        </c:when>
                                        <c:when test="${rating.type.name() == 'NEUTRAL'}">
                                            <paw:icon name="minus" classname="text-2xl text-stone-500 mt-1" />
                                        </c:when>
                                        <c:otherwise>
                                            <paw:icon name="arrow-down" classname="text-2xl text-red-600 mt-1" />
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="flex-1">
                                        <div class="flex flex-row gap-2 items-center mb-1">
                                            <span class="font-medium text-black"><c:out value="${rating.listing.title}"/></span>
                                            <c:choose>
                                                <c:when test="${rating.type.name() == 'POSITIVE'}">
                                                    <span class="text-xs text-lime-600 font-medium px-2 py-0.5 bg-lime-50 rounded"><c:out value="${ratingFilterPositive}"/></span>
                                                </c:when>
                                                <c:when test="${rating.type.name() == 'NEUTRAL'}">
                                                    <span class="text-xs text-stone-500 font-medium px-2 py-0.5 bg-stone-50 rounded"><c:out value="${ratingFilterNeutral}"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-xs text-red-600 font-medium px-2 py-0.5 bg-red-50 rounded"><c:out value="${ratingFilterNegative}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${rating.role.name() == 'SELLER'}">
                                                    <span class="text-xs text-black/50 px-2 py-0.5 bg-black/5 rounded"><c:out value="${ratingFilterSeller}"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-xs text-black/50 px-2 py-0.5 bg-black/5 rounded"><c:out value="${ratingFilterBuyer}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <p class="text-sm text-black/60 mt-1 whitespace-pre-wrap"><c:out value="${rating.reviewText}"/></p>
                                    </div>
                                    <div class="flex flex-col items-end">
                                        <paw:user user="${rating.creator}" variant="compact" />
                                        <span class="text-xs text-black/50">
                                            <spring:message code="profile.memberSince" arguments="${rating.createdAt.toEpochMilli()}"/>
                                        </span>
                                    </div>
                                </div>
                                <c:if test="${!loop.last}">
                                    <paw:divider />
                                </c:if>
                            </div>
                        </c:forEach>

                        <paw:pagination page="${ratingPage}" baseUrl="/profile/${user.id}"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </paw:card>

        <paw:card title="${listingsTitle}">
            <c:choose>
                <c:when test="${empty listings}">
                    <div class="mt-12 mb-12 flex flex-col items-center">
                        <p class="text-black/60 text-lg mb-4"><c:out value="${listingsEmpty}"/></p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="flex flex-col gap-4 mt-4">
                        <c:forEach var="listing" items="${listings}" varStatus="loop">
                            <c:url value="/listing/${listing.id}" var="listingUrl"/>
                            <div class="flex flex-col gap-2">
                                <div class="flex flex-col sm:flex-row sm:items-center gap-4">
                                    <div class="min-w-0">
                                        <h3 class="text-base font-semibold truncate">
                                            <a href="${listingUrl}" class="hover:text-lime-600 transition block truncate"><c:out value="${listing.title}"/></a>
                                        </h3>
                                    </div>
                                    <c:if test="${listing.pendingOffersCount > 0}">
                                        <paw:badge text="${listing.pendingOffersCount} offers" classname="text-red-600 ml-auto" />
                                    </c:if>
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
                                        <paw:linkButton variant="outline" href="${listingUrl}" text="${viewListingLabel}" />
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